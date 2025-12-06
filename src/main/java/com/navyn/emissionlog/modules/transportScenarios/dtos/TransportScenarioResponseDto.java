package com.navyn.emissionlog.modules.transportScenarios.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class TransportScenarioResponseDto {

    private UUID id;
    private String name;
    private String description;
    private Integer baseYear;
    private Integer endYear;
    private Instant createdAt;
    private Instant updatedAt;
}
