package com.navyn.emissionlog.modules.dashboard;

import com.navyn.emissionlog.modules.dashboard.dto.DashboardResponseDto;
import com.navyn.emissionlog.modules.dashboard.dto.DashboardSummaryDto;
import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Unified Dashboard Controller
 * Provides consistent emission data for dashboard cards and charts
 */
@RestController
@RequestMapping("/api/v2/dashboard")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Dashboard V2", description = "Unified dashboard APIs with consistent data")
@RequiredArgsConstructor
public class UnifiedDashboardController {

    private final UnifiedDashboardService dashboardService;

    /**
     * Get complete dashboard data (summary + time-series)
     * Use this single endpoint for both stat cards and chart
     */
    @GetMapping
    @Operation(summary = "Get complete dashboard",
            description = "Returns summary totals and time-series data for the selected period and mode")
    public ResponseEntity<ApiResponse> getDashboard(
            @Parameter(description = "Start year (defaults to current year)")
            @RequestParam(required = false) Integer startYear,
            @Parameter(description = "End year (defaults to current year)")
            @RequestParam(required = false) Integer endYear,
            @Parameter(description = "Display mode: MONTH (monthly breakdown) or YEAR (yearly breakdown)")
            @RequestParam(defaultValue = "MONTH") String mode) {

        DashboardResponseDto data = dashboardService.getDashboard(startYear, endYear, mode);
        return ResponseEntity.ok(new ApiResponse(true, "Dashboard fetched successfully", data));
    }

    /**
     * Get summary totals only (for stat cards)
     */
    @GetMapping("/summary")
    @Operation(summary = "Get dashboard summary",
            description = "Returns aggregated emission totals for the selected year range")
    public ResponseEntity<ApiResponse> getSummary(
            @Parameter(description = "Start year")
            @RequestParam(required = false) Integer startYear,
            @Parameter(description = "End year")
            @RequestParam(required = false) Integer endYear) {

        DashboardSummaryDto data = dashboardService.getSummary(startYear, endYear);
        return ResponseEntity.ok(new ApiResponse(true, "Summary fetched successfully", data));
    }

    /**
     * Get monthly breakdown for a single year (for monthly chart)
     */
    @GetMapping("/monthly")
    @Operation(summary = "Get monthly data",
            description = "Returns monthly emission breakdown for a specific year")
    public ResponseEntity<ApiResponse> getMonthlyData(
            @Parameter(description = "Year to get monthly data for")
            @RequestParam(required = false) Integer year) {

        List<DashboardSummaryDto> data = dashboardService.getMonthlyData(year);
        return ResponseEntity.ok(new ApiResponse(true, "Monthly data fetched successfully", data));
    }

    /**
     * Get yearly breakdown for a year range (for yearly chart)
     */
    @GetMapping("/yearly")
    @Operation(summary = "Get yearly data",
            description = "Returns yearly emission breakdown for a year range")
    public ResponseEntity<ApiResponse> getYearlyData(
            @Parameter(description = "Start year")
            @RequestParam(required = false) Integer startYear,
            @Parameter(description = "End year")
            @RequestParam(required = false) Integer endYear) {

        List<DashboardSummaryDto> data = dashboardService.getYearlyData(startYear, endYear);
        return ResponseEntity.ok(new ApiResponse(true, "Yearly data fetched successfully", data));
    }
}
