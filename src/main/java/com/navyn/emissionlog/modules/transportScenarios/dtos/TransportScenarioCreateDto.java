package com.navyn.emissionlog.modules.transportScenarios.dtos;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransportScenarioCreateDto {

    @NotNull(message = "Name is required")
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Base year is required")
    @Min(value = 1990, message = "Base year must be at least 1990")
    @Max(value = 2100, message = "Base year cannot exceed 2100")
    private Integer baseYear;

    @NotNull(message = "End year is required")
    @Min(value = 1990, message = "End year must be at least 1990")
    @Max(value = 2100, message = "End year cannot exceed 2100")
    private Integer endYear;
}
