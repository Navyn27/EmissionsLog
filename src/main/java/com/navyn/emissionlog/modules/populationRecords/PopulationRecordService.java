package com.navyn.emissionlog.modules.populationRecords;

import com.navyn.emissionlog.Enums.Countries;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.navyn.emissionlog.modules.populationRecords.dtos.CreatePopulationRecordDto;

import java.util.List;
import java.util.UUID;

@Service
public interface PopulationRecordService {
    PopulationRecords createPopulationRecord(CreatePopulationRecordDto createPopulationRecordDto);

    List<PopulationRecords> getAllPopulationRecords(Countries country, Integer year);

    PopulationRecords getPopulationRecordById(UUID id);

    PopulationRecords getPopulationRecordByYear(int year);

    List<PopulationRecords> readPopulationRecordsFromExcel(MultipartFile file);

    PopulationRecords updatePopulationRecord(UUID id, CreatePopulationRecordDto createPopulationRecordDto);

    List<PopulationRecords> getPopulationRecordsByCountry(Countries country);
}
