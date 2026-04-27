package com.loanhub.repository;

import com.loanhub.entity.Transaction;
import com.loanhub.entity.User;
import com.loanhub.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByLoan(Loan loan);
    List<Transaction> findByBorrower(User borrower);
    List<Transaction> findByLender(User lender);
    List<Transaction> findAllByOrderByCreatedAtDesc();
}
