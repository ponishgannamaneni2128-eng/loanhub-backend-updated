package com.loanhub.dto;

import com.loanhub.entity.Transaction;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TransactionDto {
    private Long id;
    private Long loanId;
    private Long borrowerId;
    private String borrowerName;
    private Long lenderId;
    private String lenderName;
    private BigDecimal amount;
    private String type;
    private String paymentMethod;
    private String status;
    private String referenceNumber;
    private String notes;
    private LocalDateTime createdAt;

    public static TransactionDto fromEntity(Transaction t) {
        TransactionDto dto = new TransactionDto();
        dto.setId(t.getId());
        if (t.getLoan() != null) dto.setLoanId(t.getLoan().getId());
        if (t.getBorrower() != null) {
            dto.setBorrowerId(t.getBorrower().getId());
            dto.setBorrowerName(t.getBorrower().getName());
        }
        if (t.getLender() != null) {
            dto.setLenderId(t.getLender().getId());
            dto.setLenderName(t.getLender().getName());
        }
        dto.setAmount(t.getAmount());
        dto.setType(t.getType().name());
        dto.setPaymentMethod(t.getPaymentMethod().name());
        dto.setStatus(t.getStatus().name());
        dto.setReferenceNumber(t.getReferenceNumber());
        dto.setNotes(t.getNotes());
        dto.setCreatedAt(t.getCreatedAt());
        return dto;
    }

    @Data
    public static class CreateTransactionRequest {
        private Long loanId;
        private Long borrowerId;
        private Long lenderId;
        private BigDecimal amount;
        private String type;
        private String paymentMethod;
        private String borrowerName;
        private String notes;
    }
}
