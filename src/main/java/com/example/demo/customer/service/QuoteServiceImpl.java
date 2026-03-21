package com.example.demo.quote.service;

import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.repository.CustomerRepository;
import com.example.demo.quote.entity.QuoteDetail;
import com.example.demo.quote.entity.QuoteHeader;
import com.example.demo.quote.entity.QuoteTermsCondition;
import com.example.demo.quote.repository.QuoteDetailRepository;
import com.example.demo.quote.repository.QuoteHeaderRepository;
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

    @Autowired
    private QuoteHeaderRepository quoteHeaderRepository;

    @Autowired
    private QuoteDetailRepository quoteDetailRepository;

    @Autowired
    private QuoteTermsConditionRepository quoteTermsConditionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // =========================
    // GET METHODS
    // =========================

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

    // =========================
    // CREATE
    // =========================

    @Override
    public QuoteHeader createQuote(QuoteHeader quoteHeader, UUID orgId, UUID userId) {
        System.out.println("Incoming terms: " + quoteHeader.getIncomingTerms());

        // Set organization and audit fields
        quoteHeader.setOrgId(orgId);
        quoteHeader.setCreatedBy(userId);
        quoteHeader.setUpdatedBy(userId);
        quoteHeader.setQuoteDate(LocalDate.now());
        quoteHeader.setQuoteRef(generateNextQuoteRef(orgId));

        // Verify customer belongs to same organization
        if (quoteHeader.getCustomer() != null && quoteHeader.getCustomer().getCustomerId() != null) {
            Customer customer = customerRepository.findByCustomerIdAndOrgId(
                    quoteHeader.getCustomer().getCustomerId(), orgId
            ).orElseThrow(() -> new RuntimeException("Customer not found or belongs to different organization"));
            quoteHeader.setCustomer(customer);
        }

        // Recalculate totals
        recalculateTotals(quoteHeader);

        // Save the quote
        QuoteHeader savedQuote = quoteHeaderRepository.save(quoteHeader);

        // Save terms conditions
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

        // Delete existing terms
        List<QuoteTermsCondition> existingTerms = quoteTermsConditionRepository
                .findByQuoteHeader_QuoteIdAndOrgId(quoteId, orgId);
        quoteTermsConditionRepository.deleteAll(existingTerms);

        // Save new terms
        for (QuoteTermsCondition term : terms) {
            term.setOrgId(orgId);
            term.setQuoteHeader(header);
            term.setQuoteRef(header.getQuoteRef());
            term.setCreatedBy(userId);
            term.setUpdatedBy(userId);
        }
        quoteTermsConditionRepository.saveAll(terms);
    }

    // =========================
    // UPDATE
    // =========================

    @Override
    public QuoteHeader updateQuote(UUID quoteId, QuoteHeader quoteHeader, UUID orgId, UUID userId) {
        QuoteHeader existing = quoteHeaderRepository.findByQuoteIdAndOrgId(quoteId, orgId)
                .orElseThrow(() -> new RuntimeException("Quote not found with id: " + quoteId));

        // Update basic fields
        if (quoteHeader.getCustomer() != null && quoteHeader.getCustomer().getCustomerId() != null) {
            Customer customer = customerRepository.findByCustomerIdAndOrgId(
                    quoteHeader.getCustomer().getCustomerId(), orgId
            ).orElseThrow(() -> new RuntimeException("Customer not found or belongs to different organization"));
            existing.setCustomer(customer);
        }

        existing.setQuoteDate(quoteHeader.getQuoteDate());
        existing.setCurrency(quoteHeader.getCurrency());
        existing.setUpdatedBy(userId);

        // Reset approval status if quote was approved or rejected
        String currentStatus = existing.getApprovalStatus();
        if ("APPROVED".equals(currentStatus) || "REJECTED".equals(currentStatus)) {
            existing.setApprovalStatus("DRAFT");
            existing.setApprovedBy(null);
            existing.setRejectionReason(null);
        }

        // Recalculate totals from existing details in DB
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

    @Override
    public String generateNextQuoteRef(UUID orgId) {
        LocalDate today = LocalDate.now();
        long countToday = quoteHeaderRepository.countByQuoteDateAndOrgId(today, orgId);
        long nextSerial = countToday + 1;
        String datePart = today.format(DateTimeFormatter.ofPattern("ddMMyy"));
        return String.format("Q-%s-%03d", datePart, nextSerial);
    }

    // =========================
    // DELETE FULL QUOTE
    // =========================

    @Override
    public void deleteQuote(UUID quoteId, UUID orgId) {
        QuoteHeader quote = quoteHeaderRepository.findByQuoteIdAndOrgId(quoteId, orgId)
                .orElseThrow(() -> new RuntimeException("Quote not found with id: " + quoteId));

        quoteHeaderRepository.delete(quote);
    }

    // =========================
    // DELETE SINGLE DETAIL
    // =========================

    @Override
    public void deleteQuoteDetail(UUID slNo, UUID orgId) {
        QuoteDetail detail = quoteDetailRepository.findBySlNoAndOrgId(slNo, orgId)
                .orElseThrow(() -> new RuntimeException("Quote detail not found"));

        UUID headerId = detail.getQuoteHeader().getQuoteId();

        // Delete detail
        quoteDetailRepository.delete(detail);

        // Recalculate totals
        recalculateHeaderTotals(headerId, orgId, null);
    }

    // =========================
    // ✅ ADD SINGLE DETAIL
    // =========================

    @Override
    public QuoteDetail addQuoteDetail(QuoteDetail quoteDetail, UUID orgId, UUID userId) {
        // Get the quote header
        UUID quoteId = quoteDetail.getQuoteId();
        QuoteHeader header = quoteHeaderRepository.findByQuoteIdAndOrgId(quoteId, orgId)
                .orElseThrow(() -> new RuntimeException("Quote not found with id: " + quoteId));

        // Set organization and audit fields
        quoteDetail.setOrgId(orgId);
        quoteDetail.setQuoteHeader(header);
        quoteDetail.setQuoteRef(header.getQuoteRef());
        quoteDetail.setCreatedBy(userId);
        quoteDetail.setUpdatedBy(userId);

        // Calculate item value using BigDecimal
        if (quoteDetail.getItemQuantity() != null && quoteDetail.getItemUnitRate() != null) {
            BigDecimal quantity = new BigDecimal(quoteDetail.getItemQuantity());
            BigDecimal rate = quoteDetail.getItemUnitRate();
            BigDecimal discount = quoteDetail.getItemDiscount() != null ?
                    quoteDetail.getItemDiscount() : BigDecimal.ZERO;

            // Calculate: quantity * rate * (1 - discount/100)
            BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                    discount.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
            );

            BigDecimal itemValue = quantity.multiply(rate).multiply(discountMultiplier)
                    .setScale(2, RoundingMode.HALF_UP);

            quoteDetail.setItemValue(itemValue);
        } else {
            quoteDetail.setItemValue(BigDecimal.ZERO);
        }

        // Save the detail
        QuoteDetail savedDetail = quoteDetailRepository.save(quoteDetail);

        // Recalculate header totals
        recalculateHeaderTotals(quoteId, orgId, userId);

        return savedDetail;
    }

    // =========================
    // ✅ UPDATE SINGLE DETAIL
    // =========================

    @Override
    public QuoteDetail updateQuoteDetail(UUID slNo, QuoteDetail quoteDetail, UUID orgId, UUID userId) {
        // Find existing detail
        QuoteDetail existingDetail = quoteDetailRepository.findBySlNoAndOrgId(slNo, orgId)
                .orElseThrow(() -> new RuntimeException("Quote detail not found with slNo: " + slNo));

        // Update fields
        existingDetail.setItemDesc(quoteDetail.getItemDesc());
        existingDetail.setItemUnitRate(quoteDetail.getItemUnitRate());
        existingDetail.setItemQuantity(quoteDetail.getItemQuantity());
        existingDetail.setItemDiscount(quoteDetail.getItemDiscount());
        existingDetail.setUpdatedBy(userId);

        // Recalculate item value using BigDecimal
        if (quoteDetail.getItemQuantity() != null && quoteDetail.getItemUnitRate() != null) {
            BigDecimal quantity = new BigDecimal(quoteDetail.getItemQuantity());
            BigDecimal rate = quoteDetail.getItemUnitRate();
            BigDecimal discount = quoteDetail.getItemDiscount() != null ?
                    quoteDetail.getItemDiscount() : BigDecimal.ZERO;

            BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                    discount.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
            );

            BigDecimal itemValue = quantity.multiply(rate).multiply(discountMultiplier)
                    .setScale(2, RoundingMode.HALF_UP);

            existingDetail.setItemValue(itemValue);
        } else {
            existingDetail.setItemValue(BigDecimal.ZERO);
        }

        // Save updated detail
        QuoteDetail updatedDetail = quoteDetailRepository.save(existingDetail);

        // Recalculate header totals
        UUID headerId = existingDetail.getQuoteHeader().getQuoteId();
        recalculateHeaderTotals(headerId, orgId, userId);

        return updatedDetail;
    }

    // =========================
    // PRIVATE HELPERS
    // =========================

    private void recalculateTotals(QuoteHeader quoteHeader) {
        if (quoteHeader.getQuoteDetails() != null && !quoteHeader.getQuoteDetails().isEmpty()) {

            int totalQty = quoteHeader.getQuoteDetails().stream()
                    .mapToInt(d -> d.getItemQuantity() != null ? d.getItemQuantity() : 0)
                    .sum();

            BigDecimal totalValue = quoteHeader.getQuoteDetails().stream()
                    .map(d -> {
                        if (d.getItemQuantity() == null || d.getItemUnitRate() == null) {
                            return BigDecimal.ZERO;
                        }

                        BigDecimal qty = new BigDecimal(d.getItemQuantity());
                        BigDecimal rate = d.getItemUnitRate();
                        BigDecimal disc = d.getItemDiscount() != null ? d.getItemDiscount() : BigDecimal.ZERO;

                        BigDecimal discMultiplier = BigDecimal.ONE.subtract(
                                disc.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
                        );

                        return qty.multiply(rate).multiply(discMultiplier);
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);

            quoteHeader.setTotalQuantity(totalQty);
            quoteHeader.setTotalValue(totalValue);

            // Set org_id and relationships for all details
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

        if (userId != null) {
            header.setUpdatedBy(userId);
        }

        quoteHeaderRepository.save(header);
    }
}