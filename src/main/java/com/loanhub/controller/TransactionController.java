package com.loanhub.controller;

import com.loanhub.dto.ApiResponse;
import com.loanhub.dto.TransactionDto;
import com.loanhub.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionDto>>> getAllTransactions(
            @RequestParam(required = false) Long loanId,
            @RequestParam(required = false) Long borrowerId) {
        if (loanId != null) {
            return ResponseEntity.ok(ApiResponse.ok(transactionService.getTransactionsByLoan(loanId)));
        }
        if (borrowerId != null) {
            return ResponseEntity.ok(ApiResponse.ok(transactionService.getTransactionsByBorrower(borrowerId)));
        }
        return ResponseEntity.ok(ApiResponse.ok(transactionService.getAllTransactions()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TransactionDto>> createTransaction(
            @RequestBody TransactionDto.CreateTransactionRequest request) {
        TransactionDto tx = transactionService.createTransaction(request);
        return ResponseEntity.ok(ApiResponse.ok("Payment recorded", tx));
    }

    // Alias: POST /api/payments
    @PostMapping("/payment")
    public ResponseEntity<ApiResponse<TransactionDto>> createPayment(
            @RequestBody TransactionDto.CreateTransactionRequest request) {
        TransactionDto tx = transactionService.createTransaction(request);
        return ResponseEntity.ok(ApiResponse.ok("Payment successful", tx));
    }
}
