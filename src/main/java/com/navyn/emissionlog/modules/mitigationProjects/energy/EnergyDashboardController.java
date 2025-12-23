package com.navyn.emissionlog.modules.mitigationProjects.energy;

import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/mitigation/energy/dashboard")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class EnergyDashboardController {

    private final EnergyDashboardService energyDashboardService;

    @Operation(summary = "Get Energy mitigation dashboard summary", description = "Totals for the four energy mitigation projects and overall mitigation")
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse> getEnergyDashboardSummary(
            @RequestParam(required = false, value = "startingYear") Integer startingYear,
            @RequestParam(required = false, value = "endingYear") Integer endingYear) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse(true, "Energy mitigation dashboard summary fetched successfully",
                        energyDashboardService.getEnergyDashboardSummary(startingYear, endingYear)));
    }

    @Operation(summary = "Get Energy mitigation dashboard graph", description = "Yearly breakdown per energy mitigation project with totals")
    @GetMapping("/graph")
    public ResponseEntity<ApiResponse> getEnergyDashboardGraph(
            @RequestParam(required = false, value = "startingYear") Integer startingYear,
            @RequestParam(required = false, value = "endingYear") Integer endingYear) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse(true, "Energy mitigation dashboard graph fetched successfully",
                        energyDashboardService.getEnergyDashboardGraph(startingYear, endingYear)));
    }

    @Operation(summary = "Export Energy mitigation dashboard report", description = "Excel report with summary, per-year graph data, and sheets for each energy mitigation project")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportEnergyDashboard(
            @RequestParam(required = false, value = "startingYear") Integer startingYear,
            @RequestParam(required = false, value = "endingYear") Integer endingYear) {
        byte[] excelBytes = energyDashboardService.exportEnergyDashboard(startingYear, endingYear);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "energy-dashboard.xlsx");
        headers.setContentLength(excelBytes.length);
        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
}

