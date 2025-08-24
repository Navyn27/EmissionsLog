package com.navyn.emissionlog.modules.wasteEmissions.dtos;

import com.navyn.emissionlog.Enums.Metrics.MassUnits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IndustrialWasteDto extends GeneralWasteByPopulationDto{

    @NotNull(message = "Please provide the amount of sugar produced in kg")
    private Double sugarProductionAmount;

    private MassUnits sugarProductionMetric = MassUnits.KILOGRAM;

    @NotNull(message = "Please provide the amount of beer produced in kg")
    private Double beerProductionAmount;

    private MassUnits beerProductionMetric = MassUnits.KILOGRAM;

    @NotNull(message = "Please provide the amount of dairy products produced in kg")
    private Double dairyProductionAmount;

    private MassUnits dairyProductionMetric = MassUnits.KILOGRAM;

    @NotNull(message = "Please provide the amount of meat and poultry products produced in kg")
    private Double meatAndPoultryProductionAmount;

    private MassUnits meatAndPoultryProductionMetric = MassUnits.KILOGRAM;
}
