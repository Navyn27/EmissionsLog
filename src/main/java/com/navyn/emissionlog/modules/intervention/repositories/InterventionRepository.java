package com.navyn.emissionlog.modules.intervention.repositories;

import com.navyn.emissionlog.modules.intervention.Intervention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InterventionRepository extends JpaRepository<Intervention, UUID>, JpaSpecificationExecutor<Intervention> {
    
    Optional<Intervention> findByNameIgnoreCase(String name);
}

