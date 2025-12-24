package com.navyn.emissionlog.modules.mitigationProjects.IPPU;

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
@RequestMapping("/mitigation/ippu/dashboard")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class IPPUDashboardController {

        private final IPPUDashboardService ippuDashboardService;

        @Operation(summary = "Get IPPU mitigation dashboard summary", description = "Totals for all F-gas types and overall mitigation")
        @GetMapping("/summary")
        public ResponseEntity<ApiResponse> getIPPUDashboardSummary(
                        @RequestParam(required = false, value = "startingYear") Integer startingYear,
                        @RequestParam(required = false, value = "endingYear") Integer endingYear) {
                return ResponseEntity.status(HttpStatus.OK).body(
                                new ApiResponse(true, "IPPU mitigation dashboard summary fetched successfully",
                                                ippuDashboardService.getIPPUDashboardSummary(startingYear,
                                                                endingYear)));
        }

        @Operation(summary = "Get IPPU mitigation dashboard graph", description = "Yearly breakdown per F-gas type with totals")
        @GetMapping("/graph")
        public ResponseEntity<ApiResponse> getIPPUDashboardGraph(
                        @RequestParam(required = false, value = "startingYear") Integer startingYear,
                        @RequestParam(required = false, value = "endingYear") Integer endingYear) {
                return ResponseEntity.status(HttpStatus.OK).body(
                                new ApiResponse(true, "IPPU mitigation dashboard graph fetched successfully",
                                                ippuDashboardService.getIPPUDashboardGraph(startingYear, endingYear)));
        }

        @Operation(summary = "Export IPPU mitigation dashboard report", description = "Excel report with summary, per-year graph data, F-gas breakdown, and detailed data sheet")
        @GetMapping("/export")
        public ResponseEntity<byte[]> exportIPPUDashboard(
                        @RequestParam(required = false, value = "startingYear") Integer startingYear,
                        @RequestParam(required = false, value = "endingYear") Integer endingYear) {
                byte[] excelBytes = ippuDashboardService.exportIPPUDashboard(startingYear, endingYear);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                headers.setContentDispositionFormData("attachment", "ippu-dashboard.xlsx");
                headers.setContentLength(excelBytes.length);
                return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        }
}
