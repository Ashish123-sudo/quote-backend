package com.example.demo.quote.service;

import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.entity.QuoteRefConfig;
import com.example.demo.customer.repository.CustomerRepository;
import com.example.demo.customer.repository.QuoteRefConfigRepository;
import com.example.demo.quote.entity.QuoteDetail;
import com.example.demo.quote.entity.QuoteHeader;
import com.example.demo.quote.entity.QuoteTermsCondition;
import com.example.demo.quote.repository.QuoteDetailRepository;
import com.example.demo.quote.repository.QuoteHeaderRepository;
import com.example.demo.quote.repository.QuoteTermsConditionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class QuoteServiceImpl implements QuoteService {

    // REMOVE all 5 @Autowired fields, REPLACE with:
    private final QuoteHeaderRepository quoteHeaderRepository;
    private final QuoteDetailRepository quoteDetailRepository;
    private final QuoteTermsConditionRepository quoteTermsConditionRepository;
    private final CustomerRepository customerRepository;
    private final QuoteRefConfigRepository quoteRefConfigRepository;

    public QuoteServiceImpl(QuoteHeaderRepository quoteHeaderRepository,
                            QuoteDetailRepository quoteDetailRepository,
                            QuoteTermsConditionRepository quoteTermsConditionRepository,
                            CustomerRepository customerRepository,
                            QuoteRefConfigRepository quoteRefConfigRepository) {
        this.quoteHeaderRepository = quoteHeaderRepository;
        this.quoteDetailRepository = quoteDetailRepository;
        this.quoteTermsConditionRepository = quoteTermsConditionRepository;
        this.customerRepository = customerRepository;
        this.quoteRefConfigRepository = quoteRefConfigRepository;
    }

    // ── Queries ───────────────────────────────────────────────────────

    @Override
    public List<QuoteHeader> getAllQuotes(UUID orgId) {
        List<QuoteHeader> quotes = quoteHeaderRepository.findByOrgIdWithCustomer(orgId);

        List<QuoteHeader> quotesWithDetails = quoteHeaderRepository.findByOrgIdWithDetails(orgId);
        List<QuoteHeader> quotesWithTerms = quoteHeaderRepository.findByOrgIdWithTerms(orgId);

        Map<UUID, QuoteHeader> detailsMap = quotesWithDetails.stream()
                .collect(java.util.stream.Collectors.toMap(QuoteHeader::getQuoteId, q -> q));

        Map<UUID, QuoteHeader> termsMap = quotesWithTerms.stream()
                .collect(java.util.stream.Collectors.toMap(QuoteHeader::getQuoteId, q -> q));

        quotes.forEach(q -> {
            QuoteHeader withDetails = detailsMap.get(q.getQuoteId());
            if (withDetails != null) q.setQuoteDetails(withDetails.getQuoteDetails());

            QuoteHeader withTerms = termsMap.get(q.getQuoteId());
            if (withTerms != null) q.setQuoteTermsConditions(withTerms.getQuoteTermsConditions());
        });

        return quotes;
    }

    @Override
    public Optional<QuoteHeader> getQuoteById(UUID quoteId, UUID orgId) {
        Optional<QuoteHeader> quote = quoteHeaderRepository.findByIdWithCustomer(quoteId, orgId);

        quote.ifPresent(q -> {
            quoteHeaderRepository.findByIdWithDetails(quoteId, orgId)
                    .ifPresent(qWithDetails ->
                            q.setQuoteDetails(qWithDetails.getQuoteDetails()));

            quoteHeaderRepository.findByIdWithTerms(quoteId, orgId)
                    .ifPresent(qWithTerms ->
                            q.setQuoteTermsConditions(qWithTerms.getQuoteTermsConditions()));
        });

        return quote;
    }

    @Override
    public Optional<QuoteHeader> getQuoteByRef(String quoteRef, UUID orgId) {
        Optional<QuoteHeader> quote = quoteHeaderRepository.findByQuoteRefAndOrgIdWithCustomer(quoteRef, orgId);

        quote.ifPresent(q -> {
            quoteHeaderRepository.findByIdWithDetails(q.getQuoteId(), orgId)
                    .ifPresent(qWithDetails ->
                            q.setQuoteDetails(qWithDetails.getQuoteDetails()));

            quoteHeaderRepository.findByIdWithTerms(q.getQuoteId(), orgId)
                    .ifPresent(qWithTerms ->
                            q.setQuoteTermsConditions(qWithTerms.getQuoteTermsConditions()));
        });

        return quote;
    }

    @Override
    public List<QuoteHeader> getQuotesByCustomerId(UUID customerId, UUID orgId) {
        List<QuoteHeader> quotes = quoteHeaderRepository.findByCustomerIdAndOrgIdWithCustomer(customerId, orgId);

        List<QuoteHeader> quotesWithDetails = quoteHeaderRepository.findByOrgIdWithDetails(orgId);
        List<QuoteHeader> quotesWithTerms = quoteHeaderRepository.findByOrgIdWithTerms(orgId);

        Map<UUID, QuoteHeader> detailsMap = quotesWithDetails.stream()
                .collect(java.util.stream.Collectors.toMap(QuoteHeader::getQuoteId, q -> q));

        Map<UUID, QuoteHeader> termsMap = quotesWithTerms.stream()
                .collect(java.util.stream.Collectors.toMap(QuoteHeader::getQuoteId, q -> q));

        quotes.forEach(q -> {
            QuoteHeader withDetails = detailsMap.get(q.getQuoteId());
            if (withDetails != null) q.setQuoteDetails(withDetails.getQuoteDetails());

            QuoteHeader withTerms = termsMap.get(q.getQuoteId());
            if (withTerms != null) q.setQuoteTermsConditions(withTerms.getQuoteTermsConditions());
        });

        return quotes;
    }

    // ── Create ────────────────────────────────────────────────────────

    @Override
    public QuoteHeader createQuote(QuoteHeader quoteHeader, UUID orgId, UUID userId) {

        quoteHeader.setOrgId(orgId);
        quoteHeader.setCreatedBy(userId);
        quoteHeader.setUpdatedBy(userId);
        quoteHeader.setQuoteDate(LocalDate.now());
        quoteHeader.setQuoteRef(generateNextQuoteRef(orgId));

        // ✅ Resolve customer
        if (quoteHeader.getCustomer() != null
                && quoteHeader.getCustomer().getCustomerId() != null) {
            Customer customer = customerRepository
                    .findByCustomerIdAndOrgId(quoteHeader.getCustomer().getCustomerId(), orgId)
                    .orElseThrow(() -> new RuntimeException(
                            "Customer not found or belongs to different organization"));
            quoteHeader.setCustomer(customer);
        }

        // ✅ Use shared helper for totals
        recalculateTotals(quoteHeader);

        QuoteHeader savedQuote = quoteHeaderRepository.save(quoteHeader);

        // ✅ Save incoming terms
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

    // ── Update ────────────────────────────────────────────────────────

    @Override
    public QuoteHeader updateQuote(UUID quoteId, QuoteHeader quoteHeader,
                                   UUID orgId, UUID userId) {
        QuoteHeader existing = quoteHeaderRepository.findByQuoteIdAndOrgId(quoteId, orgId)
                .orElseThrow(() -> new RuntimeException(
                        "Quote not found with id: " + quoteId));

        // ✅ Resolve customer if provided
        if (quoteHeader.getCustomer() != null
                && quoteHeader.getCustomer().getCustomerId() != null) {
            Customer customer = customerRepository
                    .findByCustomerIdAndOrgId(quoteHeader.getCustomer().getCustomerId(), orgId)
                    .orElseThrow(() -> new RuntimeException(
                            "Customer not found or belongs to different organization"));
            existing.setCustomer(customer);
        }

        existing.setQuoteDate(quoteHeader.getQuoteDate());
        existing.setCurrency(quoteHeader.getCurrency());
        existing.setUpdatedBy(userId);

        // ✅ Reset approval status if previously approved/rejected
        String currentStatus = existing.getApprovalStatus();
        if ("APPROVED".equals(currentStatus) || "REJECTED".equals(currentStatus)) {
            existing.setApprovalStatus("DRAFT");
            existing.setApprovedBy(null);
            existing.setRejectionReason(null);
        }

        // ✅ Recalculate totals from DB — not from LAZY collection
        List<QuoteDetail> allDetails = quoteDetailRepository
                .findByQuoteHeader_QuoteIdAndOrgId(quoteId, orgId);

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

    @Override
    public void updateQuoteTerms(UUID quoteId, List<QuoteTermsCondition> terms,
                                 UUID orgId, UUID userId) {
        QuoteHeader header = quoteHeaderRepository.findByIdWithTerms(quoteId, orgId)
                .orElseThrow(() -> new RuntimeException(
                        "Quote not found with id: " + quoteId));

        // ✅ Delete existing then flush before re-inserting
        List<QuoteTermsCondition> existingTerms = quoteTermsConditionRepository
                .findByQuoteHeader_QuoteIdAndOrgId(quoteId, orgId);
        quoteTermsConditionRepository.deleteAll(existingTerms);
        quoteTermsConditionRepository.flush(); // ✅ prevents constraint violation on re-insert

        for (QuoteTermsCondition term : terms) {
            term.setOrgId(orgId);
            term.setQuoteHeader(header);
            term.setQuoteRef(header.getQuoteRef());
            term.setCreatedBy(userId);
            term.setUpdatedBy(userId);
        }
        quoteTermsConditionRepository.saveAll(terms);
    }

    // ── Delete ────────────────────────────────────────────────────────

    @Override
    public void deleteQuote(UUID quoteId, UUID orgId) {
        QuoteHeader quote = quoteHeaderRepository.findByQuoteIdAndOrgId(quoteId, orgId)
                .orElseThrow(() -> new RuntimeException(
                        "Quote not found with id: " + quoteId));
        quoteHeaderRepository.delete(quote);
    }

    // ── Quote Details ─────────────────────────────────────────────────

    @Override
    public QuoteDetail addQuoteDetail(QuoteDetail quoteDetail, UUID orgId, UUID userId) {
        UUID quoteId = quoteDetail.getQuoteId();
        QuoteHeader header = quoteHeaderRepository.findByQuoteIdAndOrgId(quoteId, orgId)
                .orElseThrow(() -> new RuntimeException(
                        "Quote not found with id: " + quoteId));

        quoteDetail.setOrgId(orgId);
        quoteDetail.setQuoteHeader(header);
        quoteDetail.setQuoteRef(header.getQuoteRef());
        quoteDetail.setCreatedBy(userId);
        quoteDetail.setUpdatedBy(userId);

        // ✅ use shared helper
        quoteDetail.setItemValue(calculateItemValue(
                quoteDetail.getItemQuantity(),
                quoteDetail.getItemUnitRate(),
                quoteDetail.getItemDiscount()
        ));

        QuoteDetail savedDetail = quoteDetailRepository.save(quoteDetail);
        recalculateHeaderTotals(quoteId, orgId, userId);
        return savedDetail;
    }

    @Override
    public QuoteDetail updateQuoteDetail(UUID slNo, QuoteDetail quoteDetail,
                                         UUID orgId, UUID userId) {
        QuoteDetail existingDetail = quoteDetailRepository.findBySlNoAndOrgId(slNo, orgId)
                .orElseThrow(() -> new RuntimeException(
                        "Quote detail not found with slNo: " + slNo));

        // ✅ get headerId before any changes — avoids LAZY access after save
        UUID headerId = quoteDetailRepository.findQuoteIdBySlNo(slNo);

        existingDetail.setItemDesc(quoteDetail.getItemDesc());
        existingDetail.setItemUnitRate(quoteDetail.getItemUnitRate());
        existingDetail.setItemQuantity(quoteDetail.getItemQuantity());
        existingDetail.setItemDiscount(quoteDetail.getItemDiscount());
        existingDetail.setUpdatedBy(userId);

        // ✅ use shared helper
        existingDetail.setItemValue(calculateItemValue(
                quoteDetail.getItemQuantity(),
                quoteDetail.getItemUnitRate(),
                quoteDetail.getItemDiscount()
        ));

        QuoteDetail updatedDetail = quoteDetailRepository.save(existingDetail);
        recalculateHeaderTotals(headerId, orgId, userId);
        return updatedDetail;
    }

    @Override
    public void deleteQuoteDetail(UUID slNo, UUID orgId) {
        QuoteDetail detail = quoteDetailRepository.findBySlNoAndOrgId(slNo, orgId)
                .orElseThrow(() -> new RuntimeException("Quote detail not found"));

        // ✅ fetch headerId via repo query — no LAZY relationship access
        UUID headerId = quoteDetailRepository.findQuoteIdBySlNo(slNo);

        quoteDetailRepository.delete(detail);
        recalculateHeaderTotals(headerId, orgId, null);
    }

    // ── Quote Reference Generation ────────────────────────────────────

    @Override
    public String generateNextQuoteRef(UUID orgId) {
        LocalDate today = LocalDate.now();
        QuoteRefConfig config = getOrCreateConfig(orgId, today);

        boolean shouldReset = shouldResetSerial(config, today);
        if (shouldReset) {
            config.setLastSerial(0);
            config.setLastResetDate(today);
        }

        int nextSerial = config.getLastSerial() + 1;
        config.setLastSerial(nextSerial);
        quoteRefConfigRepository.save(config);

        return buildRef(config, nextSerial, today);
    }

    @Override
    public String peekNextQuoteRef(UUID orgId) {
        LocalDate today = LocalDate.now();
        QuoteRefConfig config = getOrCreateConfig(orgId, today);

        boolean shouldReset = shouldResetSerial(config, today);
        int nextSerial = (shouldReset ? 0 : config.getLastSerial()) + 1;

        return buildRef(config, nextSerial, today);
    }

    // ── Private Helpers ───────────────────────────────────────────────

    // ✅ Extracted reset logic — shared by generate and peek
    private boolean shouldResetSerial(QuoteRefConfig config, LocalDate today) {
        LocalDate lastReset = config.getLastResetDate();
        return switch (config.getResetType()) {
            case "MONTHLY" -> lastReset == null
                    || lastReset.getMonth() != today.getMonth()
                    || lastReset.getYear() != today.getYear();
            case "YEARLY" -> lastReset == null
                    || lastReset.getYear() != today.getYear();
            case "AT_VALUE" -> config.getResetAtValue() != null
                    && config.getLastSerial() >= config.getResetAtValue();
            default -> false;
        };
    }

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
        return format
                .replace("yyyy", String.valueOf(date.getYear()))
                .replace("mmm",  date.getMonth().getDisplayName(
                        java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH))
                .replace("mm",   String.format("%02d", date.getMonthValue()))
                .replace("dd",   String.format("%02d", date.getDayOfMonth()))
                .replace("yy",   String.valueOf(date.getYear()).substring(2));
    }

    // ✅ Shared item value calculator — eliminates duplication
    private BigDecimal calculateItemValue(Integer quantity, BigDecimal unitRate,
                                          BigDecimal discount) {
        if (quantity == null || unitRate == null) return BigDecimal.ZERO;
        BigDecimal qty = new BigDecimal(quantity);
        BigDecimal disc = discount != null ? discount : BigDecimal.ZERO;
        BigDecimal discMultiplier = BigDecimal.ONE.subtract(
                disc.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
        return qty.multiply(unitRate).multiply(discMultiplier)
                .setScale(2, RoundingMode.HALF_UP);
    }

    // ✅ Used by recalculateTotals on new quote creation
    private void recalculateTotals(QuoteHeader quoteHeader) {
        if (quoteHeader.getQuoteDetails() == null
                || quoteHeader.getQuoteDetails().isEmpty()) {
            quoteHeader.setTotalQuantity(0);
            quoteHeader.setTotalValue(BigDecimal.ZERO);
            return;
        }

        int totalQty = 0;
        BigDecimal totalValue = BigDecimal.ZERO;

        for (QuoteDetail detail : quoteHeader.getQuoteDetails()) {
            detail.setOrgId(quoteHeader.getOrgId());
            detail.setQuoteHeader(quoteHeader);
            detail.setQuoteRef(quoteHeader.getQuoteRef());
            detail.setCreatedBy(quoteHeader.getCreatedBy());
            detail.setUpdatedBy(quoteHeader.getUpdatedBy());

            // ✅ use shared helper
            BigDecimal itemValue = calculateItemValue(
                    detail.getItemQuantity(),
                    detail.getItemUnitRate(),
                    detail.getItemDiscount()
            );
            detail.setItemValue(itemValue);

            totalQty += detail.getItemQuantity() != null ? detail.getItemQuantity() : 0;
            totalValue = totalValue.add(itemValue);
        }

        quoteHeader.setTotalQuantity(totalQty);
        quoteHeader.setTotalValue(totalValue.setScale(2, RoundingMode.HALF_UP));
    }

    // ✅ Used after add/update/delete of individual line items
    private void recalculateHeaderTotals(UUID quoteId, UUID orgId, UUID userId) {
        QuoteHeader header = quoteHeaderRepository.findByQuoteIdAndOrgId(quoteId, orgId)
                .orElseThrow(() -> new RuntimeException("Quote not found"));

        List<QuoteDetail> allDetails = quoteDetailRepository
                .findByQuoteHeader_QuoteIdAndOrgId(quoteId, orgId);

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