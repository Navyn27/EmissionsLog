package com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.repository;

import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.model.RoofTopMitigation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IRoofTopMitigationRepository extends JpaRepository<RoofTopMitigation, UUID> {
    Optional<RoofTopMitigation> findByYear(int year);
    List<RoofTopMitigation> findAllByOrderByYearAsc();
    Optional<RoofTopMitigation> findFirstByYearLessThanOrderByYearDesc(int year);
}
