package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.controller;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveCalculationRequest;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveMitigationYear;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.service.StoveMitigationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("mitigation")
public class StoveMitigationController {

    private final StoveMitigationService mitigationService;

    public StoveMitigationController(StoveMitigationService mitigationService) {
        this.mitigationService = mitigationService;
    }

    @PostMapping("/calculate")
    public List<StoveMitigationYear> calculateMitigation(
            @RequestBody StoveCalculationRequest request
    ) {
        // Call the service method without gridEmissionFactors
        return mitigationService.calculateMitigation(
                request.getStoveType(),
                request.getUnitsInstalledPerYear(),
                request.getStartYear()
        );
    }
}
