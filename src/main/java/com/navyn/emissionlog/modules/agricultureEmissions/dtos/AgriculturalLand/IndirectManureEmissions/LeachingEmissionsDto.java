package com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.IndirectManureEmissions;

import com.navyn.emissionlog.Enums.Agriculture.LivestockSpecies;
import com.navyn.emissionlog.Enums.Agriculture.MMS;
import lombok.Data;

@Data
public class LeachingEmissionsDto {

    private Integer year;
    private MMS mms;
    private LivestockSpecies livestockSpecies;
    private double MMSExcretionAmount;

}
