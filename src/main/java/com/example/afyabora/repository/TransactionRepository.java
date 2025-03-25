package com.example.afyabora.repository;

import com.example.afyabora.model.Transaction;
import com.example.afyabora.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByReference(String reference);

    List<Transaction> findByStatusAndExpiryDateAfter(TransactionStatus status, LocalDateTime now);

    boolean existsByEmailAndStatusAndExpiryDateAfter(String email, TransactionStatus status, LocalDateTime now);
}
