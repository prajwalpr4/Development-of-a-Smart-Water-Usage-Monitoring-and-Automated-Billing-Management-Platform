package com.codecrew.smartwater.repository;

import com.codecrew.smartwater.model.entity.Household;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HouseholdRepository extends JpaRepository<Household, Long> {
    List<Household> findByApartmentId(Long apartmentId);
    Optional<Household> findByResidentId(Long residentId);
    boolean existsByApartmentIdAndFlatNumber(Long apartmentId, String flatNumber);
}