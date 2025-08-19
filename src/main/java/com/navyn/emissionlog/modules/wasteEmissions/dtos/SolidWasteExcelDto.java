package com.navyn.emissionlog.modules.wasteEmissions.dtos;

import lombok.Data;

@Data
public class SolidWasteExcelDto {
    private Double year;
    private Double foodDepositedAmount;
    private Double gardenDepositedAmount;
    private Double paperDepositedAmount;
    private Double woodDepositedAmount;
    private Double textilesDepositedAmount;
    private Double nappiesDepositedAmount;
    private Double sludgeDepositedAmount;
    private Double mswDepositedAmount;
    private Double industryDepositedAmount;
    private Double methaneRecovery = 0.0;
}
