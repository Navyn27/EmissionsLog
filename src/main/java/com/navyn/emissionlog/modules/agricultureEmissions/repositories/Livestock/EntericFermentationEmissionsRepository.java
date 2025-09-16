package com.navyn.emissionlog.modules.agricultureEmissions.repositories.Livestock;

import com.navyn.emissionlog.modules.agricultureEmissions.models.Livestock.EntericFermentationEmissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EntericFermentationEmissionsRepository extends JpaRepository<EntericFermentationEmissions, UUID>, JpaSpecificationExecutor<EntericFermentationEmissions> {
    @Query("SELECT efe FROM EntericFermentationEmissions efe WHERE efe.year BETWEEN :startYear AND :endYear")
    List<EntericFermentationEmissions> findByYearRange(@Param("startYear") int startYear, @Param("endYear") int endYear);
}
