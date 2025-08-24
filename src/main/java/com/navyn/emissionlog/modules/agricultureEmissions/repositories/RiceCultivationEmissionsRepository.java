package com.navyn.emissionlog.modules.agricultureEmissions.repositories;

import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.RiceCultivationEmissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RiceCultivationEmissionsRepository extends JpaRepository<RiceCultivationEmissions, UUID>, JpaSpecificationExecutor<RiceCultivationEmissions> {
    @Query("SELECT rce FROM RiceCultivationEmissions rce WHERE rce.year BETWEEN :startYear AND :endYear")
    List<RiceCultivationEmissions> findByYearRange(@Param("startYear") int startYear, @Param("endYear") int endYear);
}
