package com.loanhub.controller;

import com.loanhub.dto.ApiResponse;
import com.loanhub.dto.LoanDto;
import com.loanhub.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<LoanDto>>> getAllLoans(
            @RequestParam(required = false) Long borrowerId,
            @RequestParam(required = false) Long lenderId) {
        if (borrowerId != null) {
            return ResponseEntity.ok(ApiResponse.ok(loanService.getLoansByBorrower(borrowerId)));
        }
        if (lenderId != null) {
            return ResponseEntity.ok(ApiResponse.ok(loanService.getLoansByLender(lenderId)));
        }
        return ResponseEntity.ok(ApiResponse.ok(loanService.getAllLoans()));
    }

    @GetMapping("/analytics/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAnalytics() {
        return ResponseEntity.ok(ApiResponse.ok(loanService.getAnalytics()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LoanDto>> getLoanById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(loanService.getLoanById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LoanDto>> createLoan(
            @RequestBody LoanDto.CreateLoanRequest request) {
        LoanDto loan = loanService.createLoan(request);
        return ResponseEntity.ok(ApiResponse.ok("Loan application submitted", loan));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LoanDto>> updateLoan(
            @PathVariable Long id,
            @RequestBody LoanDto.UpdateLoanRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Loan updated", loanService.updateLoan(id, request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<LoanDto>> patchLoan(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(ApiResponse.ok("Loan updated", loanService.patchLoan(id, updates)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLoan(@PathVariable Long id) {
        loanService.deleteLoan(id);
        return ResponseEntity.ok(ApiResponse.ok("Loan deleted", null));
    }
}
