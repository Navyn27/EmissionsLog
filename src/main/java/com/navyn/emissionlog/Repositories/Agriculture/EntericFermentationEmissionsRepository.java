package com.navyn.emissionlog.Repositories.Agriculture;

import com.navyn.emissionlog.Models.Agriculture.AquacultureEmissions;
import com.navyn.emissionlog.Models.Agriculture.EntericFermentationEmissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EntericFermentationEmissionsRepository extends JpaRepository<EntericFermentationEmissions, UUID> {
    @Query("SELECT efe FROM EntericFermentationEmissions efe WHERE efe.year BETWEEN :startYear AND :endYear")
    List<EntericFermentationEmissions> findByYearRange(@Param("startYear") int startYear, @Param("endYear") int endYear);
}
