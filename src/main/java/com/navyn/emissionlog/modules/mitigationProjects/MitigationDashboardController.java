package com.navyn.emissionlog.modules.mitigationProjects;

import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mitigation")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class MitigationDashboardController {
    
    private final MitigationDashboardService mitigationDashboardService;
    
    @Operation(summary = "Get Mitigation dashboard summary", description = "Retrieves mitigation summary from all 17 projects (10 AFOLU + 7 Waste) plus Transport Scenarios.")
    @GetMapping("/dashboard/summary")
    public ResponseEntity<ApiResponse> getMitigationDashboardSummary(
            @RequestParam(required = false, value = "startingYear") Integer startingYear,
            @RequestParam(required = false, value = "endingYear") Integer endingYear) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse(true, "Mitigation dashboard summary fetched successfully", 
                        mitigationDashboardService.getMitigationDashboardSummary(startingYear, endingYear)));
    }
    
    @Operation(summary = "Get Mitigation dashboard graph", description = "Retrieves mitigation projects graph data by year.")
    @GetMapping("/dashboard/graph")
    public ResponseEntity<ApiResponse> getMitigationDashboardGraph(
            @RequestParam(required = false, value = "startingYear") Integer startingYear,
            @RequestParam(required = false, value = "endingYear") Integer endingYear) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse(true, "Mitigation dashboard graph fetched successfully", 
                        mitigationDashboardService.getMitigationDashboardGraph(startingYear, endingYear)));
    }
}
