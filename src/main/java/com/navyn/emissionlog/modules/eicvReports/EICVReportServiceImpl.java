package com.navyn.emissionlog.modules.eicvReports;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.modules.eicvReports.dtos.EICVReportDto;
import com.navyn.emissionlog.utils.ExcelReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EICVReportServiceImpl implements EICVReportService {

    private final EICVReportRepository eicvReportRepository;

    @Override
    public EICVReport createEICVReport(EICVReportDto eicvReportDto) {
        EICVReport eicvReport = new EICVReport();
        eicvReport.setName(eicvReportDto.getName());
        eicvReport.setYear(eicvReportDto.getYear().intValue());
        eicvReport.setFlushToilet(eicvReportDto.getFlushToilet());
        eicvReport.setProtectedLatrines(eicvReportDto.getProtectedLatrines());
        eicvReport.setImprovedTypeNotSharedWithOtherHH(eicvReportDto.getImprovedTypeNotSharedWithOtherHH());
        eicvReport.setUnprotectedLatrines(eicvReportDto.getUnprotectedLatrines());
        eicvReport.setTotalHouseholds(eicvReportDto.getTotalHouseholds());
        eicvReport.setNoToiletFacilities(eicvReportDto.getNoToiletFacilities());
        eicvReport.setTotalImprovedSanitation(eicvReportDto.getTotalImprovedSanitation());
        eicvReport.setOthers(eicvReportDto.getOthers());
        return eicvReportRepository.save(eicvReport);
    }

    @Override
    public EICVReport getEICVReportByYear(int year) {
        Optional<EICVReport> eicvReport = eicvReportRepository.findByYear(year);
        return eicvReport.orElse(null);
    }

    @Override
    public EICVReport getEICVReportById(UUID id) {
        return eicvReportRepository.findById(id).orElseThrow(() -> new RuntimeException("EICV Report not found"));
    }

    @Override
    public List<EICVReport> findAll(String name, Integer year) {
        if(name != null && year == null) {
            return eicvReportRepository.findByNameContainingIgnoreCaseOrderByYearDesc(name);
        }
        if(name == null && year != null) {
            return List.of(getEICVReportByYear(year));
        }
        if(name != null) {
            return eicvReportRepository.findByNameContainingIgnoreCaseAndYearOrderByYearDesc(name, year);
        }
        return eicvReportRepository.findAllByOrderByYearDesc();
    }

    @Override
    public List<EICVReport> createReportsFromExcel(MultipartFile file){
        List<EICVReport> savedEicvReports = new ArrayList<>();
        try {
            List<EICVReportDto> eicvReports = ExcelReader.readExcel(file.getInputStream(), EICVReportDto.class, ExcelType.EICV_REPORT);
            for (EICVReportDto eicvReport : eicvReports) {
                savedEicvReports.add(createEICVReport(eicvReport));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading EICV Reports from Excel file", e);
        }
        return savedEicvReports;
    }

    @Override
    public EICVReport updateEICVReport(UUID eicvReportId, EICVReportDto eicvReportDto) {
        EICVReport eicvReport = eicvReportRepository.findById(eicvReportId).orElseThrow(() -> new RuntimeException("EICV Report not found"));
        eicvReport.setName(eicvReportDto.getName());
        eicvReport.setYear(eicvReportDto.getYear().intValue());
        eicvReport.setFlushToilet(eicvReportDto.getFlushToilet());
        eicvReport.setProtectedLatrines(eicvReportDto.getProtectedLatrines());
        eicvReport.setImprovedTypeNotSharedWithOtherHH(eicvReportDto.getImprovedTypeNotSharedWithOtherHH());
        eicvReport.setUnprotectedLatrines(eicvReportDto.getUnprotectedLatrines());
        eicvReport.setTotalHouseholds(eicvReportDto.getTotalHouseholds());
        eicvReport.setNoToiletFacilities(eicvReportDto.getNoToiletFacilities());
        eicvReport.setTotalImprovedSanitation(eicvReportDto.getTotalImprovedSanitation());
        eicvReport.setOthers(eicvReportDto.getOthers());
        return eicvReportRepository.save(eicvReport);
    }

    @Override
    public EICVReport getEICVReportsByName(String name) {
        return eicvReportRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("EICV Report with name " + name + " not found"));
    }
}
