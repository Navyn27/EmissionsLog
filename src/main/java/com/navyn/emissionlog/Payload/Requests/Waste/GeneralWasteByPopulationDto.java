package com.navyn.emissionlog.Payload.Requests.Waste;

import com.navyn.emissionlog.Enums.Scopes;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class GeneralWasteByPopulationDto {
    @NotNull(message = "Please provide the id of the population records")
    private UUID populationRecords;

    @NotNull(message = "Please provide the emission scope")
    private Scopes scope;

    @NotNull(message = "Please provide the year of emissions")
    private LocalDateTime activityYear = LocalDateTime.now();

    @NotNull(message = "Please provide the region of emissions")
    private UUID region;
}
