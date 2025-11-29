package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
public class StoveInstallationDTO {
    private int year;
    private UUID stoveTypeId;
    private int unitsInstalledThisYear;
}
