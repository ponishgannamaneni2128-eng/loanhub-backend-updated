package com.loanhub.dto;

import com.loanhub.entity.Notification;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class NotificationDto {
    private Long id;
    private Long recipientId;
    private String type;
    private String targetRole;
    private String title;
    private String message;
    private Long loanId;
    private Long borrowerId;
    private String borrowerName;
    private Double amount;
    private Double interestRate;
    private Integer term;
    private String purpose;
    private String lenderName;
    private boolean isRead;
    private boolean accepted;
    private String declinedBy;
    private LocalDateTime createdAt;

    public static NotificationDto fromEntity(Notification n) {
        NotificationDto dto = new NotificationDto();
        dto.setId(n.getId());
        if (n.getRecipient() != null) dto.setRecipientId(n.getRecipient().getId());
        dto.setType(n.getType());
        dto.setTargetRole(n.getTargetRole());
        dto.setTitle(n.getTitle());
        dto.setMessage(n.getMessage());
        dto.setLoanId(n.getLoanId());
        dto.setBorrowerId(n.getBorrowerId());
        dto.setBorrowerName(n.getBorrowerName());
        dto.setAmount(n.getAmount());
        dto.setInterestRate(n.getInterestRate());
        dto.setTerm(n.getTerm());
        dto.setPurpose(n.getPurpose());
        dto.setLenderName(n.getLenderName());
        dto.setRead(n.isRead());
        dto.setAccepted(n.isAccepted());
        dto.setDeclinedBy(n.getDeclinedBy());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }
}
