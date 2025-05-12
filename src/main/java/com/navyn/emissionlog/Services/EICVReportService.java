package com.navyn.emissionlog.Services;

import com.navyn.emissionlog.Models.EICVReport;
import com.navyn.emissionlog.Payload.Requests.EICVReportDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface EICVReportService {
    EICVReport createEICVReport(EICVReportDto eicvReportDto);
    EICVReport getEICVReportByYear(int year);
    EICVReport getEICVReportById(UUID id);
    List<EICVReport> findAll();
    List<EICVReport> createReportsFromExcel(MultipartFile file);

    EICVReport updateEICVReport(UUID eicvReportId, EICVReportDto eicvReportDto);
}
