package com.antonroycar.homestay.repository;

import com.antonroycar.homestay.entity.Reservation;
import com.antonroycar.homestay.entity.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByStatus(String customerId);
    boolean existsByReservation(Reservation reservation);
}
