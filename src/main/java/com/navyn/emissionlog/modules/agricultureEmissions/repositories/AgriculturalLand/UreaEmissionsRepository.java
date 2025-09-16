package com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand;

import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.UreaEmissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UreaEmissionsRepository extends JpaRepository<UreaEmissions, UUID>, JpaSpecificationExecutor<UreaEmissions> {
    @Query("SELECT ue FROM UreaEmissions ue WHERE ue.year BETWEEN :startYear AND :endYear")
    List<UreaEmissions> findByYearRange(@Param("startYear") int startYear, @Param("endYear") int endYear);
}
