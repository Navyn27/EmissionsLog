package com.navyn.emissionlog.modules.populationRecords;
import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.modules.populationRecords.dtos.CreatePopulationRecordDto;
import com.navyn.emissionlog.utils.ExcelReader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PopulationRecordsServiceImpl implements PopulationRecordService {

    private static final int MIN_YEAR = 1990;
    private static final int MAX_YEAR = 2030;
    private static final long MAX_POPULATION_RWANDA = 50_000_000L;

    private final PopulationRecordsRepository populationRecordRepository;

    @Override
    public PopulationRecords createPopulationRecord(CreatePopulationRecordDto createPopulationRecordDto) {
        if (createPopulationRecordDto.getYear() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Year is required.");
        }
        int year = createPopulationRecordDto.getYear().intValue();
        if (year < MIN_YEAR || year > MAX_YEAR) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Year must be between " + MIN_YEAR + " and " + MAX_YEAR + ".");
        }
        if (createPopulationRecordDto.getPopulation() == null || createPopulationRecordDto.getPopulation() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Population must be a positive number.");
        }
        long population = createPopulationRecordDto.getPopulation().longValue();
        if (population > MAX_POPULATION_RWANDA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Population exceeds maximum plausible value for Rwanda (" + MAX_POPULATION_RWANDA + ").");
        }
        if (populationRecordRepository.existsByYear(year)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A population record already exists for year " + year + ". Please edit the existing record.");
        }
        if (createPopulationRecordDto.getNumberOfKigaliHouseholds() != null
                && createPopulationRecordDto.getNumberOfKigaliHouseholds() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Number of Kigali households cannot be negative.");
        }

        PopulationRecords populationRecords = new PopulationRecords();
        populationRecords.setYear(year);
        populationRecords.setPopulation(population);
        populationRecords.setAnnualGrowth(createPopulationRecordDto.getAnnualGrowth());
        populationRecords.setCountry(Countries.RWANDA);
        populationRecords.setGDPMillions(createPopulationRecordDto.getGDPMillions());
        populationRecords.setGDPPerCapita(createPopulationRecordDto.getGDPPerCapita());
        populationRecords.setKigaliGDP(createPopulationRecordDto.getKigaliGDP());
        populationRecords.setNumberOfKigaliHouseholds(createPopulationRecordDto.getNumberOfKigaliHouseholds() != null
                ? createPopulationRecordDto.getNumberOfKigaliHouseholds().intValue() : 0);
        return populationRecordRepository.save(populationRecords);
    }

    @Override
    public List<PopulationRecords> getAllPopulationRecords(Countries country, Integer year) {
        if (country != null && year == null) {
            return populationRecordRepository.findByCountryOrderByYearDesc(country);
        }
        if (country == null && year != null) {
            PopulationRecords populationRecord = getPopulationRecordByYear(year);
            return populationRecord != null ? List.of(populationRecord) : List.of();
        }
        if (country != null) {
            return populationRecordRepository.findByCountryAndYearOrderByYearDesc(country, year);
        }
        return populationRecordRepository.findAllByOrderByYearDesc();
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
        if (createPopulationRecordDto.getYear() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Year is required.");
        }
        int year = createPopulationRecordDto.getYear().intValue();
        if (year < MIN_YEAR || year > MAX_YEAR) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Year must be between " + MIN_YEAR + " and " + MAX_YEAR + ".");
        }
        if (createPopulationRecordDto.getPopulation() == null || createPopulationRecordDto.getPopulation() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Population must be a positive number.");
        }
        long population = createPopulationRecordDto.getPopulation().longValue();
        if (population > MAX_POPULATION_RWANDA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Population exceeds maximum plausible value for Rwanda (" + MAX_POPULATION_RWANDA + ").");
        }
        if (year != populationRecords.getYear() && populationRecordRepository.existsByYear(year)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A population record already exists for year " + year + ". Please use a different year.");
        }
        populationRecords.setYear(year);
        populationRecords.setPopulation(population);
        populationRecords.setAnnualGrowth(createPopulationRecordDto.getAnnualGrowth());
        populationRecords.setCountry(Countries.RWANDA);
        populationRecords.setGDPMillions(createPopulationRecordDto.getGDPMillions());
        populationRecords.setGDPPerCapita(createPopulationRecordDto.getGDPPerCapita());
        populationRecords.setKigaliGDP(createPopulationRecordDto.getKigaliGDP());
        populationRecords.setNumberOfKigaliHouseholds(createPopulationRecordDto.getNumberOfKigaliHouseholds() != null
                ? createPopulationRecordDto.getNumberOfKigaliHouseholds().intValue() : 0);
        return populationRecordRepository.save(populationRecords);
    }

    @Override
    public List<PopulationRecords> getPopulationRecordsByCountry(Countries country) {
        return populationRecordRepository.findByCountryOrderByYearDesc(country);
    }
}
    