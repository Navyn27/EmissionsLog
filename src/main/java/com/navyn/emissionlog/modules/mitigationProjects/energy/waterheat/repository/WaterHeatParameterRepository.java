package com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.repository;

import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.models.WaterHeatParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WaterHeatParameterRepository extends JpaRepository<WaterHeatParameter, UUID> {
}