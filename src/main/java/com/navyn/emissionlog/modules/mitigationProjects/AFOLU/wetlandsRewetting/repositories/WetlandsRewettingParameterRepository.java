package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.repositories;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.models.WetlandsRewettingParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WetlandsRewettingParameterRepository extends JpaRepository<WetlandsRewettingParameter, UUID> {

    Optional<WetlandsRewettingParameter> findFirstByIsActiveTrueOrderByCreatedAtDesc();
}
