package com.navyn.emissionlog.modules.mitigationProjects.Waste;

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
@RequestMapping("/mitigation/waste/dashboard")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class WasteDashboardController {

        private final WasteDashboardService wasteDashboardService;

        @Operation(summary = "Get Waste mitigation dashboard summary", description = "Totals for the seven waste mitigation projects and overall mitigation")
        @GetMapping("/summary")
        public ResponseEntity<ApiResponse> getWasteDashboardSummary(
                        @RequestParam(required = false, value = "startingYear") Integer startingYear,
                        @RequestParam(required = false, value = "endingYear") Integer endingYear) {
                return ResponseEntity.status(HttpStatus.OK).body(
                                new ApiResponse(true, "Waste mitigation dashboard summary fetched successfully",
                                                wasteDashboardService.getWasteDashboardSummary(startingYear,
                                                                endingYear)));
        }

        @Operation(summary = "Get Waste mitigation dashboard graph", description = "Yearly breakdown per waste mitigation project with totals")
        @GetMapping("/graph")
        public ResponseEntity<ApiResponse> getWasteDashboardGraph(
                        @RequestParam(required = false, value = "startingYear") Integer startingYear,
                        @RequestParam(required = false, value = "endingYear") Integer endingYear) {
                return ResponseEntity.status(HttpStatus.OK).body(
                                new ApiResponse(true, "Waste mitigation dashboard graph fetched successfully",
                                                wasteDashboardService.getWasteDashboardGraph(startingYear,
                                                                endingYear)));
        }

        @Operation(summary = "Export Waste mitigation dashboard report", description = "Excel report with summary, per-year graph data, and sheets for each waste mitigation project")
        @GetMapping("/export")
        public ResponseEntity<byte[]> exportWasteDashboard(
                        @RequestParam(required = false, value = "startingYear") Integer startingYear,
                        @RequestParam(required = false, value = "endingYear") Integer endingYear) {
                byte[] excelBytes = wasteDashboardService.exportWasteDashboard(startingYear, endingYear);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                headers.setContentDispositionFormData("attachment", "waste-dashboard.xlsx");
                headers.setContentLength(excelBytes.length);
                return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        }
}
