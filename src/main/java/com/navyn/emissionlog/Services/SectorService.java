package com.navyn.emissionlog.Services;

import com.navyn.emissionlog.Models.Sector;
import com.navyn.emissionlog.Payload.Requests.CreateSectorDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SectorService {
    Sector saveSector(CreateSectorDto sectorDto);
    Optional<Sector> getSectorById(UUID id);
    List<Sector> getAllSectors();
    Sector updateSector(UUID id, CreateSectorDto sectorDto);
    void deleteSector(UUID id);
}
