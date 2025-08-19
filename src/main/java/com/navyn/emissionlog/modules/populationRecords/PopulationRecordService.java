package com.navyn.emissionlog.modules.populationRecords;

import com.navyn.emissionlog.Enums.Countries;
import com.navyn.emissionlog.modules.populationRecords.PopulationRecords;
import com.navyn.emissionlog.modules.population.dtos.CreatePopulationRecordDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public interface PopulationRecordService {
    PopulationRecords createPopulationRecord(CreatePopulationRecordDto createPopulationRecordDto);

    List<PopulationRecords> getAllPopulationRecords();

    PopulationRecords getPopulationRecordById(UUID id);

    PopulationRecords getPopulationRecordByYear(int year);

    List<PopulationRecords> readPopulationRecordsFromExcel(MultipartFile file);

    PopulationRecords updatePopulationRecord(UUID id, CreatePopulationRecordDto createPopulationRecordDto);

    List<PopulationRecords> getPopulationRecordsByCountry(Countries country);
}
