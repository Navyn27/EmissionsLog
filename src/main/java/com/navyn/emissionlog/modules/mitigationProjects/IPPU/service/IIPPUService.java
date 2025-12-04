package com.navyn.emissionlog.modules.mitigationProjects.IPPU.service;

import com.navyn.emissionlog.modules.mitigationProjects.IPPU.dto.IPPUMitigationDTO;
import com.navyn.emissionlog.modules.mitigationProjects.IPPU.dto.IPPUMitigationResponseDTO;
import com.navyn.emissionlog.modules.mitigationProjects.IPPU.model.IPPUMitigation;

import java.util.Optional;
import java.util.UUID;

public interface IIPPUService {
    IPPUMitigation save(IPPUMitigationDTO ippuMitigationDTO);
    IPPUMitigationResponseDTO findAll();
    Optional<IPPUMitigation> findById(UUID id);
    void deleteById(UUID id);
    IPPUMitigation update(UUID id, IPPUMitigationDTO ippuMitigationDTO);
}
