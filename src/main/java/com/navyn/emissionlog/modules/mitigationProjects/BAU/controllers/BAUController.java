package com.navyn.emissionlog.modules.mitigationProjects.BAU.controllers;

import com.navyn.emissionlog.modules.mitigationProjects.BAU.enums.ESector;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.models.BAU;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.dtos.BAUDto;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.services.BAUService;
import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/mitigation/bau")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class BAUController {

    private final BAUService bauService;

    @Operation(summary = "Create a new BAU record", description = "Creates a new BAU (Business As Usual) record with the provided details. Year and sector combination must be unique.")
    @PostMapping
    public ResponseEntity<ApiResponse> createBAU(@Valid @RequestBody BAUDto dto) {
        BAU bau = bauService.createBAU(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "BAU record created successfully", bau));
    }

    @Operation(summary = "Get BAU by ID", description = "Fetches a BAU record identified by the provided ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getBAUById(@PathVariable UUID id) {
        return bauService.getBAUById(id)
                .map(bau -> ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse(true, "BAU record fetched successfully", bau)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, "BAU record not found with id: " + id, null)));
    }

    @Operation(summary = "Get all BAU records", description = "Fetches all BAU records available in the system, ordered by year descending.")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllBAUs(@RequestParam(required = false) Integer year,
            @RequestParam(required = false) ESector sector) {

        List<BAU> baus;

        if (year != null && sector != null) {
            // Get specific year and sector
            return bauService.getBAUByYearAndSector(year, sector)
                    .map(bau -> ResponseEntity.status(HttpStatus.OK)
                            .body(new ApiResponse(true, "BAU record fetched successfully", bau)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false,
                            String.format("BAU record not found for year %d and sector %s", year, sector), null)));
        } else if (year != null) {
            // Get all BAUs for a specific year
            baus = bauService.getBAUsByYear(year);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse(true, String.format("BAU records for year %d fetched successfully", year), baus));
        } else if (sector != null) {
            // Get all BAUs for a specific sector
            baus = bauService.getBAUsBySector(sector);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true,
                    String.format("BAU records for sector %s fetched successfully", sector), baus));
        } else {
            // Get all BAUs
            baus = bauService.getAllBAUs();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, "BAU records fetched successfully", baus));
        }
    }

    @Operation(summary = "Update a BAU record", description = "Updates the BAU record identified by the provided ID with the new details. Year and sector combination must be unique.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateBAU(@PathVariable UUID id, @Valid @RequestBody BAUDto dto) {
        BAU bau = bauService.updateBAU(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "BAU record updated successfully", bau));
    }

    @Operation(summary = "Delete a BAU record", description = "Deletes the BAU record identified by the provided ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteBAU(@PathVariable UUID id) {
        bauService.deleteBAU(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, "BAU record deleted successfully", null));
    }
}
