package com.navyn.emissionlog.Services;

import com.navyn.emissionlog.Models.Region;
import com.navyn.emissionlog.Payload.Requests.CreateRegionDto;

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
