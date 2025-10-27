package com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.IndirectManureEmissions;

import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.IndirectManureEmissions.VolatilizationEmissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VolatilizationEmissionsRepository extends JpaRepository<VolatilizationEmissions, UUID>, JpaSpecificationExecutor<VolatilizationEmissions> {
    List<VolatilizationEmissions> findAllByOrderByYearDesc();
}
