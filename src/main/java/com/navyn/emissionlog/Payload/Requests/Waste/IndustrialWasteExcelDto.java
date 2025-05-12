package com.navyn.emissionlog.Payload.Requests.Waste;

import lombok.Data;

@Data
public class IndustrialWasteExcelDto {
    private Double year;
    private Double sugarProductionAmount;
    private Double beerProductionAmount;
    private Double dairyProductionAmount;
    private Double meatAndPoultryProductionAmount;
}
