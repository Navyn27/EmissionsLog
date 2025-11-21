package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.repository;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.models.DailySpreadMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DailySpreadMitigationRepository extends JpaRepository<DailySpreadMitigation, Long>, 
        JpaSpecificationExecutor<DailySpreadMitigation> {
    
    Optional<DailySpreadMitigation> findByYear(Integer year);
}
