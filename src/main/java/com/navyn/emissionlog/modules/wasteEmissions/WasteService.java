package com.navyn.emissionlog.modules.wasteEmissions;

import com.navyn.emissionlog.Enums.Waste.SolidWasteType;
import com.navyn.emissionlog.Enums.Waste.WasteType;
import com.navyn.emissionlog.modules.wasteEmissions.models.SolidWasteData;
import com.navyn.emissionlog.modules.wasteEmissions.models.WasteDataAbstract;
import com.navyn.emissionlog.modules.wasteEmissions.dtos.GeneralWasteByPopulationDto;
import com.navyn.emissionlog.modules.wasteEmissions.dtos.IndustrialWasteDto;
import com.navyn.emissionlog.modules.wasteEmissions.dtos.SolidWasteDto;
import com.navyn.emissionlog.modules.wasteEmissions.dtos.WasteWaterDto;
import com.navyn.emissionlog.utils.DashboardData;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface WasteService {
    WasteDataAbstract createIndustrialWasteWaterData(IndustrialWasteDto wasteData);

    WasteDataAbstract createSolidWasteData(SolidWasteDto wasteData);

    WasteDataAbstract createWasteWaterData(WasteWaterDto wasteData);

    WasteDataAbstract createBioTreatedWasteWaterData(GeneralWasteByPopulationDto wasteData);

    WasteDataAbstract createBurntWasteData(GeneralWasteByPopulationDto wasteData);

    WasteDataAbstract createIncinerationWasteData(GeneralWasteByPopulationDto wasteData);

    List<WasteDataAbstract> getAllWasteData();

    List<WasteDataAbstract> getWasteData(WasteType wasteType, Integer year, UUID regionId);

    //populate population affiliated waste data
    List<WasteDataAbstract> populatePopulationAffiliatedWasteData();

    List<WasteDataAbstract> populateIndustrialWasteData(MultipartFile file) throws IOException;

    List<WasteDataAbstract> populateSolidWasteData(MultipartFile file) throws IOException;

    List<SolidWasteData> getSolidWasteData(SolidWasteType solidWasteType, Integer year, UUID regionId);
    
    // Mini Dashboards
    DashboardData getWasteDashboardSummary(Integer startingYear, Integer endingYear);
    
    List<DashboardData> getWasteDashboardGraph(Integer startingYear, Integer endingYear);
}
