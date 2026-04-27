package com.loanhub.dto;

import com.loanhub.entity.Loan;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class LoanDto {
    private Long id;
    private Long borrowerId;
    private String borrowerName;
    private Long lenderId;
    private String lenderName;
    private BigDecimal amount;
    private BigDecimal interestRate;
    private Integer termMonths;
    private String purpose;
    private String status;
    private BigDecimal emiAmount;
    private BigDecimal totalInterest;
    private BigDecimal totalAmountDue;
    private LocalDateTime createdAt;
    private LocalDateTime approvalDate;
    private LocalDateTime disbursalDate;
    private LocalDateTime maturityDate;
    private String notes;

    public static LoanDto fromEntity(Loan loan) {
        LoanDto dto = new LoanDto();
        dto.setId(loan.getId());
        if (loan.getBorrower() != null) {
            dto.setBorrowerId(loan.getBorrower().getId());
            dto.setBorrowerName(loan.getBorrower().getName());
        }
        if (loan.getLender() != null) {
            dto.setLenderId(loan.getLender().getId());
            dto.setLenderName(loan.getLender().getName());
        }
        dto.setAmount(loan.getAmount());
        dto.setInterestRate(loan.getInterestRate());
        dto.setTermMonths(loan.getTermMonths());
        dto.setPurpose(loan.getPurpose());
        dto.setStatus(loan.getStatus().name());
        dto.setEmiAmount(loan.getEmiAmount());
        dto.setTotalInterest(loan.getTotalInterest());
        dto.setTotalAmountDue(loan.getTotalAmountDue());
        dto.setCreatedAt(loan.getCreatedAt());
        dto.setApprovalDate(loan.getApprovalDate());
        dto.setDisbursalDate(loan.getDisbursalDate());
        dto.setMaturityDate(loan.getMaturityDate());
        dto.setNotes(loan.getNotes());
        return dto;
    }

    @Data
    public static class CreateLoanRequest {
        private Long borrowerId;
        private String borrowerName;
        private BigDecimal amount;
        private BigDecimal interestRate;
        private Integer termMonths;
        private String purpose;
    }

    @Data
    public static class UpdateLoanRequest {
        private String status;
        private Long lenderId;
        private String notes;
    }
}
