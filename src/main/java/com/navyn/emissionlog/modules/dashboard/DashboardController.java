package com.navyn.emissionlog.modules.dashboard;

import com.navyn.emissionlog.utils.ApiResponse;
import com.navyn.emissionlog.utils.DashboardData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Dashboard", description = "Comprehensive dashboard APIs for all emission modules")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // ==================== MAIN DASHBOARD ====================

    @GetMapping("/main")
    @Operation(summary = "Get main dashboard", description = "Returns Total N2O, CO2, CH4, CO2eq emissions, Total Mitigation, and Net Emissions (Gross - Mitigation)")
    public ResponseEntity<ApiResponse> getMainDashboard(
            @RequestParam(required = false) Integer startingYear,
            @RequestParam(required = false) Integer endingYear) {
        DashboardData data = dashboardService.getMainDashboard(startingYear, endingYear);
        return ResponseEntity.ok(new ApiResponse(true, "Main dashboard fetched successfully", data));
    }

    @GetMapping("/main/graph")
    @Operation(summary = "Get main dashboard graph data by year", description = "Returns yearly breakdown with all emissions, mitigation, and net emissions")
    public ResponseEntity<ApiResponse> getMainDashboardGraph(
            @RequestParam(required = false) Integer startingYear,
            @RequestParam(required = false) Integer endingYear) {
        List<DashboardData> data = dashboardService.getMainDashboardGraph(startingYear, endingYear);
        return ResponseEntity.ok(new ApiResponse(true, "Main dashboard graph fetched successfully", data));
    }

    // ==================== STATIONARY DASHBOARD ====================

    @GetMapping("/stationary")
    @Operation(summary = "Get stationary emissions dashboard", description = "Returns Total N2O, CO2, CH4, and CO2eq emissions for stationary sources only")
    public ResponseEntity<ApiResponse> getStationaryDashboard(
            @RequestParam(required = false) Integer startingYear,
            @RequestParam(required = false) Integer endingYear) {
        DashboardData data = dashboardService.getStationaryDashboard(startingYear, endingYear);
        return ResponseEntity.ok(new ApiResponse(true, "Stationary dashboard fetched successfully", data));
    }

    // ==================== WASTE DASHBOARD ====================

    @GetMapping("/waste")
    @Operation(summary = "Get waste emissions dashboard", description = "Returns Total N2O, CO2, CH4, and CO2eq emissions for waste sector only")
    public ResponseEntity<ApiResponse> getWasteDashboard(
            @RequestParam(required = false) Integer startingYear,
            @RequestParam(required = false) Integer endingYear) {
        DashboardData data = dashboardService.getWasteDashboard(startingYear, endingYear);
        return ResponseEntity.ok(new ApiResponse(true, "Waste dashboard fetched successfully", data));
    }

    // ==================== AGRICULTURE DASHBOARD ====================

    @GetMapping("/agriculture")
    @Operation(summary = "Get agriculture emissions dashboard", description = "Returns Total N2O, CO2, CH4, and CO2eq emissions for agriculture sector only")
    public ResponseEntity<ApiResponse> getAgricultureDashboard(
            @RequestParam(required = false) Integer startingYear,
            @RequestParam(required = false) Integer endingYear) {
        DashboardData data = dashboardService.getAgricultureDashboard(startingYear, endingYear);
        return ResponseEntity.ok(new ApiResponse(true, "Agriculture dashboard fetched successfully", data));
    }

    // ==================== LAND USE DASHBOARD ====================

    @GetMapping("/landuse")
    @Operation(summary = "Get land use emissions dashboard", description = "Returns Total N2O, CO2, CH4, and CO2eq emissions for land use sector only")
    public ResponseEntity<ApiResponse> getLandUseDashboard(
            @RequestParam(required = false) Integer startingYear,
            @RequestParam(required = false) Integer endingYear) {
        DashboardData data = dashboardService.getLandUseDashboard(startingYear, endingYear);
        return ResponseEntity.ok(new ApiResponse(true, "Land use dashboard fetched successfully", data));
    }

    // ==================== EXPORT ====================

    @GetMapping("/main/export")
    @Operation(summary = "Export main dashboard report", description = "Excel report with emissions data, mitigation totals, and net emissions breakdown")
    public ResponseEntity<byte[]> exportMainDashboard(
            @RequestParam(required = false) Integer startingYear,
            @RequestParam(required = false) Integer endingYear) {
        byte[] excelBytes = dashboardService.exportMainDashboard(startingYear, endingYear);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "main-dashboard.xlsx");
        headers.setContentLength(excelBytes.length);
        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
}
