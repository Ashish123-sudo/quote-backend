package com.example.demo.quote.controller;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.config.BrevoEmailService;
import com.example.demo.config.SecurityHelper;
import com.example.demo.customer.entity.AppUser;
import com.example.demo.customer.repository.AppRoleRepository;
import com.example.demo.customer.repository.AppUserRepository;
import com.example.demo.quote.entity.QuoteDetail;
import com.example.demo.quote.entity.QuoteHeader;
import com.example.demo.quote.entity.QuoteTermsCondition;
import com.example.demo.quote.repository.QuoteHeaderRepository;
import com.example.demo.quote.service.QuoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/quotes")
@CrossOrigin(origins = {
        "https://699db1e4064fec9991497b90--sprightly-vacherin-2274ac.netlify.app",
        "http://localhost:4200"
})
public class QuoteController {

    private final QuoteService quoteService;
    private final QuoteHeaderRepository quoteHeaderRepository;
    private final BrevoEmailService brevoEmailService;
    private final AppUserRepository appUserRepository;
    private final AppRoleRepository appRoleRepository;
    private final SecurityHelper securityHelper;

    public QuoteController(QuoteService quoteService,
                           QuoteHeaderRepository quoteHeaderRepository,
                           BrevoEmailService brevoEmailService,
                           AppUserRepository appUserRepository,
                           AppRoleRepository appRoleRepository,
                           SecurityHelper securityHelper) {
        this.quoteService           = quoteService;
        this.quoteHeaderRepository  = quoteHeaderRepository;
        this.brevoEmailService      = brevoEmailService;
        this.appUserRepository      = appUserRepository;
        this.appRoleRepository      = appRoleRepository;
        this.securityHelper         = securityHelper;
    }

    @GetMapping
    public ResponseEntity<List<QuoteHeader>> getAllQuotes() {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            List<QuoteHeader> quotes = quoteService.getAllQuotes(orgId);
            return new ResponseEntity<>(quotes, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("❌ Error fetching quotes: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuoteHeader> getQuoteById(@PathVariable UUID id) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            Optional<QuoteHeader> quote = quoteService.getQuoteById(id, orgId);
            return quote.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            System.err.println("❌ Error fetching quote: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/ref/{quoteRef}")
    public ResponseEntity<QuoteHeader> getQuoteByRef(@PathVariable String quoteRef) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            Optional<QuoteHeader> quote = quoteService.getQuoteByRef(quoteRef, orgId);
            return quote.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            System.err.println("❌ Error fetching quote by ref: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<QuoteHeader>> getQuotesByCustomerId(@PathVariable UUID customerId) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            List<QuoteHeader> quotes = quoteService.getQuotesByCustomerId(customerId, orgId);
            return new ResponseEntity<>(quotes, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("❌ Error fetching quotes by customer: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/next-ref")
    public ResponseEntity<String> getNextQuoteRef() {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            String nextRef = quoteService.peekNextQuoteRef(orgId);
            return ResponseEntity.ok(nextRef);
        } catch (Exception e) {
            System.err.println("❌ Error generating quote ref: " + e.getMessage());
            return ResponseEntity.status(500).body("Error generating reference");
        }
    }

    @PostMapping
    public ResponseEntity<QuoteHeader> createQuote(@RequestBody QuoteHeader quoteHeader) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            UUID userId = securityHelper.getCurrentUserId();
            QuoteHeader createdQuote = quoteService.createQuote(quoteHeader, orgId, userId);
            return new ResponseEntity<>(createdQuote, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("❌ Error creating quote: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuoteHeader> updateQuote(@PathVariable UUID id,
                                                   @RequestBody QuoteHeader quoteHeader) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            UUID userId = securityHelper.getCurrentUserId();
            QuoteHeader updatedQuote = quoteService.updateQuote(id, quoteHeader, orgId, userId);
            return new ResponseEntity<>(updatedQuote, HttpStatus.OK);
        } catch (RuntimeException e) {
            System.err.println("❌ Error updating quote: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("❌ Error updating quote: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/terms")
    public ResponseEntity<Void> updateQuoteTerms(@PathVariable UUID id,
                                                 @RequestBody List<QuoteTermsCondition> terms) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            UUID userId = securityHelper.getCurrentUserId();
            quoteService.updateQuoteTerms(id, terms, orgId, userId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("❌ Error updating quote terms: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Transactional
    @PutMapping("/{id}/submit")
    public ResponseEntity<?> submitForApproval(@PathVariable UUID id,
                                               @RequestBody Map<String, String> payload) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            UUID userId = securityHelper.getCurrentUserId();

            QuoteHeader quote = quoteService.getQuoteById(id, orgId)
                    .orElseThrow(() -> new RuntimeException("Quote not found"));

            quote.setApprovalStatus("PENDING");
            quote.setUpdatedBy(userId);
            quoteHeaderRepository.save(quote);

            // Notify approvers
            appRoleRepository.findByRoleNameAndOrgId("Quote Approver", orgId).ifPresent(role -> {
                List<AppUser> approvers = appUserRepository.findByOrgIdAndAppRole(orgId, role);
                for (AppUser approver : approvers) {
                    if (approver.getEmail() != null && !approver.getEmail().isBlank()) {
                        String subject = "Quote " + quote.getQuoteRef() + " submitted for approval";
                        String html = "<p>Hi " + approver.getFullName() + ",</p>"
                                + "<p>Quote <strong>" + quote.getQuoteRef() + "</strong> has been submitted for your approval.</p>"
                                + "<p>Customer: " + (quote.getCustomer() != null ? quote.getCustomer().getName() : "—") + "<br>"
                                + "Total Value: " + quote.getCurrency() + " " + quote.getTotalValue() + "</p>"
                                + "<p>Please log in to QuoteApp to review.</p>";
                        brevoEmailService.sendEmail(approver.getEmail(), approver.getFullName(), subject, html);
                    }
                }
            });

            return ResponseEntity.ok(quote);
        } catch (Exception e) {
            System.err.println("❌ Error submitting quote: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    @Transactional
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveQuote(@PathVariable UUID id,
                                          @RequestBody Map<String, String> payload) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            UUID userId = securityHelper.getCurrentUserId();

            QuoteHeader quote = quoteService.getQuoteById(id, orgId)
                    .orElseThrow(() -> new RuntimeException("Quote not found"));

            quote.setApprovalStatus("APPROVED");
            quote.setApprovedBy(payload.get("approvedBy"));
            quote.setUpdatedBy(userId);
            quoteHeaderRepository.save(quote);

            if (quote.getCreatedBy() != null) {
                appUserRepository.findById(quote.getCreatedBy()).ifPresent(creator -> {
                    if (creator.getEmail() != null && !creator.getEmail().isBlank()) {
                        String subject = "Quote " + quote.getQuoteRef() + " has been APPROVED ✅";
                        String html = "<p>Hi " + creator.getFullName() + ",</p>"
                                + "<p>Your quote <strong>" + quote.getQuoteRef() + "</strong> has been <strong style='color:green'>approved</strong>.</p>"
                                + "<p>Customer: " + (quote.getCustomer() != null ? quote.getCustomer().getName() : "—") + "<br>"
                                + "Total Value: " + quote.getCurrency() + " " + quote.getTotalValue() + "</p>";
                        brevoEmailService.sendEmail(creator.getEmail(), creator.getFullName(), subject, html);
                    }
                });
            }

            return ResponseEntity.ok(quote);
        } catch (Exception e) {
            System.err.println("❌ Error approving quote: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    @Transactional
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectQuote(@PathVariable UUID id,
                                         @RequestBody Map<String, String> payload) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            UUID userId = securityHelper.getCurrentUserId();

            QuoteHeader quote = quoteService.getQuoteById(id, orgId)
                    .orElseThrow(() -> new RuntimeException("Quote not found"));

            quote.setApprovalStatus("REJECTED");
            quote.setApprovedBy(payload.get("approvedBy"));
            quote.setRejectionReason(payload.get("rejectionReason"));
            quote.setUpdatedBy(userId);
            quoteHeaderRepository.save(quote);

            if (quote.getCreatedBy() != null) {
                appUserRepository.findById(quote.getCreatedBy()).ifPresent(creator -> {
                    if (creator.getEmail() != null && !creator.getEmail().isBlank()) {
                        String subject = "Quote " + quote.getQuoteRef() + " has been REJECTED ❌";
                        String html = "<p>Hi " + creator.getFullName() + ",</p>"
                                + "<p>Your quote <strong>" + quote.getQuoteRef() + "</strong> has been <strong style='color:red'>rejected</strong>.</p>"
                                + "<p>Reason: " + payload.get("rejectionReason") + "</p>"
                                + "<p>Customer: " + (quote.getCustomer() != null ? quote.getCustomer().getName() : "—") + "<br>"
                                + "Total Value: " + quote.getCurrency() + " " + quote.getTotalValue() + "</p>";
                        brevoEmailService.sendEmail(creator.getEmail(), creator.getFullName(), subject, html);
                    }
                });
            }

            return ResponseEntity.ok(quote);
        } catch (Exception e) {
            System.err.println("❌ Error rejecting quote: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuote(@PathVariable UUID id) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            quoteService.deleteQuote(id, orgId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            System.err.println("❌ Error deleting quote: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("❌ Error deleting quote: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/detail")
    public ResponseEntity<QuoteDetail> addQuoteDetail(@RequestBody QuoteDetail quoteDetail) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            UUID userId = securityHelper.getCurrentUserId();
            QuoteDetail addedDetail = quoteService.addQuoteDetail(quoteDetail, orgId, userId);
            return new ResponseEntity<>(addedDetail, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            System.err.println("❌ Error adding quote detail: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("❌ Error adding quote detail: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/detail/{slNo}")
    public ResponseEntity<QuoteDetail> updateQuoteDetail(@PathVariable UUID slNo,
                                                         @RequestBody QuoteDetail quoteDetail) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            UUID userId = securityHelper.getCurrentUserId();
            QuoteDetail updatedDetail = quoteService.updateQuoteDetail(slNo, quoteDetail, orgId, userId);
            return new ResponseEntity<>(updatedDetail, HttpStatus.OK);
        } catch (RuntimeException e) {
            System.err.println("❌ Error updating quote detail: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("❌ Error updating quote detail: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/detail/{slNo}")
    public ResponseEntity<Void> deleteQuoteDetail(@PathVariable UUID slNo) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            quoteService.deleteQuoteDetail(slNo, orgId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            System.err.println("❌ Error deleting quote detail: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("❌ Error deleting quote detail: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}