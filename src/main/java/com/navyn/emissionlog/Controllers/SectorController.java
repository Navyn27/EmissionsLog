package com.navyn.emissionlog.Controllers;

import com.navyn.emissionlog.Models.Sector;
import com.navyn.emissionlog.Payload.Requests.CreateSectorDto;
import com.navyn.emissionlog.Payload.Responses.ApiResponse;
import com.navyn.emissionlog.Services.SectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/sectors")
public class SectorController {

    @Autowired
    private SectorService sectorService;

    @PostMapping
    public ResponseEntity<ApiResponse> createSector(@RequestBody CreateSectorDto sectorDto) {
        ApiResponse response = new ApiResponse(true, "Sector created successfully", sectorService.saveSector(sectorDto));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getSectorById(@PathVariable UUID id) {
        Optional<Sector> sector = sectorService.getSectorById(id);
        if (sector.isPresent()) {
            ApiResponse response = new ApiResponse(true, "Sector fetched successfully", sector.get());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            ApiResponse response = new ApiResponse(false, "Sector not found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllSectors() {
        List<Sector> sectors = sectorService.getAllSectors();
        ApiResponse response = new ApiResponse(true, "Sectors fetched successfully", sectors);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateSector(@PathVariable UUID id, @RequestBody CreateSectorDto sectorDto) {
        try {
            Sector sector = sectorService.updateSector(id, sectorDto);
            ApiResponse response = new ApiResponse(true, "Sector updated successfully", sector);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RuntimeException e) {
            ApiResponse response = new ApiResponse(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteSector(@PathVariable UUID id) {
        sectorService.deleteSector(id);
        ApiResponse response = new ApiResponse(true, "Sector deleted successfully", null);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
