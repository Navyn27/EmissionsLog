package com.navyn.emissionlog.modules.agricultureEmissions;

import com.navyn.emissionlog.Enums.Agriculture.*;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.*;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.DirectLandEmissions.*;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.IndirectLandEmissions.AtmosphericDepositionEmissionsDto;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.IndirectLandEmissions.LeachingAndRunoffEmissionsDto;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.IndirectManureEmissions.*;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.Livestock.EntericFermentationEmissionsDto;

import com.navyn.emissionlog.modules.agricultureEmissions.dtos.Livestock.ManureManagementEmissionsDto;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.*;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions.*;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.IndirectLandEmissions.AtmosphericDepositionEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.IndirectLandEmissions.LeachingAndRunoffEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.IndirectManureEmissions.*;
import com.navyn.emissionlog.modules.agricultureEmissions.models.Livestock.EntericFermentationEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.Livestock.ManureManagementEmissions;

import java.util.List;

public interface AgricultureEmissionsService {
    List<AquacultureEmissions> getAllAquacultureEmissions(Integer year);
    List<EntericFermentationEmissions> getAllEntericFermentationEmissions(Integer year, LivestockSpecies species);
    List<LimingEmissions> getAllLimingEmissions(Integer year, LimingMaterials limingMaterials);
    List<AnimalManureAndCompostEmissions> getAllAnimalManureAndCompostEmissions(Integer year, OrganicAmendmentTypes amendmentType, LivestockSpecies species);
    List<RiceCultivationEmissions> getAllRiceCultivationEmissions(String riceEcosystem, WaterRegime waterRegime, Integer year);
    List<SyntheticFertilizerEmissions> getAllSyntheticFertilizerEmissions(Integer year, CropTypes cropType, Fertilizers fertilizerType);
    List<UreaEmissions> getAllUreaEmissions(String fertilizer, Integer year);

    AquacultureEmissions createAquacultureEmissions(AquacultureEmissionsDto emissions);
    EntericFermentationEmissions createEntericFermentationEmissions(EntericFermentationEmissionsDto emissionsDto);

    LimingEmissions createLimingEmissions(LimingEmissionsDto emissions);
    AnimalManureAndCompostEmissions createAnimalManureAndCompostEmissions(AnimalManureAndCompostEmissionsDto emissionsDto);

    RiceCultivationEmissions createRiceCultivationEmissions(RiceCultivationEmissionsDto emissions);
    SyntheticFertilizerEmissions createSyntheticFertilizerEmissions(SyntheticFertilizerEmissionsDto emissions);
    UreaEmissions createUreaEmissions(UreaEmissionsDto emissions);

    BurningEmissions createBurningEmissions(BurningEmissionsDto burningEmissionsDto);

    List<BurningEmissions> getAllBurningEmissions(Integer year, BurningAgentType forestType);

    CropResiduesEmissions createCropResidueEmissions(CropResiduesEmissionsDto cropResidueEmissionsDto);

    List<CropResiduesEmissions> getAllCropResidueEmissions(Integer year, CropResiduesCropType cropType, LandUseCategory landUseCategory);

    PastureExcretionEmissions createPastureExcretionEmissions(PastureExcretionsEmissionsDto pastureExcretionEmissionsDto);

    MineralSoilEmissions createMineralSoilEmissions(MineralSoilEmissionsDto mineralSoilEmissionsDto);

    List<MineralSoilEmissions> getAllMineralSoilEmissions(Integer year, LandUseCategory initialLandUse, LandUseCategory landUseInReportingYear);

    List<PastureExcretionEmissions> getAllPastureExcretionEmissions(Integer year, LivestockSpecies species, MMS mms);

    VolatilizationEmissions createVolatilizationEmissions(VolatilizationEmissionsDto volatilizationEmissionsDto);

    LeachingEmissions createLeachingEmissions(LeachingEmissionsDto leachingEmissionsDto);

    AtmosphericDepositionEmissions createAtmosphericNDepositionEmissions(AtmosphericDepositionEmissionsDto atmosphericNDepositionEmissionsDto);

    LeachingAndRunoffEmissions createLeachingAndRunoffEmissions(LeachingAndRunoffEmissionsDto leachingAndRunoffEmissionsDto);

    List<AtmosphericDepositionEmissions> getAllAtmosphericNDepositionEmissions(Integer year, LandUseCategory landUseCategory);

    List<LeachingAndRunoffEmissions> getAllLeachingAndRunoffEmissions(Integer year, LandUseCategory landUseCategory);

    List<LeachingEmissions> getAllLeachingEmissions(Integer year, MMS mms, LivestockSpecies species);

    List<VolatilizationEmissions> getAllVolatilizationEmissions(Integer year, MMS mms, LivestockSpecies species);
//
//    ManureManagementEmissions createAnimalManureMgmtEmissions(ManureManagementEmissionsDto manureAndCompostEmissionsDto);
//
//    List<ManureManagementEmissions> getAllAnimalManureMgmtEmissions(Integer year, LivestockSpecies species, MMS mms);

}
