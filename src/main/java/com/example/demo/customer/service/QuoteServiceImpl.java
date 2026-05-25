package com.example.demo.quote.service;

import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.repository.CustomerRepository;
import com.example.demo.quote.entity.QuoteDetail;
import com.example.demo.quote.entity.QuoteHeader;
import com.example.demo.customer.entity.QuoteRefConfig;
import com.example.demo.quote.entity.QuoteTermsCondition;
import com.example.demo.quote.repository.QuoteDetailRepository;
import com.example.demo.quote.repository.QuoteHeaderRepository;
import com.example.demo.customer.repository.QuoteRefConfigRepository;
import com.example.demo.quote.repository.QuoteTermsConditionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class QuoteServiceImpl implements QuoteService {

    @Autowired private QuoteHeaderRepository quoteHeaderRepository;
    @Autowired private QuoteDetailRepository quoteDetailRepository;
    @Autowired private QuoteTermsConditionRepository quoteTermsConditionRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private QuoteRefConfigRepository quoteRefConfigRepository;

    @Override
    public List<QuoteHeader> getAllQuotes(UUID orgId) {
        return quoteHeaderRepository.findByOrgId(orgId);
    }

    @Override
    public Optional<QuoteHeader> getQuoteById(UUID quoteId, UUID orgId) {
        return quoteHeaderRepository.findByQuoteIdAndOrgId(quoteId, orgId);
    }

    @Override
    public Optional<QuoteHeader> getQuoteByRef(String quoteRef, UUID orgId) {
        return quoteHeaderRepository.findByQuoteRefAndOrgId(quoteRef, orgId);
    }

    @Override
    public List<QuoteHeader> getQuotesByCustomerId(UUID customerId, UUID orgId) {
        return quoteHeaderRepository.findByCustomer_CustomerIdAndOrgId(customerId, orgId);
    }

    @Override
    public QuoteHeader createQuote(QuoteHeader quoteHeader, UUID orgId, UUID userId) {
        System.out.println("Incoming terms: " + quoteHeader.getIncomingTerms());

        quoteHeader.setOrgId(orgId);
        quoteHeader.setCreatedBy(userId);
        quoteHeader.setUpdatedBy(userId);
        quoteHeader.setQuoteDate(LocalDate.now());
        quoteHeader.setQuoteRef(generateNextQuoteRef(orgId)); // increments here only

        if (quoteHeader.getCustomer() != null && quoteHeader.getCustomer().getCustomerId() != null) {
            Customer customer = customerRepository.findByCustomerIdAndOrgId(
                    quoteHeader.getCustomer().getCustomerId(), orgId
            ).orElseThrow(() -> new RuntimeException("Customer not found or belongs to different organization"));
            quoteHeader.setCustomer(customer);
        }

        recalculateTotals(quoteHeader);

        QuoteHeader savedQuote = quoteHeaderRepository.save(quoteHeader);

        if (quoteHeader.getIncomingTerms() != null) {
            for (QuoteTermsCondition term : quoteHeader.getIncomingTerms()) {
                term.setOrgId(orgId);
                term.setQuoteHeader(savedQuote);
                term.setQuoteRef(savedQuote.getQuoteRef());
                term.setCreatedBy(userId);
                term.setUpdatedBy(userId);
            }
            quoteTermsConditionRepository.saveAll(quoteHeader.getIncomingTerms());
        }

        return savedQuote;
    }

    @Override
    public void updateQuoteTerms(UUID quoteId, List<QuoteTermsCondition> terms, UUID orgId, UUID userId) {
        QuoteHeader header = quoteHeaderRepository.findByQuoteIdAndOrgId(quoteId, orgId)
                .orElseThrow(() -> new RuntimeException("Quote not found with id: " + quoteId));

        List<QuoteTermsCondition> existingTerms = quoteTermsConditionRepository
                .findByQuoteHeader_QuoteIdAndOrgId(quoteId, orgId);
        quoteTermsConditionRepository.deleteAll(existingTerms);

        for (QuoteTermsCondition term : terms) {
            term.setOrgId(orgId);
            term.setQuoteHeader(header);
            term.setQuoteRef(header.getQuoteRef());
            term.setCreatedBy(userId);
            term.setUpdatedBy(userId);
        }
        quoteTermsConditionRepository.saveAll(terms);
    }

    @Override
    public QuoteHeader updateQuote(UUID quoteId, QuoteHeader quoteHeader, UUID orgId, UUID userId) {
        QuoteHeader existing = quoteHeaderRepository.findByQuoteIdAndOrgId(quoteId, orgId)
                .orElseThrow(() -> new RuntimeException("Quote not found with id: " + quoteId));

        if (quoteHeader.getCustomer() != null && quoteHeader.getCustomer().getCustomerId() != null) {
            Customer customer = customerRepository.findByCustomerIdAndOrgId(
                    quoteHeader.getCustomer().getCustomerId(), orgId
            ).orElseThrow(() -> new RuntimeException("Customer not found or belongs to different organization"));
            existing.setCustomer(customer);
        }

        existing.setQuoteDate(quoteHeader.getQuoteDate());
        existing.setCurrency(quoteHeader.getCurrency());
        existing.setUpdatedBy(userId);

        String currentStatus = existing.getApprovalStatus();
        if ("APPROVED".equals(currentStatus) || "REJECTED".equals(currentStatus)) {
            existing.setApprovalStatus("DRAFT");
            existing.setApprovedBy(null);
            existing.setRejectionReason(null);
        }

        List<QuoteDetail> allDetails = quoteDetailRepository.findByQuoteHeader_QuoteIdAndOrgId(quoteId, orgId);

        int totalQty = allDetails.stream()
                .mapToInt(d -> d.getItemQuantity() != null ? d.getItemQuantity() : 0)
                .sum();

        BigDecimal totalValue = allDetails.stream()
                .map(d -> d.getItemValue() != null ? d.getItemValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        existing.setTotalQuantity(totalQty);
        existing.setTotalValue(totalValue);

        return quoteHeaderRepository.save(existing);
    }

    // ── Quote Reference Generation ────────────────────────────────────

    @Override
    public String generateNextQuoteRef(UUID orgId) {
        LocalDate today = LocalDate.now();
        QuoteRefConfig config = getOrCreateConfig(orgId, today);

        // Handle serial reset
        boolean shouldReset = false;
        LocalDate lastReset = config.getLastResetDate();
        if ("MONTHLY".equals(config.getResetType())) {
            shouldReset = lastReset == null
                    || lastReset.getMonth() != today.getMonth()
                    || lastReset.getYear() != today.getYear();
        } else if ("YEARLY".equals(config.getResetType())) {
            shouldReset = lastReset == null || lastReset.getYear() != today.getYear();
        } else if ("AT_VALUE".equals(config.getResetType()) && config.getResetAtValue() != null) {
            shouldReset = config.getLastSerial() >= config.getResetAtValue();
        }

        if (shouldReset) {
            config.setLastSerial(0);
            config.setLastResetDate(today);
        }

        // Increment and persist
        int nextSerial = config.getLastSerial() + 1;
        config.setLastSerial(nextSerial);
        quoteRefConfigRepository.save(config);

        return buildRef(config, nextSerial, today);
    }

    @Override
    public String peekNextQuoteRef(UUID orgId) {
        LocalDate today = LocalDate.now();
        QuoteRefConfig config = getOrCreateConfig(orgId, today);

        // Calculate what the next serial would be — no save
        boolean shouldReset = false;
        LocalDate lastReset = config.getLastResetDate();
        if ("MONTHLY".equals(config.getResetType())) {
            shouldReset = lastReset == null
                    || lastReset.getMonth() != today.getMonth()
                    || lastReset.getYear() != today.getYear();
        } else if ("YEARLY".equals(config.getResetType())) {
            shouldReset = lastReset == null || lastReset.getYear() != today.getYear();
        } else if ("AT_VALUE".equals(config.getResetType()) && config.getResetAtValue() != null) {
            shouldReset = config.getLastSerial() >= config.getResetAtValue();
        }

        int nextSerial = (shouldReset ? 0 : config.getLastSerial()) + 1;

        return buildRef(config, nextSerial, today);
    }

    // ── Private Helpers ───────────────────────────────────────────────

    private QuoteRefConfig getOrCreateConfig(UUID orgId, LocalDate today) {
        return quoteRefConfigRepository.findByOrgId(orgId)
                .orElseGet(() -> {
                    QuoteRefConfig def = new QuoteRefConfig();
                    def.setOrgId(orgId);
                    def.setLastSerial(0);
                    def.setLastResetDate(today);
                    return quoteRefConfigRepository.save(def);
                });
    }

    private String buildRef(QuoteRefConfig config, int serial, LocalDate date) {
        String datePart = buildDatePart(config.getDateFormat(), date);
        String serialPart = String.format("%0" + config.getSerialDigits() + "d", serial);
        String sep = config.getSeparator() != null ? config.getSeparator() : "-";

        StringBuilder ref = new StringBuilder();
        ref.append(config.getPrefix() != null ? config.getPrefix() : "Q");
        if (!datePart.isEmpty()) ref.append(sep).append(datePart);
        ref.append(sep).append(serialPart);
        if (config.getSuffix() != null && !config.getSuffix().isBlank()) {
            ref.append(sep).append(config.getSuffix());
        }
        return ref.toString();
    }

    private String buildDatePart(String format, LocalDate date) {
        if (format == null || format.isBlank() || "NONE".equals(format)) return "";

        // Support both preset formats and free-form patterns like "ddMMyy", "mmm-yyyy"
        return format
                .replace("yyyy", String.valueOf(date.getYear()))
                .replace("mmm",  date.getMonth().getDisplayName(
                        java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH))
                .replace("mm",   String.format("%02d", date.getMonthValue()))
                .replace("dd",   String.format("%02d", date.getDayOfMonth()))
                .replace("yy",   String.valueOf(date.getYear()).substring(2));
    }

    @Override
    public void deleteQuote(UUID quoteId, UUID orgId) {
        QuoteHeader quote = quoteHeaderRepository.findByQuoteIdAndOrgId(quoteId, orgId)
                .orElseThrow(() -> new RuntimeException("Quote not found with id: " + quoteId));
        quoteHeaderRepository.delete(quote);
    }

    @Override
    public void deleteQuoteDetail(UUID slNo, UUID orgId) {
        QuoteDetail detail = quoteDetailRepository.findBySlNoAndOrgId(slNo, orgId)
                .orElseThrow(() -> new RuntimeException("Quote detail not found"));
        UUID headerId = detail.getQuoteHeader().getQuoteId();
        quoteDetailRepository.delete(detail);
        recalculateHeaderTotals(headerId, orgId, null);
    }

    @Override
    public QuoteDetail addQuoteDetail(QuoteDetail quoteDetail, UUID orgId, UUID userId) {
        UUID quoteId = quoteDetail.getQuoteId();
        QuoteHeader header = quoteHeaderRepository.findByQuoteIdAndOrgId(quoteId, orgId)
                .orElseThrow(() -> new RuntimeException("Quote not found with id: " + quoteId));

        quoteDetail.setOrgId(orgId);
        quoteDetail.setQuoteHeader(header);
        quoteDetail.setQuoteRef(header.getQuoteRef());
        quoteDetail.setCreatedBy(userId);
        quoteDetail.setUpdatedBy(userId);

        if (quoteDetail.getItemQuantity() != null && quoteDetail.getItemUnitRate() != null) {
            BigDecimal quantity = new BigDecimal(quoteDetail.getItemQuantity());
            BigDecimal rate = quoteDetail.getItemUnitRate();
            BigDecimal discount = quoteDetail.getItemDiscount() != null ?
                    quoteDetail.getItemDiscount() : BigDecimal.ZERO;
            BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                    discount.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
            quoteDetail.setItemValue(quantity.multiply(rate).multiply(discountMultiplier)
                    .setScale(2, RoundingMode.HALF_UP));
        } else {
            quoteDetail.setItemValue(BigDecimal.ZERO);
        }

        QuoteDetail savedDetail = quoteDetailRepository.save(quoteDetail);
        recalculateHeaderTotals(quoteId, orgId, userId);
        return savedDetail;
    }

    @Override
    public QuoteDetail updateQuoteDetail(UUID slNo, QuoteDetail quoteDetail, UUID orgId, UUID userId) {
        QuoteDetail existingDetail = quoteDetailRepository.findBySlNoAndOrgId(slNo, orgId)
                .orElseThrow(() -> new RuntimeException("Quote detail not found with slNo: " + slNo));

        existingDetail.setItemDesc(quoteDetail.getItemDesc());
        existingDetail.setItemUnitRate(quoteDetail.getItemUnitRate());
        existingDetail.setItemQuantity(quoteDetail.getItemQuantity());
        existingDetail.setItemDiscount(quoteDetail.getItemDiscount());
        existingDetail.setUpdatedBy(userId);

        if (quoteDetail.getItemQuantity() != null && quoteDetail.getItemUnitRate() != null) {
            BigDecimal quantity = new BigDecimal(quoteDetail.getItemQuantity());
            BigDecimal rate = quoteDetail.getItemUnitRate();
            BigDecimal discount = quoteDetail.getItemDiscount() != null ?
                    quoteDetail.getItemDiscount() : BigDecimal.ZERO;
            BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                    discount.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
            existingDetail.setItemValue(quantity.multiply(rate).multiply(discountMultiplier)
                    .setScale(2, RoundingMode.HALF_UP));
        } else {
            existingDetail.setItemValue(BigDecimal.ZERO);
        }

        QuoteDetail updatedDetail = quoteDetailRepository.save(existingDetail);
        recalculateHeaderTotals(existingDetail.getQuoteHeader().getQuoteId(), orgId, userId);
        return updatedDetail;
    }

    private void recalculateTotals(QuoteHeader quoteHeader) {
        if (quoteHeader.getQuoteDetails() != null && !quoteHeader.getQuoteDetails().isEmpty()) {
            int totalQty = quoteHeader.getQuoteDetails().stream()
                    .mapToInt(d -> d.getItemQuantity() != null ? d.getItemQuantity() : 0)
                    .sum();

            BigDecimal totalValue = quoteHeader.getQuoteDetails().stream()
                    .map(d -> {
                        if (d.getItemQuantity() == null || d.getItemUnitRate() == null) return BigDecimal.ZERO;
                        BigDecimal qty = new BigDecimal(d.getItemQuantity());
                        BigDecimal rate = d.getItemUnitRate();
                        BigDecimal disc = d.getItemDiscount() != null ? d.getItemDiscount() : BigDecimal.ZERO;
                        BigDecimal discMultiplier = BigDecimal.ONE.subtract(
                                disc.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
                        return qty.multiply(rate).multiply(discMultiplier);
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);

            quoteHeader.setTotalQuantity(totalQty);
            quoteHeader.setTotalValue(totalValue);

            quoteHeader.getQuoteDetails().forEach(detail -> {
                detail.setOrgId(quoteHeader.getOrgId());
                detail.setQuoteHeader(quoteHeader);
                detail.setQuoteRef(quoteHeader.getQuoteRef());
                detail.setCreatedBy(quoteHeader.getCreatedBy());
                detail.setUpdatedBy(quoteHeader.getUpdatedBy());
            });
        } else {
            quoteHeader.setTotalQuantity(0);
            quoteHeader.setTotalValue(BigDecimal.ZERO);
        }
    }

    private void recalculateHeaderTotals(UUID quoteId, UUID orgId, UUID userId) {
        QuoteHeader header = quoteHeaderRepository.findByQuoteIdAndOrgId(quoteId, orgId)
                .orElseThrow(() -> new RuntimeException("Quote not found"));

        List<QuoteDetail> allDetails = quoteDetailRepository.findByQuoteHeader_QuoteIdAndOrgId(quoteId, orgId);

        int totalQty = allDetails.stream()
                .mapToInt(d -> d.getItemQuantity() != null ? d.getItemQuantity() : 0)
                .sum();

        BigDecimal totalValue = allDetails.stream()
                .map(d -> d.getItemValue() != null ? d.getItemValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        header.setTotalQuantity(totalQty);
        header.setTotalValue(totalValue);
        if (userId != null) header.setUpdatedBy(userId);
        quoteHeaderRepository.save(header);
    }
}