package com.example.demo.quote.controller;

import com.example.demo.quote.entity.QuoteDetail;
import com.example.demo.quote.entity.QuoteHeader;
import com.example.demo.quote.service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quotes")
@CrossOrigin(origins = "https://699db1e4064fec9991497b90--sprightly-vacherin-2274ac.netlify.app")
public class QuoteController {

    @Autowired
    private QuoteService quoteService;

    // GET all quotes
    @GetMapping
    public ResponseEntity<List<QuoteHeader>> getAllQuotes() {
        try {
            List<QuoteHeader> quotes = quoteService.getAllQuotes();
            return new ResponseEntity<>(quotes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET quote by ID
    @GetMapping("/{id}")
    public ResponseEntity<QuoteHeader> getQuoteById(@PathVariable Long id) {
        Optional<QuoteHeader> quote = quoteService.getQuoteById(id);
        return quote.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // GET quote by reference
    @GetMapping("/ref/{quoteRef}")
    public ResponseEntity<QuoteHeader> getQuoteByRef(@PathVariable String quoteRef) {
        Optional<QuoteHeader> quote = quoteService.getQuoteByRef(quoteRef);
        return quote.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // GET quotes by customer ID
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<QuoteHeader>> getQuotesByCustomerId(@PathVariable Integer customerId) {
        try {
            List<QuoteHeader> quotes = quoteService.getQuotesByCustomerId(customerId);
            return new ResponseEntity<>(quotes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // POST create new quote
    @PostMapping
    public ResponseEntity<QuoteHeader> createQuote(@RequestBody QuoteHeader quoteHeader) {
        try {
            QuoteHeader createdQuote = quoteService.createQuote(quoteHeader);
            return new ResponseEntity<>(createdQuote, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PUT update quote
    @PutMapping("/{id}")
    public ResponseEntity<QuoteHeader> updateQuote(@PathVariable Long id,
                                                   @RequestBody QuoteHeader quoteHeader) {
        try {
            QuoteHeader updatedQuote = quoteService.updateQuote(id, quoteHeader);
            return new ResponseEntity<>(updatedQuote, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE quote
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuote(@PathVariable Long id) {
        try {
            quoteService.deleteQuote(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/quotes/next-ref")
    public String getNextQuoteRef() {
        return quoteService.generateNextQuoteRef();
    }

    // ✅ POST add single quote detail
    @PostMapping("/detail")
    public ResponseEntity<QuoteDetail> addQuoteDetail(@RequestBody QuoteDetail quoteDetail) {
        try {
            QuoteDetail addedDetail = quoteService.addQuoteDetail(quoteDetail);
            return new ResponseEntity<>(addedDetail, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ PUT update single quote detail
    @PutMapping("/detail/{slNo}")
    public ResponseEntity<QuoteDetail> updateQuoteDetail(@PathVariable Long slNo,
                                                         @RequestBody QuoteDetail quoteDetail) {
        try {
            QuoteDetail updatedDetail = quoteService.updateQuoteDetail(slNo, quoteDetail);
            return new ResponseEntity<>(updatedDetail, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE single quote detail
    @DeleteMapping("/detail/{slNo}")
    public ResponseEntity<Void> deleteQuoteDetail(@PathVariable Long slNo) {
        try {
            quoteService.deleteQuoteDetail(slNo);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}