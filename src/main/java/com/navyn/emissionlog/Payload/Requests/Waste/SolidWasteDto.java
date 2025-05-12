package com.navyn.emissionlog.Payload.Requests.Waste;

import com.navyn.emissionlog.Enums.MassUnits;
import com.navyn.emissionlog.Enums.Scopes;
import com.navyn.emissionlog.Enums.SolidWasteType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SolidWasteDto {

    @NotNull(message = "The solid waste type is a required field")
    private SolidWasteType solidWasteType;

    @NotNull(message = "Please provide the amount of solid waste deposited in kg")
    private Double amountDeposited;

    private MassUnits massUnit = MassUnits.KILOGRAM;

    @NotNull(message = "Please provide the emission scope")
    private Scopes scope;

    @NotNull(message = "Please provide the year of emissions")
    private LocalDateTime activityYear = LocalDateTime.now();

    @NotNull(message = "Please provide the region of emissions")
    private UUID region;

    @NotNull(message = "Please provide the recovered amount")
    private Double methaneRecovery;
}