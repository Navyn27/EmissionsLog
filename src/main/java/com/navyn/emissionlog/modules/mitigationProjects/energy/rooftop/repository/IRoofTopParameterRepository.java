package com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.repository;

import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.model.RoofTopParameter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IRoofTopParameterRepository extends JpaRepository<RoofTopParameter, UUID> {
}
