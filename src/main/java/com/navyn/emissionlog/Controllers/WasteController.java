package com.navyn.emissionlog.Controllers;

import com.navyn.emissionlog.Enums.WasteType;
import com.navyn.emissionlog.Models.WasteData.WasteDataAbstract;
import com.navyn.emissionlog.Payload.Requests.Waste.*;
import com.navyn.emissionlog.Services.WasteService;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/waste")
public class WasteController {

    @Autowired
    private WasteService wasteService;

    @Operation(summary = "Create Industrial Waste Water Data and calculate emissions")
    @PostMapping("/industrialWasteWater")
    public WasteDataAbstract createIndustrialWasteWaterData(@RequestBody IndustrialWasteDto wasteData) {
        return wasteService.createIndustrialWasteWaterData(wasteData);
    }

    @Operation(summary = "Create Solid Waste Data and calculate emissions")
    @PostMapping("/solidWaste")
    public WasteDataAbstract createSolidWasteData(@RequestBody SolidWasteDto wasteData) {
        return wasteService.createSolidWasteData(wasteData);
    }

    @Operation(summary = "Create Waste water Data and calculate emissions")
    @PostMapping("/wasteWater")
    public WasteDataAbstract createWasteWaterData(@RequestBody GeneralWasteByPopulationDto wasteData) {
        return wasteService.createWasteWaterData(wasteData);
    }

    @Operation(summary = "Create Bio Treated Waste Water Data and calculate emissions")
    @PostMapping("/bioTreatedWasteWater")
    public WasteDataAbstract createBioTreatedWasteWaterData(@RequestBody GeneralWasteByPopulationDto wasteData) {
        return wasteService.createBioTreatedWasteWaterData(wasteData);
    }

    @Operation(summary = "Create Bio Treated Waste Water Data and calculate emissions")
    @PostMapping("/burntWaste")
    public WasteDataAbstract createBurntWasteData(@RequestBody GeneralWasteByPopulationDto wasteData) {
        return wasteService.createBurntWasteData(wasteData);
    }

    @Operation(summary = "Create Incineration Waste Data and calculate emissions")
    @PostMapping("/incinerationWaste")
    public WasteDataAbstract createWasteData(@RequestBody GeneralWasteByPopulationDto wasteData) {
        return wasteService.createIncinerationWasteData(wasteData);
    }

    @Operation(summary = "Get all recorded Waste Data and their emissions")
    @GetMapping("/allWasteData")
    public List<WasteDataAbstract> getAllWasteData() {
        return wasteService.getAllWasteData();
    }

    @Operation(summary = "Get Waste Data by type")
    @GetMapping("/wasteType/{wasteType}")
    public List<WasteDataAbstract> getWasteDataByType(@PathVariable("wasteType") WasteType wasteType) {
        return wasteService.getWasteDataByType(wasteType);
    }
}
