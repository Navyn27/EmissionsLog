package com.navyn.emissionlog.modules.regions;

import com.navyn.emissionlog.modules.regions.dtos.CreateRegionDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegionService {

    Region saveRegion(CreateRegionDto region);

    Optional<Region> getRegionById(UUID id);
    List<Region> getAllRegions();

    Region updateRegion(UUID id, CreateRegionDto region);

    void deleteRegion(UUID id);
}
