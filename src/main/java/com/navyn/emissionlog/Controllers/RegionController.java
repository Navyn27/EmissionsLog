package com.navyn.emissionlog.Controllers;

import com.navyn.emissionlog.Payload.Requests.CreateRegionDto;
import com.navyn.emissionlog.Payload.Responses.ApiResponse;
import com.navyn.emissionlog.Services.RegionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path="/regions")
@SecurityRequirement(name = "BearerAuth")
public class RegionController {

    @Autowired
    private RegionService regionService;

    @PostMapping
    public ResponseEntity<ApiResponse> createRegion(@RequestBody CreateRegionDto region) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Region created successfully",regionService.saveRegion(region)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getRegionById(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Region fetched successfully",regionService.getRegionById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllRegions() {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Regions fetched successfully",regionService.getAllRegions()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateRegion(@PathVariable UUID id, @RequestBody CreateRegionDto region) {
       return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Region updated successfully",regionService.updateRegion(id, region)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteRegion(@PathVariable UUID id) {
        regionService.deleteRegion(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Region deleted successfully"));
    }
}
