package com.navyn.emissionlog.Repositories.Agriculture;

import com.navyn.emissionlog.Models.Agriculture.LimingEmissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LimingEmissionsRepository extends JpaRepository<LimingEmissions, UUID> {
    @Query("SELECT le FROM LimingEmissions le WHERE le.year BETWEEN :startYear AND :endYear")
    List<LimingEmissions> findByYearRange(@Param("startYear") int startYear, @Param("endYear") int endYear);
}
