package com.navyn.emissionlog.modules.populationRecords;
import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.modules.fuel.dtos.CreateFuelDto;
import com.navyn.emissionlog.modules.population.dtos.CreatePopulationRecordDto;
import com.navyn.emissionlog.utils.ExcelReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PopulationRecordsServiceImpl implements PopulationRecordService {

    private final PopulationRecordsRepository populationRecordRepository;

    @Override
    public PopulationRecords createPopulationRecord(CreatePopulationRecordDto createPopulationRecordDto) {
        PopulationRecords populationRecords = new PopulationRecords();
        populationRecords.setYear(createPopulationRecordDto.getYear().intValue());
        populationRecords.setPopulation(createPopulationRecordDto.getPopulation().longValue());
        populationRecords.setAnnualGrowth(createPopulationRecordDto.getAnnualGrowth());
        populationRecords.setCountry(Countries.RWANDA);
        populationRecords.setGDPMillions(createPopulationRecordDto.getGDPMillions());
        populationRecords.setGDPPerCapita(createPopulationRecordDto.getGDPPerCapita());
        populationRecords.setKigaliGDP(createPopulationRecordDto.getKigaliGDP());
        populationRecords.setNumberOfKigaliHouseholds(createPopulationRecordDto.getNumberOfKigaliHouseholds().intValue());
        return populationRecordRepository.save(populationRecords);
    }

    @Override
    public List<PopulationRecords> getAllPopulationRecords() {
        return populationRecordRepository.findAll();
    }

    @Override
    public PopulationRecords getPopulationRecordById(UUID id) {
        return populationRecordRepository.findById(id).orElseThrow(() -> new RuntimeException("Population record not found"));
    }

    @Override
    public PopulationRecords getPopulationRecordByYear(int year) {
        return populationRecordRepository.findByYear(year);
    }

    @Override
    public List<PopulationRecords> readPopulationRecordsFromExcel(MultipartFile file) {
        try {
            List<CreatePopulationRecordDto> populationRecords = ExcelReader.readExcel(file.getInputStream(), CreatePopulationRecordDto.class, ExcelType.POPULATION_RECORDS);
            List<PopulationRecords> savedPopulationRecords = new ArrayList<>();
            for (CreatePopulationRecordDto populationRecord : populationRecords) {
                CreateFuelDto createFuelDto = new CreateFuelDto();

                //Create Population Record
                savedPopulationRecords.add(createPopulationRecord(populationRecord));
            }
            return savedPopulationRecords;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public PopulationRecords updatePopulationRecord(UUID id, CreatePopulationRecordDto createPopulationRecordDto) {
        PopulationRecords populationRecords = populationRecordRepository.findById(id).orElseThrow(() -> new RuntimeException("Population record not found"));
        populationRecords.setYear(createPopulationRecordDto.getYear().intValue());
        populationRecords.setPopulation(createPopulationRecordDto.getPopulation().longValue());
        populationRecords.setAnnualGrowth(createPopulationRecordDto.getAnnualGrowth());
        populationRecords.setCountry(Countries.RWANDA);
        populationRecords.setGDPMillions(createPopulationRecordDto.getGDPMillions());
        populationRecords.setGDPPerCapita(createPopulationRecordDto.getGDPPerCapita());
        populationRecords.setKigaliGDP(createPopulationRecordDto.getKigaliGDP());
        populationRecords.setNumberOfKigaliHouseholds(createPopulationRecordDto.getNumberOfKigaliHouseholds().intValue());
        return populationRecordRepository.save(populationRecords);
    }

    @Override
    public List<PopulationRecords> getPopulationRecordsByCountry(Countries country) {
        return populationRecordRepository.findByCountry(country);
    }
}
    