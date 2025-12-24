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
import com.navyn.emissionlog.utils.DashboardData;

import java.util.List;
import java.util.UUID;

public interface AgricultureEmissionsService {
    List<AquacultureEmissions> getAllAquacultureEmissions(Integer year);

    List<EntericFermentationEmissions> getAllEntericFermentationEmissions(Integer year, LivestockSpecies species);

    List<LimingEmissions> getAllLimingEmissions(Integer year, LimingMaterials limingMaterials);

    List<AnimalManureAndCompostEmissions> getAllAnimalManureAndCompostEmissions(Integer year,
            OrganicAmendmentTypes amendmentType, LivestockSpecies species);

    List<RiceCultivationEmissions> getAllRiceCultivationEmissions(String riceEcosystem, WaterRegime waterRegime,
            Integer year);

    List<SyntheticFertilizerEmissions> getAllSyntheticFertilizerEmissions(Integer year, CropTypes cropType,
            Fertilizers fertilizerType);

    List<UreaEmissions> getAllUreaEmissions(String fertilizer, Integer year);

    AquacultureEmissions createAquacultureEmissions(AquacultureEmissionsDto emissions);

    AquacultureEmissions updateAquacultureEmissions(UUID id, AquacultureEmissionsDto emissions);

    void deleteAquacultureEmissions(UUID id);

    EntericFermentationEmissions createEntericFermentationEmissions(EntericFermentationEmissionsDto emissionsDto);

    EntericFermentationEmissions updateEntericFermentationEmissions(UUID id,
            EntericFermentationEmissionsDto emissionsDto);

    void deleteEntericFermentationEmissions(UUID id);

    LimingEmissions createLimingEmissions(LimingEmissionsDto emissions);

    LimingEmissions updateLimingEmissions(UUID id, LimingEmissionsDto emissions);

    void deleteLimingEmissions(UUID id);

    AnimalManureAndCompostEmissions createAnimalManureAndCompostEmissions(
            AnimalManureAndCompostEmissionsDto emissionsDto);

    AnimalManureAndCompostEmissions updateAnimalManureAndCompostEmissions(UUID id,
            AnimalManureAndCompostEmissionsDto emissionsDto);

    void deleteAnimalManureAndCompostEmissions(UUID id);

    RiceCultivationEmissions createRiceCultivationEmissions(RiceCultivationEmissionsDto emissions);

    RiceCultivationEmissions updateRiceCultivationEmissions(UUID id, RiceCultivationEmissionsDto emissions);

    void deleteRiceCultivationEmissions(UUID id);

    SyntheticFertilizerEmissions createSyntheticFertilizerEmissions(SyntheticFertilizerEmissionsDto emissions);

    SyntheticFertilizerEmissions updateSyntheticFertilizerEmissions(UUID id, SyntheticFertilizerEmissionsDto emissions);

    void deleteSyntheticFertilizerEmissions(UUID id);

    UreaEmissions createUreaEmissions(UreaEmissionsDto emissions);

    UreaEmissions updateUreaEmissions(UUID id, UreaEmissionsDto emissions);

    void deleteUreaEmissions(UUID id);

    BurningEmissions createBurningEmissions(BurningEmissionsDto burningEmissionsDto);

    BurningEmissions updateBurningEmissions(UUID id, BurningEmissionsDto burningEmissionsDto);

    void deleteBurningEmissions(UUID id);

    List<BurningEmissions> getAllBurningEmissions(Integer year, BurningAgentType forestType);

    CropResiduesEmissions createCropResidueEmissions(CropResiduesEmissionsDto cropResidueEmissionsDto);

    CropResiduesEmissions updateCropResidueEmissions(UUID id, CropResiduesEmissionsDto cropResidueEmissionsDto);

    void deleteCropResidueEmissions(UUID id);

    List<CropResiduesEmissions> getAllCropResidueEmissions(Integer year, CropResiduesCropType cropType,
            LandUseCategory landUseCategory);

    PastureExcretionEmissions createPastureExcretionEmissions(
            PastureExcretionsEmissionsDto pastureExcretionEmissionsDto);

    PastureExcretionEmissions updatePastureExcretionEmissions(UUID id,
            PastureExcretionsEmissionsDto pastureExcretionEmissionsDto);

    void deletePastureExcretionEmissions(UUID id);

    MineralSoilEmissions createMineralSoilEmissions(MineralSoilEmissionsDto mineralSoilEmissionsDto);

    MineralSoilEmissions updateMineralSoilEmissions(UUID id, MineralSoilEmissionsDto mineralSoilEmissionsDto);

    void deleteMineralSoilEmissions(UUID id);

    List<MineralSoilEmissions> getAllMineralSoilEmissions(Integer year, LandUseCategory initialLandUse,
            LandUseCategory landUseInReportingYear);

    List<PastureExcretionEmissions> getAllPastureExcretionEmissions(Integer year, LivestockSpecies species, MMS mms);

    VolatilizationEmissions createVolatilizationEmissions(VolatilizationEmissionsDto volatilizationEmissionsDto);

    VolatilizationEmissions updateVolatilizationEmissions(UUID id,
            VolatilizationEmissionsDto volatilizationEmissionsDto);

    void deleteVolatilizationEmissions(UUID id);

    LeachingEmissions createLeachingEmissions(LeachingEmissionsDto leachingEmissionsDto);

    LeachingEmissions updateLeachingEmissions(UUID id, LeachingEmissionsDto leachingEmissionsDto);

    void deleteLeachingEmissions(UUID id);

    AtmosphericDepositionEmissions createAtmosphericNDepositionEmissions(
            AtmosphericDepositionEmissionsDto atmosphericNDepositionEmissionsDto);

    AtmosphericDepositionEmissions updateAtmosphericNDepositionEmissions(UUID id,
            AtmosphericDepositionEmissionsDto atmosphericNDepositionEmissionsDto);

    void deleteAtmosphericNDepositionEmissions(UUID id);

    LeachingAndRunoffEmissions createLeachingAndRunoffEmissions(
            LeachingAndRunoffEmissionsDto leachingAndRunoffEmissionsDto);

    LeachingAndRunoffEmissions updateLeachingAndRunoffEmissions(UUID id,
            LeachingAndRunoffEmissionsDto leachingAndRunoffEmissionsDto);

    void deleteLeachingAndRunoffEmissions(UUID id);

    List<AtmosphericDepositionEmissions> getAllAtmosphericNDepositionEmissions(Integer year,
            LandUseCategory landUseCategory);

    List<LeachingAndRunoffEmissions> getAllLeachingAndRunoffEmissions(Integer year, LandUseCategory landUseCategory);

    List<LeachingEmissions> getAllLeachingEmissions(Integer year, MMS mms, LivestockSpecies species);

    List<VolatilizationEmissions> getAllVolatilizationEmissions(Integer year, MMS mms, LivestockSpecies species);

    ManureManagementEmissions createManureManagementEmissions(ManureManagementEmissionsDto dto);

    ManureManagementEmissions updateManureManagementEmissions(UUID id, ManureManagementEmissionsDto dto);

    void deleteManureManagementEmissions(UUID id);

    ManureManagementEmissions getManureManagementEmissionsById(UUID id);

    List<ManureManagementEmissions> getAllManureManagementEmissions(Integer year, ManureManagementLivestock species);

    // Mini Dashboards
    DashboardData getAgricultureDashboardSummary(Integer startingYear, Integer endingYear);

    List<DashboardData> getAgricultureDashboardGraph(Integer startingYear, Integer endingYear);

}
