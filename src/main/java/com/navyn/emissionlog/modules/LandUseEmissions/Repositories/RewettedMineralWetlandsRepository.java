package com.navyn.emissionlog.modules.LandUseEmissions.Repositories;

import com.navyn.emissionlog.modules.LandUseEmissions.models.RewettedMineralWetlands;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RewettedMineralWetlandsRepository extends JpaRepository<RewettedMineralWetlands, UUID>, JpaSpecificationExecutor<RewettedMineralWetlands> {
    List<RewettedMineralWetlands> findAllByOrderByYearDesc();
}
