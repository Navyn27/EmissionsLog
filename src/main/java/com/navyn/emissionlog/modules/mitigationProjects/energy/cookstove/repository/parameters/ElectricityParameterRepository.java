package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.repository.parameters;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.parameters.ElectricityParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ElectricityParameterRepository extends JpaRepository<ElectricityParameter, UUID> {
    Optional<ElectricityParameter> findFirstByIsActiveTrueOrderByCreatedAtDesc();
    List<ElectricityParameter> findAllByOrderByIsActiveDescCreatedAtDesc();
}

