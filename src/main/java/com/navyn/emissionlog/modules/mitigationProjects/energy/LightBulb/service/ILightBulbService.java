package com.navyn.emissionlog.modules.mitigationProjects.Energy.LightBulb.service;

import com.navyn.emissionlog.modules.mitigationProjects.Energy.LightBulb.dto.CreateLightBulbDTO;
import com.navyn.emissionlog.modules.mitigationProjects.Energy.LightBulb.dto.UpdateLightBulbDTO;
import com.navyn.emissionlog.modules.mitigationProjects.Energy.LightBulb.model.LightBulb;

import java.util.List;
import java.util.UUID;

public interface ILightBulbService {
    LightBulb create(CreateLightBulbDTO lightBulbDTO);
    List<LightBulb> getAll();
    LightBulb getById(UUID id);
    LightBulb update(UUID id, UpdateLightBulbDTO lightBulbDTO);
    void delete(UUID id);
}
