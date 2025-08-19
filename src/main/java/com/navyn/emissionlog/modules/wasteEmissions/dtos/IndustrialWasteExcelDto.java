package com.navyn.emissionlog.modules.wasteEmissions.dtos;

import lombok.Data;

@Data
public class IndustrialWasteExcelDto {
    private Double year;
    private Double sugarProductionAmount;
    private Double beerProductionAmount;
    private Double dairyProductionAmount;
    private Double meatAndPoultryProductionAmount;
}
