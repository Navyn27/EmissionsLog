package com.navyn.emissionlog.Services;

import com.navyn.emissionlog.Controllers.PopulationRecordsController;
import com.navyn.emissionlog.Models.PopulationRecords;
import com.navyn.emissionlog.Payload.Requests.CreatePopulationRecordDto;
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
}
