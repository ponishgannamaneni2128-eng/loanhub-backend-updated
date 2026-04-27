package com.loanhub.repository;

import com.loanhub.entity.Loan;
import com.loanhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByBorrower(User borrower);
    List<Loan> findByLender(User lender);
    List<Loan> findByStatus(Loan.LoanStatus status);
    List<Loan> findByBorrowerOrderByCreatedAtDesc(User borrower);
    List<Loan> findByLenderOrderByCreatedAtDesc(User lender);
    List<Loan> findAllByOrderByCreatedAtDesc();

    @Query("SELECT COUNT(l) FROM Loan l WHERE l.status = :status")
    long countByStatus(Loan.LoanStatus status);
}
