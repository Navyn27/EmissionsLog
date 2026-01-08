package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.repository.parameters;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.parameters.LGPParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LGPParameterRepository extends JpaRepository<LGPParameter, UUID> {
    Optional<LGPParameter> findFirstByIsActiveTrueOrderByCreatedAtDesc();
    List<LGPParameter> findAllByOrderByIsActiveDescCreatedAtDesc();
}

