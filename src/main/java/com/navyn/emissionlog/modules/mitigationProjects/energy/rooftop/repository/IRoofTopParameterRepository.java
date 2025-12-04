package com.navyn.emissionlog.modules.mitigationProjects.Energy.rooftop.repository;

import com.navyn.emissionlog.modules.mitigationProjects.Energy.rooftop.model.RoofTopParameter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IRoofTopParameterRepository extends JpaRepository<RoofTopParameter, UUID> {
}
