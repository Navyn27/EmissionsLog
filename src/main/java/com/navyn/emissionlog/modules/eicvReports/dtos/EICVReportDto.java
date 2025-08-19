package com.navyn.emissionlog.modules.eicvReports.dtos;

import lombok.Data;

@Data
public class EICVReportDto {
    private String name;
    private Double year;
    private Double totalImprovedSanitation;
    private Double improvedTypeNotSharedWithOtherHH;
    private Double flushToilet;
    private Double protectedLatrines;
    private Double unprotectedLatrines;
    private Double others;
    private Double noToiletFacilities;
    private Double totalHouseholds;
}
