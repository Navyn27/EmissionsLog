package com.navyn.emissionlog.ServiceImpls;

import com.navyn.emissionlog.Models.Sector;
import com.navyn.emissionlog.Payload.Requests.CreateSectorDto;
import com.navyn.emissionlog.Repositories.SectorRepository;
import com.navyn.emissionlog.Services.SectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SectorServiceImpl implements SectorService {

    @Autowired
    private SectorRepository sectorRepository;

    @Override
    public Sector saveSector(CreateSectorDto sectorDto) {
        Sector sector = new Sector();
        sector.setName(sectorDto.getName());
        sector.setDescription(sectorDto.getDescription());
        return sectorRepository.save(sector);
    }

    @Override
    public Optional<Sector> getSectorById(UUID id) {
        return sectorRepository.findById(id);
    }

    @Override
    public List<Sector> getAllSectors() {
        return sectorRepository.findAll();
    }

    @Override
    public Sector updateSector(UUID id, CreateSectorDto sectorDto) {
        return sectorRepository.findById(id)
                .map(existingSector -> {
                    existingSector.setName(sectorDto.getName());
                    existingSector.setDescription(sectorDto.getDescription());
                    return sectorRepository.save(existingSector);
                })
                .orElseThrow(() -> new RuntimeException("Sector not found with id " + id));
    }

    @Override
    public void deleteSector(UUID id) {
        sectorRepository.deleteById(id);
    }
}
