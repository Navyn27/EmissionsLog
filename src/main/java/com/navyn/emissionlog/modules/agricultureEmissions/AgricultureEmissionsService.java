package com.navyn.emissionlog.modules.agricultureEmissions;

import com.navyn.emissionlog.Enums.Agriculture.*;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.*;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.DirectLand.*;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.Livestock.EntericFermentationEmissionsDto;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.*;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions.*;
import com.navyn.emissionlog.modules.agricultureEmissions.models.Livestock.EntericFermentationEmissions;

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
}
