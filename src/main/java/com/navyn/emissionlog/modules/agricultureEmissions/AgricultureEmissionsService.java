package com.navyn.emissionlog.modules.agricultureEmissions;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.modules.agricultureEmissions.dtos.*;
import com.navyn.emissionlog.modules.agricultureEmissions.models.*;

import java.util.List;

public interface AgricultureEmissionsService {
    List<AquacultureEmissions> getAllAquacultureEmissions(Integer year);
    List<EntericFermentationEmissions> getAllEntericFermentationEmissions(Integer year, LivestockSpecies species);
    List<LimingEmissions> getAllLimingEmissions(Integer year, LimingMaterials limingMaterials);
    List<ManureMgmtEmissions> getAllManureMgmtEmissions(Integer year, OrganicAmendmentTypes amendmentType, LivestockSpecies species);
    List<RiceCultivationEmissions> getAllRiceCultivationEmissions(String riceEcosystem, WaterRegime waterRegime, Integer year);
    List<SyntheticFertilizerEmissions> getAllSyntheticFertilizerEmissions(Integer year, CropTypes cropType, Fertilizers fertilizerType);
    List<UreaEmissions> getAllUreaEmissions(String fertilizer, Integer year);

    AquacultureEmissions createAquacultureEmissions(AquacultureEmissionsDto emissions);
    EntericFermentationEmissions createEntericFermentationEmissions(EntericFermentationEmissionsDto emissionsDto);

    LimingEmissions createLimingEmissions(LimingEmissionsDto emissions);
    ManureMgmtEmissions createManureMgmtEmissions(ManureMgmtEmissionsDto emissionsDto);

    RiceCultivationEmissions createRiceCultivationEmissions(RiceCultivationEmissionsDto emissions);
    SyntheticFertilizerEmissions createSyntheticFertilizerEmissions(SyntheticFertilizerEmissionsDto emissions);
    UreaEmissions createUreaEmissions(UreaEmissionsDto emissions);

}
