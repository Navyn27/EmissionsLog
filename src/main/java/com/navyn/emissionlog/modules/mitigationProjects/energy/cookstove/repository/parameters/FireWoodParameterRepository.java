package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.repository.parameters;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.parameters.FireWoodParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FireWoodParameterRepository extends JpaRepository<FireWoodParameter, UUID> {
    Optional<FireWoodParameter> findFirstByIsActiveTrueOrderByCreatedAtDesc();
    List<FireWoodParameter> findAllByOrderByIsActiveDescCreatedAtDesc();
}

