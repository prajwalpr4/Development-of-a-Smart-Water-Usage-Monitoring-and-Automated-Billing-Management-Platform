package com.codecrew.smartwater.repository;

import com.codecrew.smartwater.model.entity.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {
    Optional<Apartment> findByName(String name);
    boolean existsByName(String name);
}