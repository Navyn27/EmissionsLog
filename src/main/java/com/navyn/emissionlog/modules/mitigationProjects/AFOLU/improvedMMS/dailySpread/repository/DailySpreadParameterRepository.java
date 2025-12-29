package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.repository;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.models.DailySpreadParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DailySpreadParameterRepository extends JpaRepository<DailySpreadParameter, UUID> {
    Optional<DailySpreadParameter> findFirstByIsActiveTrueOrderByCreatedAtDesc();
}

