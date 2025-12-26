package com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.repository;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.models.LandfillGasParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LandfillGasParameterRepository extends JpaRepository<LandfillGasParameter, UUID> {

    Optional<LandfillGasParameter> findFirstByOrderByCreatedAtDesc();

    /**
     * Finds the latest active LandfillGasParameter ordered by creation date (most recent first)
     * @return Optional containing the latest active parameter, or empty if none found
     */
    Optional<LandfillGasParameter> findFirstByIsActiveTrueOrderByCreatedAtDesc();
}
