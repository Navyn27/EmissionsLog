package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.repository;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.models.KigaliWWTPMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface KigaliWWTPMitigationRepository extends JpaRepository<KigaliWWTPMitigation, UUID>, 
        JpaSpecificationExecutor<KigaliWWTPMitigation> {
    
    Optional<KigaliWWTPMitigation> findByYear(Integer year);
    
    @Query("SELECT k FROM KigaliWWTPMitigation k ORDER BY k.projectPhase DESC LIMIT 1")
    Optional<KigaliWWTPMitigation> findTopByOrderByProjectPhaseDesc();
}
