package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.repository;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.models.DailySpreadMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DailySpreadMitigationRepository extends JpaRepository<DailySpreadMitigation, UUID>,
        JpaSpecificationExecutor<DailySpreadMitigation> {

    Optional<DailySpreadMitigation> findByYear(Integer year);

    // Find by year range for dashboard filtering
    List<DailySpreadMitigation> findByYearBetweenOrderByYearDesc(Integer startYear, Integer endYear);

    default List<DailySpreadMitigation> findByYearRange(Integer startYear, Integer endYear) {
        return findByYearBetweenOrderByYearDesc(startYear, endYear);
    }
}
