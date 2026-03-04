package com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand;

import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.BurningEmissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BurningEmissionsRepository extends JpaRepository<BurningEmissions, UUID>, JpaSpecificationExecutor<BurningEmissions> {
    List<BurningEmissions> findAllByOrderByYearDesc();
    
    Optional<BurningEmissions> findByYearAndBurningAgentType(int year, com.navyn.emissionlog.Enums.Agriculture.BurningAgentType burningAgentType);
}
