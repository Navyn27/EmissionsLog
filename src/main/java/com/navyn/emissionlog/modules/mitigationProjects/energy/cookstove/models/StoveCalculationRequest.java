package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StoveCalculationRequest {
    private StoveType stoveType;
    private List<Integer> unitsInstalledPerYear;
    private int startYear;
}
