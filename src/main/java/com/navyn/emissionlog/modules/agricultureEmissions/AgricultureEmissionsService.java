package com.navyn.emissionlog.modules.agricultureEmissions;

import com.navyn.emissionlog.modules.agricultureEmissions.models.*;

import java.util.List;

public interface AgricultureEmissionsService {
    List<AquacultureEmissions> getAllAquacultureEmissions();
    List<EntericFermentationEmissions> getAllEntericFermentationEmissions();
    List<LimingEmissions> getAllLimingEmissions();
    List<ManureMgmtEmissions> getAllManureMgmtEmissions();
    List<RiceCultivationEmissions> getAllRiceCultivationEmissions();
    List<SyntheticFertilizerEmissions> getAllSyntheticFertilizerEmissions();
    List<UreaEmissions> getAllUreaEmissions();

    AquacultureEmissions createAquacultureEmissions(AquacultureEmissionsDto emissions);
    EntericFermentationEmissions createEntericFermentationEmissions(EntericFermentationEmissionsDto emissionsDto);

    LimingEmissions createLimingEmissions(LimingEmissionsDto emissions);
    ManureMgmtEmissions createManureMgmtEmissions(ManureMgmtEmissionsDto emissionsDto);

    RiceCultivationEmissions createRiceCultivationEmissions(RiceCultivationEmissionsDto emissions);
    SyntheticFertilizerEmissions createSyntheticFertilizerEmissions(SyntheticFertilizerEmissionsDto emissions);
    UreaEmissions createUreaEmissions(UreaEmissionsDto emissions);

}
