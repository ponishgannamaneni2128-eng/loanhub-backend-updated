package com.loanhub.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(nullable = false)
    private String type;

    private String targetRole;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    private Long loanId;
    private Long borrowerId;
    private String borrowerName;
    private Double amount;
    private Double interestRate;
    private Integer term;
    private String purpose;
    private String lenderName;

    @Builder.Default
    private boolean isRead = false;

    @Builder.Default
    private boolean accepted = false;

    private String declinedBy;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime readAt;
}
