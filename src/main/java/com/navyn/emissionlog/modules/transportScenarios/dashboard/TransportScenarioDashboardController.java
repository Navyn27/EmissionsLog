package com.navyn.emissionlog.modules.transportScenarios.dashboard;

import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transport-scenario-dashboard")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Transport Scenario Dashboard", description = "Dashboard APIs for Transport Scenario mitigations (Modal Shift and Electric Vehicle)")
@RequiredArgsConstructor
public class TransportScenarioDashboardController {

    private final TransportScenarioDashboardService transportScenarioDashboardService;

    @Operation(summary = "Get Transport Scenario Dashboard summary",
               description = "Returns total BAU, Modal Shift mitigation, Electric Vehicle mitigation, total mitigation, and net emissions")
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse> getTransportScenarioDashboardSummary(
            @RequestParam(required = false, value = "startingYear") Integer startingYear,
            @RequestParam(required = false, value = "endingYear") Integer endingYear) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse(true, "Transport scenario dashboard summary fetched successfully",
                        transportScenarioDashboardService.getTransportScenarioDashboardSummary(startingYear, endingYear)));
    }

    @Operation(summary = "Get Transport Scenario Dashboard graph",
               description = "Returns yearly breakdown with BAU scenario, Modal Shift mitigation, Electric Vehicle mitigation, total mitigation, and net emissions per year")
    @GetMapping("/graph")
    public ResponseEntity<ApiResponse> getTransportScenarioDashboardGraph(
            @RequestParam(required = false, value = "startingYear") Integer startingYear,
            @RequestParam(required = false, value = "endingYear") Integer endingYear) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse(true, "Transport scenario dashboard graph fetched successfully",
                        transportScenarioDashboardService.getTransportScenarioDashboardGraph(startingYear, endingYear)));
    }

    @Operation(summary = "Export Transport Scenario Dashboard report",
               description = "Excel report with data table and chart showing BAU Scenario vs Net Emissions")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportTransportScenarioDashboard(
            @RequestParam(required = false, value = "startingYear") Integer startingYear,
            @RequestParam(required = false, value = "endingYear") Integer endingYear) {
        byte[] excelBytes = transportScenarioDashboardService.exportTransportScenarioDashboard(startingYear, endingYear);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "transport-scenario-dashboard.xlsx");
        headers.setContentLength(excelBytes.length);
        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
}

