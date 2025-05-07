package com.navyn.emissionlog.Payload.Requests.Waste;

import com.navyn.emissionlog.Models.PopulationRecords;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class IndustrialWasteDto extends GeneralWasteByPopulationDto{

    @NotNull(message = "Please provide the amount of sugar produced in kg")
    private Double sugarProductionAmount;

    @NotNull(message = "Please provide the amount of beer produced in kg")
    private Double bearProductionAmount;

    @NotNull(message = "Please provide the amount of dairy products produced in kg")
    private Double dairyProductionAmount;

    @NotNull(message = "Please provide the amount of meat and poultry products produced in kg")
    private Double meatAndPoultryProductionAmount;
}
