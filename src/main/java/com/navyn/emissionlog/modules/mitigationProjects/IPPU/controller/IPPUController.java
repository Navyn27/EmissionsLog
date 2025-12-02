package com.navyn.emissionlog.modules.mitigationProjects.IPPU.controller;

import com.navyn.emissionlog.modules.mitigationProjects.IPPU.dto.IPPUMitigationDTO;
import com.navyn.emissionlog.modules.mitigationProjects.IPPU.dto.IPPUMitigationResponseDTO;
import com.navyn.emissionlog.modules.mitigationProjects.IPPU.model.IPPUMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.IPPU.service.IIPPUService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/ippu-mitigations")
@AllArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "IPPU Mitigation Projects", description = "APIs for managing IPPU (Industrial Processes and Product Use) mitigation projects related to F-gases.")
public class IPPUController {
    private final IIPPUService iippuService;

    @Operation(summary = "Create a new IPPU mitigation entry",
            description = "Creates a new IPPU mitigation record, calculating emissions reductions based on the provided data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the entry",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IPPUMitigation.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data provided")
    })
    @PostMapping
    public ResponseEntity<IPPUMitigation> create(@Valid @RequestBody IPPUMitigationDTO ippuMitigationDTO) {
        IPPUMitigation savedIPPUMitigation = iippuService.save(ippuMitigationDTO);
        return new ResponseEntity<>(savedIPPUMitigation, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all IPPU mitigation entries with totals",
            description = "Retrieves a list of all IPPU mitigation records, along with the sum of all 'mitigationScenario' and 'reducedEmissionInKtCO2e' values.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list and totals",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IPPUMitigationResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<IPPUMitigationResponseDTO> getAll() {
        IPPUMitigationResponseDTO response = iippuService.findAll();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get an IPPU mitigation entry by ID",
            description = "Retrieves a specific IPPU mitigation record by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found the entry",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IPPUMitigation.class))),
            @ApiResponse(responseCode = "404", description = "Entry not found with the given ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<IPPUMitigation> getById(
            @Parameter(description = "Unique ID of the IPPU mitigation entry") @PathVariable UUID id) {
        return iippuService.findById(id)
                .map(ippuMitigation -> new ResponseEntity<>(ippuMitigation, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Update an IPPU mitigation entry",
            description = "Updates an existing IPPU mitigation record and recalculates the emissions values.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the entry",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IPPUMitigation.class))),
            @ApiResponse(responseCode = "404", description = "Entry not found with the given ID"),
            @ApiResponse(responseCode = "400", description = "Invalid input data provided")
    })
    @PutMapping("/{id}")
    public ResponseEntity<IPPUMitigation> update(
            @Parameter(description = "Unique ID of the IPPU mitigation entry to update") @PathVariable UUID id,
            @Valid @RequestBody IPPUMitigationDTO ippuMitigationDTO) {
        IPPUMitigation updatedIPPUMitigation = iippuService.update(id, ippuMitigationDTO);
        return new ResponseEntity<>(updatedIPPUMitigation, HttpStatus.OK);
    }

    @Operation(summary = "Delete an IPPU mitigation entry",
            description = "Deletes an IPPU mitigation record by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted the entry"),
            @ApiResponse(responseCode = "404", description = "Entry not found with the given ID")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Unique ID of the IPPU mitigation entry to delete") @PathVariable UUID id) {
        iippuService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
