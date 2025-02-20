package com.navyn.emissionlog.ServiceImpls;

import com.navyn.emissionlog.Models.Region;
import com.navyn.emissionlog.Payload.Requests.CreateRegionDto;
import com.navyn.emissionlog.Repositories.RegionRepository;
import com.navyn.emissionlog.Services.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RegionServiceImpl implements RegionService {

    @Autowired
    private RegionRepository regionRepository;

    @Override
    public Region saveRegion(CreateRegionDto region) {
        Region region1 = new Region();
        region1.setCity(region.getCity());
        region1.setCountry(region.getCountry());
        region1.setProvince(region.getProvince());
        return regionRepository.save(region1);
    }

    @Override
    public Optional<Region> getRegionById(UUID id) {
        return regionRepository.findById(id);
    }

    @Override
    public List<Region> getAllRegions() {
        return regionRepository.findAll();
    }

    @Override
    public Region updateRegion(UUID id, CreateRegionDto region) {
        return regionRepository.findById(id)
                .map(existingRegion -> {
                    existingRegion.setCountry(region.getCountry());
                    existingRegion.setProvince(region.getProvince());
                    existingRegion.setCity(region.getCity());
                    return regionRepository.save(existingRegion);
                })
                .orElseThrow(() -> new RuntimeException("Region not found with id " + id));
    }

    @Override
    public void deleteRegion(UUID id) {
        regionRepository.deleteById(id);
    }
}
