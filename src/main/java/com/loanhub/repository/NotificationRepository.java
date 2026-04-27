package com.loanhub.repository;

import com.loanhub.entity.Notification;
import com.loanhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipient(User recipient);
    List<Notification> findByRecipientAndIsRead(User recipient, boolean isRead);
    List<Notification> findByTargetRole(String targetRole);
    List<Notification> findByLoanId(Long loanId);
}
