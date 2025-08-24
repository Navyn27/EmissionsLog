package com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions;

import com.navyn.emissionlog.Enums.Agriculture.CropTypes;
import com.navyn.emissionlog.Enums.Agriculture.LandUseCategory;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgricultureAbstractClass;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class CropResiduesEmissions extends AgricultureAbstractClass {

   //DM is Dry Matter
   //AG is Above Ground

   @Enumerated(EnumType.STRING)
   private LandUseCategory landUseCategory;

   @Enumerated(EnumType.STRING)
   private CropTypes cropTypes;

   private double totalAreaHarvested = 0.0;
   private double harvestedFreshCropYield = 0.0;
   private double DMFractionOfHarvestedCrop = 0.0;
   private double harvestedDMYield = 0.0;
   private double AGResiduesDryMatter = 0.0;
   private double ratioOfAGResiduesDMToHarvestedYield = 0.0;
   private double NInCropResiduesReturned = 0.0;
   private double N2ONEmissions = 0.0;
   private double N2OEmissions = 0.0;
   private double CO2EqEmissions = 0.0;
}
