package com.navyn.emissionlog.Services;

import com.navyn.emissionlog.Enums.WasteType;
import com.navyn.emissionlog.Models.WasteData.WasteDataAbstract;
import com.navyn.emissionlog.Payload.Requests.Waste.GeneralWasteByPopulationDto;
import com.navyn.emissionlog.Payload.Requests.Waste.IndustrialWasteDto;
import com.navyn.emissionlog.Payload.Requests.Waste.SolidWasteDto;

import java.util.List;

public interface WasteService {
    WasteDataAbstract createIndustrialWasteWaterData(IndustrialWasteDto wasteData);

    WasteDataAbstract createSolidWasteData(SolidWasteDto wasteData);

    WasteDataAbstract createWasteWaterData(GeneralWasteByPopulationDto wasteData);

    WasteDataAbstract createBioTreatedWasteWaterData(GeneralWasteByPopulationDto wasteData);

    WasteDataAbstract createBurntWasteData(GeneralWasteByPopulationDto wasteData);

    WasteDataAbstract createIncinerationWasteData(GeneralWasteByPopulationDto wasteData);

    List<WasteDataAbstract> getAllWasteData();

    List<WasteDataAbstract> getWasteDataByType(WasteType wasteType);
}
