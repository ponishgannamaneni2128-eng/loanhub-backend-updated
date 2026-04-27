package com.loanhub.service;

import com.loanhub.dto.LoanDto;
import com.loanhub.dto.NotificationDto;
import com.loanhub.entity.Loan;
import com.loanhub.entity.Notification;
import com.loanhub.entity.User;
import com.loanhub.exception.AppException;
import com.loanhub.repository.LoanRepository;
import com.loanhub.repository.NotificationRepository;
import com.loanhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LoanService {

    @Autowired private LoanRepository loanRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private NotificationRepository notificationRepository;

    public List<LoanDto> getAllLoans() {
        return loanRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(LoanDto::fromEntity)
                .collect(Collectors.toList());
    }

    public LoanDto getLoanById(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new AppException("Loan not found"));
        return LoanDto.fromEntity(loan);
    }

    public List<LoanDto> getLoansByBorrower(Long borrowerId) {
        User borrower = userRepository.findById(borrowerId)
                .orElseThrow(() -> new AppException("Borrower not found"));
        return loanRepository.findByBorrowerOrderByCreatedAtDesc(borrower).stream()
                .map(LoanDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<LoanDto> getLoansByLender(Long lenderId) {
        User lender = userRepository.findById(lenderId)
                .orElseThrow(() -> new AppException("Lender not found"));
        return loanRepository.findByLenderOrderByCreatedAtDesc(lender).stream()
                .map(LoanDto::fromEntity)
                .collect(Collectors.toList());
    }

    public LoanDto createLoan(LoanDto.CreateLoanRequest request) {
        User borrower = userRepository.findById(request.getBorrowerId())
                .orElseThrow(() -> new AppException("Borrower not found"));

        // Calculate EMI: EMI = P * r * (1+r)^n / ((1+r)^n - 1)
        BigDecimal principal = request.getAmount();
        BigDecimal annualRate = request.getInterestRate();
        int months = request.getTermMonths();
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        BigDecimal emiAmount;

        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            emiAmount = principal.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);
        } else {
            BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
            BigDecimal power = onePlusR.pow(months);
            emiAmount = principal.multiply(monthlyRate).multiply(power)
                    .divide(power.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
        }

        BigDecimal totalAmountDue = emiAmount.multiply(BigDecimal.valueOf(months)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalInterest = totalAmountDue.subtract(principal).setScale(2, RoundingMode.HALF_UP);

        Loan loan = Loan.builder()
                .borrower(borrower)
                .borrowerName(borrower.getName())
                .amount(principal)
                .interestRate(annualRate)
                .termMonths(months)
                .purpose(request.getPurpose())
                .status(Loan.LoanStatus.pending)
                .emiAmount(emiAmount)
                .totalInterest(totalInterest)
                .totalAmountDue(totalAmountDue)
                .maturityDate(LocalDateTime.now().plusMonths(months))
                .build();

        loan = loanRepository.save(loan);

        // Create notification for all lenders
        List<User> lenders = userRepository.findByRole(User.Role.lender);
        for (User lender : lenders) {
            if (lender.getStatus() == User.Status.active) {
                Notification notif = Notification.builder()
                        .recipient(lender)
                        .type("loan_application")
                        .targetRole("lender")
                        .title("New Loan Application")
                        .message("New loan application from " + borrower.getName() +
                                " for Rs." + String.format("%.0f", principal.doubleValue()) +
                                " at " + annualRate + "% interest rate.")
                        .loanId(loan.getId())
                        .borrowerId(borrower.getId())
                        .borrowerName(borrower.getName())
                        .amount(principal.doubleValue())
                        .interestRate(annualRate.doubleValue())
                        .term(months)
                        .purpose(request.getPurpose())
                        .isRead(false)
                        .accepted(false)
                        .build();
                notificationRepository.save(notif);
            }
        }

        return LoanDto.fromEntity(loan);
    }

    public LoanDto updateLoan(Long id, LoanDto.UpdateLoanRequest request) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new AppException("Loan not found"));

        if (request.getStatus() != null) {
            try {
                Loan.LoanStatus newStatus = Loan.LoanStatus.valueOf(request.getStatus());
                loan.setStatus(newStatus);
                if (newStatus == Loan.LoanStatus.active) {
                    loan.setApprovalDate(LocalDateTime.now());
                    loan.setDisbursalDate(LocalDateTime.now());
                }
            } catch (IllegalArgumentException e) {
                throw new AppException("Invalid loan status: " + request.getStatus());
            }
        }

        if (request.getLenderId() != null) {
            User lender = userRepository.findById(request.getLenderId())
                    .orElseThrow(() -> new AppException("Lender not found"));
            loan.setLender(lender);

            // Mark all notifications for this loan as accepted
            List<Notification> notifs = notificationRepository.findByLoanId(id);
            notifs.forEach(n -> { n.setAccepted(true); n.setRead(true); });
            notificationRepository.saveAll(notifs);

            // Create notification for borrower
            User borrower = loan.getBorrower();
            if (borrower != null) {
                Notification notif = Notification.builder()
                        .recipient(borrower)
                        .type("loan_approved")
                        .targetRole("borrower")
                        .title("Loan Approved!")
                        .message("Your loan of Rs." +
                                String.format("%.0f", loan.getAmount().doubleValue()) +
                                " has been approved by " + lender.getName())
                        .loanId(id)
                        .lenderName(lender.getName())
                        .isRead(false)
                        .build();
                notificationRepository.save(notif);
            }
        }

        if (request.getNotes() != null) {
            loan.setNotes(request.getNotes());
        }

        return LoanDto.fromEntity(loanRepository.save(loan));
    }

    public LoanDto patchLoan(Long id, Map<String, Object> updates) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new AppException("Loan not found"));

        if (updates.containsKey("status")) {
            try {
                loan.setStatus(Loan.LoanStatus.valueOf((String) updates.get("status")));
            } catch (IllegalArgumentException e) {
                throw new AppException("Invalid status");
            }
        }
        if (updates.containsKey("lenderId")) {
            Long lenderId = ((Number) updates.get("lenderId")).longValue();
            User lender = userRepository.findById(lenderId)
                    .orElseThrow(() -> new AppException("Lender not found"));
            loan.setLender(lender);
        }
        if (updates.containsKey("notes")) loan.setNotes((String) updates.get("notes"));

        return LoanDto.fromEntity(loanRepository.save(loan));
    }

    public void deleteLoan(Long id) {
        if (!loanRepository.existsById(id)) {
            throw new AppException("Loan not found");
        }
        loanRepository.deleteById(id);
    }

    public Map<String, Object> getAnalytics() {
        List<Loan> loans = loanRepository.findAll();
        long total = loans.size();
        long active = loans.stream().filter(l -> l.getStatus() == Loan.LoanStatus.active).count();
        long pending = loans.stream().filter(l -> l.getStatus() == Loan.LoanStatus.pending).count();
        long completed = loans.stream().filter(l -> l.getStatus() == Loan.LoanStatus.completed).count();
        long declined = loans.stream().filter(l -> l.getStatus() == Loan.LoanStatus.declined).count();
        long overdue = loans.stream().filter(l -> l.getStatus() == Loan.LoanStatus.overdue).count();

        BigDecimal totalDisbursed = loans.stream()
                .filter(l -> l.getStatus() == Loan.LoanStatus.active || l.getStatus() == Loan.LoanStatus.completed)
                .map(Loan::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPending = loans.stream()
                .filter(l -> l.getStatus() == Loan.LoanStatus.pending)
                .map(Loan::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Map.of(
                "total", total,
                "active", active,
                "pending", pending,
                "completed", completed,
                "declined", declined,
                "overdue", overdue,
                "totalDisbursed", totalDisbursed,
                "totalPending", totalPending
        );
    }
}
