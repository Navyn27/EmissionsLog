package com.navyn.emissionlog.modules.mitigationProjects;

import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping("/api/mitigation-dashboard")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class MitigationDashboardController {

    private final MitigationDashboardService mitigationDashboardService;

    @Operation(summary = "Get Mitigation Dashboard summary", 
               description = "Returns total BAU, total mitigation by sector (AFOLU, WASTE, ENERGY, IPPU, TRANSPORT), total mitigation, and mitigation scenario")
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse> getMitigationDashboardSummary(
            @RequestParam(required = false, value = "startingYear") Integer startingYear,
            @RequestParam(required = false, value = "endingYear") Integer endingYear) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse(true, "Mitigation dashboard summary fetched successfully",
                        mitigationDashboardService.getMitigationDashboardSummary(startingYear, endingYear)));
    }

    @Operation(summary = "Get Mitigation Dashboard graph", 
               description = "Returns yearly breakdown with BAU scenario, mitigation by sector, total mitigation, and mitigation scenario per year")
    @GetMapping("/graph")
    public ResponseEntity<ApiResponse> getMitigationDashboardGraph(
            @RequestParam(required = false, value = "startingYear") Integer startingYear,
            @RequestParam(required = false, value = "endingYear") Integer endingYear) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse(true, "Mitigation dashboard graph fetched successfully",
                        mitigationDashboardService.getMitigationDashboardGraph(startingYear, endingYear)));
    }

    @Operation(summary = "Export Mitigation Dashboard report", 
               description = "Excel report with data table and chart showing BAU Scenario vs Mitigation Scenario")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportMitigationDashboard(
            @RequestParam(required = false, value = "startingYear") Integer startingYear,
            @RequestParam(required = false, value = "endingYear") Integer endingYear) {
        byte[] excelBytes = mitigationDashboardService.exportMitigationDashboard(startingYear, endingYear);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "mitigation-dashboard.xlsx");
        headers.setContentLength(excelBytes.length);
        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
}

