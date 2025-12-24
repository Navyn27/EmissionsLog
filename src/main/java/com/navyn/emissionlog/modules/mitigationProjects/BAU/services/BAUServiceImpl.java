package com.navyn.emissionlog.modules.mitigationProjects.BAU.services;

import com.navyn.emissionlog.modules.mitigationProjects.BAU.enums.ESector;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.models.BAU;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.dtos.BAUDto;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.repositories.BAURepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BAUServiceImpl implements BAUService {

    private final BAURepository bauRepository;

    @Override
    @Transactional
    public BAU createBAU(BAUDto dto) {
        // Check if BAU with same year and sector already exists
        Optional<BAU> existingBAU = bauRepository.findByYearAndSector(dto.getYear(), dto.getSector());
        if (existingBAU.isPresent()) {
            throw new RuntimeException(
                String.format("BAU record for year %d and sector %s already exists. Please use a different year or sector, or update the existing record.", 
                    dto.getYear(), dto.getSector())
            );
        }

        BAU bau = new BAU();
        bau.setValue(dto.getValue());
        bau.setSector(dto.getSector());
        bau.setYear(dto.getYear());
        
        return bauRepository.save(bau);
    }

    @Override
    @Transactional
    public BAU updateBAU(UUID id, BAUDto dto) {
        return bauRepository.findById(id)
                .map(existingBAU -> {
                    // Check if another BAU with the same year+sector exists (excluding current one)
                    Optional<BAU> bauWithSameYearAndSector = bauRepository.findByYearAndSector(dto.getYear(), dto.getSector());
                    if (bauWithSameYearAndSector.isPresent() && !bauWithSameYearAndSector.get().getId().equals(id)) {
                        throw new RuntimeException(
                            String.format("BAU record for year %d and sector %s already exists. Please use a different year or sector.", 
                                dto.getYear(), dto.getSector())
                        );
                    }
                    
                    existingBAU.setValue(dto.getValue());
                    existingBAU.setSector(dto.getSector());
                    existingBAU.setYear(dto.getYear());
                    
                    return bauRepository.save(existingBAU);
                })
                .orElseThrow(() -> new RuntimeException("BAU not found with id: " + id));
    }

    @Override
    public Optional<BAU> getBAUById(UUID id) {
        return bauRepository.findById(id);
    }

    @Override
    public List<BAU> getAllBAUs() {
        return bauRepository.findAll(Sort.by(Sort.Direction.ASC, "year").and(Sort.by(Sort.Direction.ASC, "sector")));
    }

    @Override
    public List<BAU> getBAUsByYear(Integer year) {
        return bauRepository.findByYearOrderBySectorAsc(year);
    }

    @Override
    public List<BAU> getBAUsBySector(ESector sector) {
        return bauRepository.findBySectorOrderByYearAsc(sector);
    }

    @Override
    public Optional<BAU> getBAUByYearAndSector(Integer year, ESector sector) {
        return bauRepository.findByYearAndSector(year, sector);
    }

    @Override
    @Transactional
    public void deleteBAU(UUID id) {
        if (!bauRepository.existsById(id)) {
            throw new RuntimeException("BAU not found with id: " + id);
        }
        bauRepository.deleteById(id);
    }
}

