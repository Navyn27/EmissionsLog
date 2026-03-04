package com.navyn.emissionlog.modules.mitigationProjects.IPPU.repository;

import com.navyn.emissionlog.modules.mitigationProjects.IPPU.model.FGas;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IFGasRepository extends JpaRepository<FGas, UUID> {
    List<FGas> findAllByOrderByNameAsc();
}
