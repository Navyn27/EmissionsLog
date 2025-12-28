package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.repository;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.models.ISWMParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ISWMParameterRepository extends JpaRepository<ISWMParameter, UUID> {
    
    Optional<ISWMParameter> findFirstByIsActiveTrueOrderByCreatedAtDesc();
}

