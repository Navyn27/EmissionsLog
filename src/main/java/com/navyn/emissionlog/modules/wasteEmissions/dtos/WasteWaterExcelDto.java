package com.navyn.emissionlog.modules.wasteEmissions.dtos;

import lombok.Data;

@Data
public class WasteWaterExcelDto {
    private Double year;
    private String scope;
    private Integer eicvReportYear;
}
