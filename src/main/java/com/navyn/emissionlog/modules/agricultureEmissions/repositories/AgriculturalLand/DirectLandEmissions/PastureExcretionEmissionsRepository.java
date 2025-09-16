package com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.DirectLandEmissions;

import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions.PastureExcretionEmissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface PastureExcretionEmissionsRepository extends JpaRepository<PastureExcretionEmissions, UUID>, JpaSpecificationExecutor<PastureExcretionEmissions> {
}
