package com.Inventra.backend.repository;

import com.Inventra.backend.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction , Long> {

}
