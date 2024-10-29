package com.antonroycar.homestay.repository;

import com.antonroycar.homestay.entity.Crew;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CrewRepository extends MongoRepository<Crew, String> {
}
