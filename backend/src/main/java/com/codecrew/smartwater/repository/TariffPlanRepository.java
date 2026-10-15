package com.codecrew.smartwater.repository;

import com.codecrew.smartwater.model.entity.TariffPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TariffPlanRepository extends JpaRepository<TariffPlan, Long> {
    Optional<TariffPlan> findFirstByApartmentIdAndIsActiveTrueOrderByEffectiveFromDesc(Long apartmentId);
    List<TariffPlan> findByApartmentId(Long apartmentId);
}