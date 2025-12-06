package com.navyn.emissionlog.modules.mitigationProjects.IPPU.repository;

import com.navyn.emissionlog.modules.mitigationProjects.IPPU.model.IPPUMitigation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IIPPURepository extends JpaRepository<IPPUMitigation, UUID> {
    List<IPPUMitigation> findByYear(int year);
}
