package com.navyn.emissionlog.modules.transportScenarios.modalShift.repository;

import com.navyn.emissionlog.modules.transportScenarios.modalShift.models.ModalShiftParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ModalShiftParameterRepository extends JpaRepository<ModalShiftParameter, UUID> {

    Optional<ModalShiftParameter> findFirstByOrderByCreatedAtDesc();

    /**
     * Finds the latest active ModalShiftParameter ordered by creation date (most recent first)
     * @return Optional containing the latest active parameter, or empty if none found
     */
    Optional<ModalShiftParameter> findFirstByIsActiveTrueOrderByCreatedAtDesc();
}

