package com.navyn.emissionlog.ServiceImpls;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.Models.EICVReport;
import com.navyn.emissionlog.Payload.Requests.EICVReportDto;
import com.navyn.emissionlog.Repositories.EICVReportRepository;
import com.navyn.emissionlog.Services.EICVReportService;
import com.navyn.emissionlog.Utils.ExcelReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EICVReportServiceImpl implements EICVReportService {

    @Autowired
    private EICVReportRepository eicvReportRepository;

    @Override
    public EICVReport createEICVReport(EICVReportDto eicvReportDto) {
        EICVReport eicvReport = new EICVReport();
        eicvReport.setYear(eicvReportDto.getYear());
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
        return eicvReportRepository.findByYear(year);
    }

    @Override
    public EICVReport getEICVReportById(UUID id) {
        return eicvReportRepository.findById(id).orElseThrow(() -> new RuntimeException("EICV Report not found"));
    }

    @Override
    public List<EICVReport> findAll(){
        return eicvReportRepository.findAll();
    }

    @Override
    public List<EICVReport> createReportsFromExcel(MultipartFile file){
        List<EICVReport> savedEicvReports = new ArrayList<>();
        try {
            List<EICVReportDto> eicvReports = ExcelReader.readEmissionsExcel(file.getInputStream(), EICVReportDto.class, ExcelType.EICV_REPORT);
            for (EICVReportDto eicvReport : eicvReports) {
                savedEicvReports.add(createEICVReport(eicvReport));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading EICV Reports from Excel file", e);
        }
        return savedEicvReports;
    }
}
