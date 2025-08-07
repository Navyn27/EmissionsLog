package com.navyn.emissionlog.Repositories.Agriculture;

import com.navyn.emissionlog.Models.Agriculture.EntericFermentationEmissions;
import com.navyn.emissionlog.Models.Agriculture.SyntheticFertilizerEmissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SyntheticFertilizerEmissionsRepository extends JpaRepository<SyntheticFertilizerEmissions, UUID> {
    @Query("SELECT sfe FROM SyntheticFertilizerEmissions sfe WHERE sfe.year BETWEEN :startYear AND :endYear")
    List<SyntheticFertilizerEmissions> findByYearRange(@Param("startYear") int startYear, @Param("endYear") int endYear);
}
