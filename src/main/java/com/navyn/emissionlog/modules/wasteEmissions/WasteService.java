package com.navyn.emissionlog.Services;

import com.navyn.emissionlog.Enums.SolidWasteType;
import com.navyn.emissionlog.Enums.WasteType;
import com.navyn.emissionlog.modules.wasteEmissions.models.SolidWasteData;
import com.navyn.emissionlog.modules.wasteEmissions.models.WasteDataAbstract;
import com.navyn.emissionlog.modules.wasteEmissions.dtos.GeneralWasteByPopulationDto;
import com.navyn.emissionlog.modules.wasteEmissions.dtos.IndustrialWasteDto;
import com.navyn.emissionlog.modules.wasteEmissions.dtos.SolidWasteDto;
import com.navyn.emissionlog.modules.wasteEmissions.dtos.WasteWaterDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface WasteService {
    WasteDataAbstract createIndustrialWasteWaterData(IndustrialWasteDto wasteData);

    WasteDataAbstract createSolidWasteData(SolidWasteDto wasteData);

    WasteDataAbstract createWasteWaterData(WasteWaterDto wasteData);

    WasteDataAbstract createBioTreatedWasteWaterData(GeneralWasteByPopulationDto wasteData);

    WasteDataAbstract createBurntWasteData(GeneralWasteByPopulationDto wasteData);

    WasteDataAbstract createIncinerationWasteData(GeneralWasteByPopulationDto wasteData);

    List<WasteDataAbstract> getAllWasteData();

    List<WasteDataAbstract> getWasteDataByType(WasteType wasteType);

    //populate population affiliated waste data
    List<WasteDataAbstract> populatePopulationAffiliatedWasteData();

    List<WasteDataAbstract> populateIndustrialWasteData(MultipartFile file) throws IOException;

    List<WasteDataAbstract> populateSolidWasteData(MultipartFile file) throws IOException;

    List<SolidWasteData> getSolidWasteDataByType(SolidWasteType solidWasteType);
}
