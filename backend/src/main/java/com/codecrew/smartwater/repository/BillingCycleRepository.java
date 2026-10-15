package com.codecrew.smartwater.repository;

import com.codecrew.smartwater.model.entity.BillingCycle;
import com.codecrew.smartwater.model.enums.CycleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillingCycleRepository extends JpaRepository<BillingCycle, Long> {
    List<BillingCycle> findByApartmentId(Long apartmentId);
    Optional<BillingCycle> findByApartmentIdAndStatus(Long apartmentId, CycleStatus status);
}