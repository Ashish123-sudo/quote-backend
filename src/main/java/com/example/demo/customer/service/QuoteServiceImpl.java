package com.example.demo.quote.service;

import com.example.demo.quote.entity.QuoteDetail;
import com.example.demo.quote.entity.QuoteHeader;
import com.example.demo.quote.repository.QuoteDetailRepository;
import com.example.demo.quote.repository.QuoteHeaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class QuoteServiceImpl implements QuoteService {

    @Autowired
    private QuoteHeaderRepository quoteHeaderRepository;

    @Autowired
    private QuoteDetailRepository quoteDetailRepository;

    // =========================
    // GET METHODS
    // =========================

    @Override
    public List<QuoteHeader> getAllQuotes() {
        return quoteHeaderRepository.findAll();
    }

    @Override
    public Optional<QuoteHeader> getQuoteById(Long id) {
        return quoteHeaderRepository.findById(id);
    }

    @Override
    public Optional<QuoteHeader> getQuoteByRef(String quoteRef) {
        return quoteHeaderRepository.findByQuoteRef(quoteRef);
    }

    @Override
    public List<QuoteHeader> getQuotesByCustomerId(Integer customerId) {
        return quoteHeaderRepository.findByCustomerId(customerId);
    }

    // =========================
    // CREATE
    // =========================

    @Override
    public QuoteHeader createQuote(QuoteHeader quoteHeader) {

        // ✅ Backend owns the reference
        quoteHeader.setQuoteDate(LocalDate.now());
        quoteHeader.setQuoteRef(generateNextQuoteRef());

        recalculateTotals(quoteHeader);

        return quoteHeaderRepository.save(quoteHeader);
    }

    // =========================
    // UPDATE
    // =========================

    @Override
    public QuoteHeader updateQuote(Long id, QuoteHeader quoteHeader) {

        if (!quoteHeaderRepository.existsById(id)) {
            throw new RuntimeException("Quote not found with id: " + id);
        }

        quoteHeader.setQuoteId(id);

        recalculateTotals(quoteHeader);

        return quoteHeaderRepository.save(quoteHeader);
    }

    @Override
    public String generateNextQuoteRef() {

        LocalDate today = LocalDate.now();

        long countToday = quoteHeaderRepository.countByQuoteDate(today);

        long nextSerial = countToday + 1;

        String datePart = today.format(DateTimeFormatter.ofPattern("ddMMyy"));

        return String.format("Q-%s-%03d", datePart, nextSerial);
    }

    // =========================
    // DELETE FULL QUOTE
    // =========================

    @Override
    public void deleteQuote(Long id) {

        if (!quoteHeaderRepository.existsById(id)) {
            throw new RuntimeException("Quote not found with id: " + id);
        }

        quoteHeaderRepository.deleteById(id);
    }

    // =========================
    // DELETE SINGLE DETAIL
    // =========================

    @Override
    public void deleteQuoteDetail(Long slNo) {

        QuoteDetail detail = quoteDetailRepository.findById(slNo)
                .orElseThrow(() -> new RuntimeException("Quote detail not found"));

        Long headerId = detail.getQuoteHeader().getQuoteId();

        // Delete detail
        quoteDetailRepository.deleteById(slNo);

        // Recalculate totals
        QuoteHeader header = quoteHeaderRepository.findById(headerId)
                .orElseThrow(() -> new RuntimeException("Parent quote not found"));

        List<QuoteDetail> remainingDetails =
                quoteDetailRepository.findByQuoteHeader_QuoteId(headerId);

        int totalQty = remainingDetails.stream()
                .mapToInt(d -> d.getItemQuantity() != null ? d.getItemQuantity() : 0)
                .sum();

        double totalValue = remainingDetails.stream()
                .mapToDouble(d -> d.getItemValue() != null ? d.getItemValue() : 0.0)
                .sum();

        header.setTotalQuantity(totalQty);
        header.setTotalValue(totalValue);

        quoteHeaderRepository.save(header);
    }

    // =========================
    // ✅ ADD SINGLE DETAIL
    // =========================

    @Override
    public QuoteDetail addQuoteDetail(QuoteDetail quoteDetail) {

        // Get the quote header
        Long quoteId = quoteDetail.getQuoteId();
        QuoteHeader header = quoteHeaderRepository.findById(quoteId)
                .orElseThrow(() -> new RuntimeException("Quote not found with id: " + quoteId));

        // Set the relationship
        quoteDetail.setQuoteHeader(header);
        quoteDetail.setQuoteRef(header.getQuoteRef());

        // Calculate item value
        if (quoteDetail.getItemQuantity() != null && quoteDetail.getItemUnitRate() != null) {
            double itemValue = quoteDetail.getItemQuantity() * quoteDetail.getItemUnitRate();
            quoteDetail.setItemValue(itemValue);
        } else {
            quoteDetail.setItemValue(0.0);
        }

        // Save the detail
        QuoteDetail savedDetail = quoteDetailRepository.save(quoteDetail);

        // Recalculate header totals
        List<QuoteDetail> allDetails = quoteDetailRepository.findByQuoteHeader_QuoteId(quoteId);

        int totalQty = allDetails.stream()
                .mapToInt(d -> d.getItemQuantity() != null ? d.getItemQuantity() : 0)
                .sum();

        double totalValue = allDetails.stream()
                .mapToDouble(d -> d.getItemValue() != null ? d.getItemValue() : 0.0)
                .sum();

        header.setTotalQuantity(totalQty);
        header.setTotalValue(totalValue);

        quoteHeaderRepository.save(header);

        return savedDetail;
    }

    // =========================
    // ✅ UPDATE SINGLE DETAIL
    // =========================

    @Override
    public QuoteDetail updateQuoteDetail(Long slNo, QuoteDetail quoteDetail) {

        // Find existing detail
        QuoteDetail existingDetail = quoteDetailRepository.findById(slNo)
                .orElseThrow(() -> new RuntimeException("Quote detail not found with slNo: " + slNo));

        // Update fields
        existingDetail.setItemDesc(quoteDetail.getItemDesc());
        existingDetail.setItemUnitRate(quoteDetail.getItemUnitRate());
        existingDetail.setItemQuantity(quoteDetail.getItemQuantity());
        existingDetail.setItemValue(quoteDetail.getItemValue());

        // Save updated detail
        QuoteDetail updatedDetail = quoteDetailRepository.save(existingDetail);

        // Recalculate header totals
        Long headerId = existingDetail.getQuoteHeader().getQuoteId();
        QuoteHeader header = quoteHeaderRepository.findById(headerId)
                .orElseThrow(() -> new RuntimeException("Parent quote not found"));

        List<QuoteDetail> allDetails =
                quoteDetailRepository.findByQuoteHeader_QuoteId(headerId);

        int totalQty = allDetails.stream()
                .mapToInt(d -> d.getItemQuantity() != null ? d.getItemQuantity() : 0)
                .sum();

        double totalValue = allDetails.stream()
                .mapToDouble(d -> d.getItemValue() != null ? d.getItemValue() : 0.0)
                .sum();

        header.setTotalQuantity(totalQty);
        header.setTotalValue(totalValue);

        quoteHeaderRepository.save(header);

        return updatedDetail;
    }

    // =========================
    // PRIVATE HELPER
    // =========================

    private void recalculateTotals(QuoteHeader quoteHeader) {

        if (quoteHeader.getQuoteDetails() != null &&
                !quoteHeader.getQuoteDetails().isEmpty()) {

            int totalQty = quoteHeader.getQuoteDetails().stream()
                    .mapToInt(d -> d.getItemQuantity() != null ? d.getItemQuantity() : 0)
                    .sum();

            double totalValue = quoteHeader.getQuoteDetails().stream()
                    .mapToDouble(d -> d.getItemValue() != null ? d.getItemValue() : 0.0)
                    .sum();

            quoteHeader.setTotalQuantity(totalQty);
            quoteHeader.setTotalValue(totalValue);

            quoteHeader.getQuoteDetails().forEach(detail -> {
                detail.setQuoteHeader(quoteHeader);
                detail.setQuoteRef(quoteHeader.getQuoteRef());
            });

        } else {
            quoteHeader.setTotalQuantity(0);
            quoteHeader.setTotalValue(0.0);
        }
    }
}