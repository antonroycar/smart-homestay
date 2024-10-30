package com.antonroycar.homestay.repository;

import com.antonroycar.homestay.entity.Reservation;
import com.antonroycar.homestay.entity.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByStatus(String customerId);
    boolean existsByReservation(Reservation reservation);
    Optional<Transaction> findByPaymentCode(String paymentCode);
}
