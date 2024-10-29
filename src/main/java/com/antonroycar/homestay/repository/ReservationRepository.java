package com.antonroycar.homestay.repository;

import com.antonroycar.homestay.entity.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReservationRepository extends MongoRepository<Reservation, String> {
}
