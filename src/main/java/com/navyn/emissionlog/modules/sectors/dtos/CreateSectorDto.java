package com.navyn.emissionlog.modules.sectors.dtos;

import lombok.Data;

@Data
public class CreateSectorDto {
    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}