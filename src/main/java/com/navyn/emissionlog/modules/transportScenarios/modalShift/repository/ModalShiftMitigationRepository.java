package com.navyn.emissionlog.modules.transportScenarios.modalShift.repository;

import com.navyn.emissionlog.modules.transportScenarios.modalShift.models.ModalShiftMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ModalShiftMitigationRepository extends JpaRepository<ModalShiftMitigation, UUID>, 
        JpaSpecificationExecutor<ModalShiftMitigation> {
}

