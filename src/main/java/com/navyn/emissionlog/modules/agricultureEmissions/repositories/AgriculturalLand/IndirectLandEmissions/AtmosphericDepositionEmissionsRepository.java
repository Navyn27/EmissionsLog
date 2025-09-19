package com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.IndirectLandEmissions;

import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.IndirectLandEmissions.AtmosphericDepositionEmissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AtmosphericDepositionEmissionsRepository extends JpaRepository<AtmosphericDepositionEmissions, UUID>, JpaSpecificationExecutor<AtmosphericDepositionEmissions> {
}
