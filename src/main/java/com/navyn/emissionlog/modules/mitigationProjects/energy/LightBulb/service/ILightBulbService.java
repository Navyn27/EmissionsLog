package com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.service;

import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.dto.CreateLightBulbDTO;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.dto.UpdateLightBulbDTO;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.model.LightBulb;

import java.util.List;
import java.util.UUID;

public interface ILightBulbService {
    LightBulb create(CreateLightBulbDTO lightBulbDTO);
    List<LightBulb> getAll();
    LightBulb getById(UUID id);
    LightBulb update(UUID id, UpdateLightBulbDTO lightBulbDTO);
    void delete(UUID id);

    List<LightBulb> getByYear(int year);
}
