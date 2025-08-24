package com.navyn.emissionlog.modules.agricultureEmissions.repositories;

import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.BurningEmissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface BurningEmissionsRepository extends JpaRepository<BurningEmissions, UUID>, JpaSpecificationExecutor<BurningEmissions> {
}
