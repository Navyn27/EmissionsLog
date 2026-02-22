package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class KigaliWWTPParameterResponseDto {
    private UUID id;
    private Double methaneEmissionFactor; // kg CH4 per kg COD
    private Double codConcentration; // kg COD per m³
    private Double ch4Gwp100Year; // kg CO2e per kg CH4
    private Double totalNKgPerM3;
    private Double n2oEfPlant;
    private Double n2oEfEffluent;
    @JsonProperty("nRemovalEfficiency")
    private Double nRemovalEfficiency;
    private Double n2oGwp100Year;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

