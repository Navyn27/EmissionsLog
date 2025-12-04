package com.navyn.emissionlog.modules.mitigationProjects.Energy.LightBulb.controller;

import com.navyn.emissionlog.modules.mitigationProjects.Energy.LightBulb.dto.CreateLightBulbDTO;
import com.navyn.emissionlog.modules.mitigationProjects.Energy.LightBulb.dto.UpdateLightBulbDTO;
import com.navyn.emissionlog.modules.mitigationProjects.Energy.LightBulb.model.LightBulb;
import com.navyn.emissionlog.modules.mitigationProjects.Energy.LightBulb.service.ILightBulbService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/mitigation/lightBulb")
@SecurityRequirement(name = "BearerAuth")
@AllArgsConstructor
public class LightBulbController {
    private final ILightBulbService lightBulbService;

    @PostMapping
    @Operation(summary = "Create a new LightBulb entry")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "LightBulb entry created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<LightBulb> create(@Valid @RequestBody CreateLightBulbDTO lightBulbDTO) {
        LightBulb createdLightBulb = lightBulbService.create(lightBulbDTO);
        return new ResponseEntity<>(createdLightBulb, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all LightBulb entries")
    public ResponseEntity<List<LightBulb>> getAll() {
        List<LightBulb> lightBulbs = lightBulbService.getAll();
        return new ResponseEntity<>(lightBulbs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a LightBulb entry by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the LightBulb entry"),
            @ApiResponse(responseCode = "404", description = "LightBulb entry not found")
    })
    public ResponseEntity<LightBulb> getById(@PathVariable UUID id) {
        LightBulb lightBulb = lightBulbService.getById(id);
        return new ResponseEntity<>(lightBulb, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a LightBulb entry")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "LightBulb entry updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "LightBulb entry not found")
    })
    public ResponseEntity<LightBulb> update(@PathVariable UUID id, @Valid @RequestBody UpdateLightBulbDTO lightBulbDTO) {
        LightBulb updatedLightBulb = lightBulbService.update(id, lightBulbDTO);
        return new ResponseEntity<>(updatedLightBulb, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a LightBulb entry")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "LightBulb entry deleted successfully"),
            @ApiResponse(responseCode = "404", description = "LightBulb entry not found")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        lightBulbService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
