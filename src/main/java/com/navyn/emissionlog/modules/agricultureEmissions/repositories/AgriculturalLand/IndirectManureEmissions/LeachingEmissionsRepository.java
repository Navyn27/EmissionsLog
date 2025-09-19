package com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.IndirectManureEmissions;

import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.IndirectManureEmissions.LeachingEmissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LeachingEmissionsRepository extends JpaRepository<LeachingEmissions, UUID>, JpaSpecificationExecutor<LeachingEmissions> {
}
