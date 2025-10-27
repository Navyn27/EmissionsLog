package com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand;

import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.AquacultureEmissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AquacultureEmissionsRepository extends JpaRepository<AquacultureEmissions, UUID>, JpaSpecificationExecutor<AquacultureEmissions> {
    @Query("SELECT ae FROM AquacultureEmissions ae WHERE ae.year BETWEEN :startYear AND :endYear ORDER BY ae.year DESC")
    List<AquacultureEmissions> findByYearRange(@Param("startYear") int startYear, @Param("endYear") int endYear);
}
