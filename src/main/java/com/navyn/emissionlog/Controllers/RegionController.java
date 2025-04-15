package com.navyn.emissionlog.Controllers;

import com.navyn.emissionlog.Payload.Requests.CreateRegionDto;
import com.navyn.emissionlog.Payload.Responses.ApiResponse;
import com.navyn.emissionlog.Services.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController("RegionController")
@RequestMapping(path="/regions")
@SecurityRequirement(name = "BearerAuth")
public class RegionController {

    @Autowired
    private RegionService regionService;

    @Operation(summary = "Create a region", description = "Creates a region with the provided details.")
    @PostMapping
    public ResponseEntity<ApiResponse> createRegion(@RequestBody CreateRegionDto region) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Region created successfully",regionService.saveRegion(region)));
    }

    @Operation(summary = "Get a region by ID", description = "Fetches a region identified by the provided ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getRegionById(@PathVariable("id") UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Region fetched successfully",regionService.getRegionById(id)));
    }

    @Operation(summary = "Get all regions", description = "Fetches all regions available in the system.")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllRegions() {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Regions fetched successfully",regionService.getAllRegions()));
    }

    @Operation(summary = "Update a region", description = "Updates the region identified by the provided ID with the new details.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateRegion(@PathVariable("id") UUID id, @RequestBody CreateRegionDto region) {
       return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Region updated successfully",regionService.updateRegion(id, region)));
    }

    @Operation(summary = "Delete a region", description = "Deletes the region identified by the provided ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteRegion(@PathVariable("id") UUID id) {
        regionService.deleteRegion(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Region deleted successfully"));
    }
}
