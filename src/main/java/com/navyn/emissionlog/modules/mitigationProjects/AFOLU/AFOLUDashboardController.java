package com.navyn.emissionlog.modules.mitigationProjects.AFOLU;

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
@RequestMapping("/mitigation/afolu/dashboard")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class AFOLUDashboardController {

    private final AFOLUDashboardService afoluDashboardService;

    @Operation(summary = "Get AFOLU mitigation dashboard summary", description = "Totals for the ten AFOLU mitigation projects and overall mitigation")
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse> getAFOLUDashboardSummary(
            @RequestParam(required = false, value = "startingYear") Integer startingYear,
            @RequestParam(required = false, value = "endingYear") Integer endingYear) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse(true, "AFOLU mitigation dashboard summary fetched successfully",
                        afoluDashboardService.getAFOLUDashboardSummary(startingYear, endingYear)));
    }

    @Operation(summary = "Get AFOLU mitigation dashboard graph", description = "Yearly breakdown per AFOLU mitigation project with totals")
    @GetMapping("/graph")
    public ResponseEntity<ApiResponse> getAFOLUDashboardGraph(
            @RequestParam(required = false, value = "startingYear") Integer startingYear,
            @RequestParam(required = false, value = "endingYear") Integer endingYear) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse(true, "AFOLU mitigation dashboard graph fetched successfully",
                        afoluDashboardService.getAFOLUDashboardGraph(startingYear, endingYear)));
    }

    @Operation(summary = "Export AFOLU mitigation dashboard report", description = "Excel report with summary, per-year graph data, and sheets for each AFOLU mitigation project")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportAFOLUDashboard(
            @RequestParam(required = false, value = "startingYear") Integer startingYear,
            @RequestParam(required = false, value = "endingYear") Integer endingYear) {
        byte[] excelBytes = afoluDashboardService.exportAFOLUDashboard(startingYear, endingYear);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "afolu-dashboard.xlsx");
        headers.setContentLength(excelBytes.length);
        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
}

