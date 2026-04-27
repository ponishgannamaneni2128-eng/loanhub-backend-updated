package com.loanhub.service;

import com.loanhub.dto.TransactionDto;
import com.loanhub.entity.*;
import com.loanhub.exception.AppException;
import com.loanhub.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired private TransactionRepository transactionRepository;
    @Autowired private LoanRepository loanRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private NotificationRepository notificationRepository;

    public List<TransactionDto> getAllTransactions() {
        return transactionRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TransactionDto> getTransactionsByLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new AppException("Loan not found"));
        return transactionRepository.findByLoan(loan).stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TransactionDto> getTransactionsByBorrower(Long borrowerId) {
        User borrower = userRepository.findById(borrowerId)
                .orElseThrow(() -> new AppException("User not found"));
        return transactionRepository.findByBorrower(borrower).stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
    }

    public TransactionDto createTransaction(TransactionDto.CreateTransactionRequest request) {
        Loan loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() -> new AppException("Loan not found"));
        User borrower = userRepository.findById(request.getBorrowerId())
                .orElseThrow(() -> new AppException("Borrower not found"));

        User lender = null;
        if (request.getLenderId() != null) {
            lender = userRepository.findById(request.getLenderId()).orElse(null);
        }

        Transaction.TransactionType type = Transaction.TransactionType.payment;
        try {
            if (request.getType() != null) {
                type = Transaction.TransactionType.valueOf(request.getType());
            }
        } catch (IllegalArgumentException ignored) {}

        Transaction.PaymentMethod method = Transaction.PaymentMethod.upi;
        try {
            if (request.getPaymentMethod() != null) {
                method = Transaction.PaymentMethod.valueOf(request.getPaymentMethod());
            }
        } catch (IllegalArgumentException ignored) {}

        Transaction transaction = Transaction.builder()
                .loan(loan)
                .borrower(borrower)
                .lender(lender)
                .amount(request.getAmount())
                .type(type)
                .paymentMethod(method)
                .status(Transaction.TransactionStatus.completed)
                .referenceNumber("TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .borrowerName(request.getBorrowerName() != null ? request.getBorrowerName() : borrower.getName())
                .notes(request.getNotes())
                .processedAt(LocalDateTime.now())
                .build();

        transaction = transactionRepository.save(transaction);

        // Notify lender about payment if applicable
        if (lender != null && type == Transaction.TransactionType.payment) {
            Notification notif = Notification.builder()
                    .recipient(lender)
                    .type("payment_received")
                    .targetRole("lender")
                    .title("Payment Received")
                    .message(borrower.getName() + " made a payment of Rs." +
                            String.format("%.2f", request.getAmount().doubleValue()) +
                            " for loan #" + loan.getId())
                    .loanId(loan.getId())
                    .borrowerName(borrower.getName())
                    .amount(request.getAmount().doubleValue())
                    .isRead(false)
                    .build();
            notificationRepository.save(notif);
        }

        return TransactionDto.fromEntity(transaction);
    }
}
