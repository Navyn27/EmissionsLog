package com.navyn.emissionlog.modules.wasteEmissions.dtos;

import com.navyn.emissionlog.Enums.Metrics.MassUnits;
import com.navyn.emissionlog.Enums.Scopes;
import com.navyn.emissionlog.Enums.Waste.SolidWasteType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SolidWasteDto {

    @NotNull(message = "Waste type is required")
    private SolidWasteType solidWasteType;

    @NotNull(message = "Amount deposited is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount deposited must be greater than 0")
    private Double amountDeposited;

    private MassUnits massUnit = MassUnits.KILOGRAM;

    @NotNull(message = "Emission scope is required")
    private Scopes scope;

    @NotNull(message = "Activity year is required")
    private LocalDateTime activityYear = LocalDateTime.now();

    @NotNull(message = "Region is required")
    private UUID region;

    @NotNull(message = "Methane recovery amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Methane recovery cannot be negative")
    private Double methaneRecovery;
}