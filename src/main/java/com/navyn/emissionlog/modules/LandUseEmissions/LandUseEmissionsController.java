package com.navyn.emissionlog.modules.LandUseEmissions;

import com.navyn.emissionlog.Enums.LandUse.LandCategory;
import com.navyn.emissionlog.modules.LandUseEmissions.Dtos.*;
import com.navyn.emissionlog.modules.LandUseEmissions.models.*;
import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.UUID;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/landUseEmissions")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class LandUseEmissionsController {

        private final LandUseEmissionsService landUseEmissionsService;

        // BiomassGain endpoints
        @PostMapping("/biomassGain")
        public ResponseEntity<BiomassGain> createBiomassGain(@Valid @RequestBody BiomassGainDto biomassGainDto) {
                BiomassGain biomassGain = landUseEmissionsService.createBiomassGain(biomassGainDto);
                return ResponseEntity.ok(biomassGain);
        }

        @GetMapping("/biomassGain")
        public ResponseEntity<List<BiomassGain>> getAllBiomassGain(
                        @RequestParam(required = false) Integer year,
                        @RequestParam(required = false) LandCategory landCategory) {
                List<BiomassGain> biomassGains = landUseEmissionsService.getAllBiomassGain(year, landCategory);
                return ResponseEntity.ok(biomassGains);
        }

        @PutMapping("/biomassGain/{id}")
        @Operation(summary = "Update biomass gain record")
        public ResponseEntity<BiomassGain> updateBiomassGain(
                        @PathVariable UUID id,
                        @Valid @RequestBody BiomassGainDto biomassGainDto) {
                BiomassGain biomassGain = landUseEmissionsService.updateBiomassGain(id, biomassGainDto);
                return ResponseEntity.ok(biomassGain);
        }

        @DeleteMapping("/biomassGain/{id}")
        @Operation(summary = "Delete biomass gain record")
        public ResponseEntity<ApiResponse> deleteBiomassGain(@PathVariable UUID id) {
                landUseEmissionsService.deleteBiomassGain(id);
                return ResponseEntity.ok(new ApiResponse(true, "Biomass Gain deleted successfully", null));
        }

        @GetMapping("/biomassGain/template")
        @Operation(summary = "Download Biomass Gain Excel template")
        public ResponseEntity<byte[]> downloadBiomassGainTemplate() {
                byte[] bytes = landUseEmissionsService.generateBiomassGainExcelTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", "biomass_gain_template.xlsx");
                return ResponseEntity.ok().headers(headers).body(bytes);
        }

        @PostMapping("/biomassGain/excel")
        @Operation(summary = "Upload Biomass Gain records from Excel")
        public ResponseEntity<ApiResponse> createBiomassGainFromExcel(@RequestParam("file") MultipartFile file) {
                Map<String, Object> result = landUseEmissionsService.createBiomassGainFromExcel(file);
                return ResponseEntity.ok(new ApiResponse(true, uploadMessage(result), result));
        }

        // DisturbanceBiomassLoss endpoints
        @PostMapping("/disturbanceBiomassLoss")
        public ResponseEntity<DisturbanceBiomassLoss> createDisturbanceBiomassLoss(
                        @Valid @RequestBody DisturbanceBiomassLossDto disturbanceBiomassLossDto) {
                DisturbanceBiomassLoss disturbanceBiomassLoss = landUseEmissionsService
                                .createDisturbanceBiomassLoss(disturbanceBiomassLossDto);
                return ResponseEntity.ok(disturbanceBiomassLoss);
        }

        @GetMapping("/disturbanceBiomassLoss")
        public ResponseEntity<List<DisturbanceBiomassLoss>> getAllDisturbanceBiomassLoss(
                        @RequestParam(required = false) Integer year,
                        @RequestParam(required = false) LandCategory landCategory) {
                List<DisturbanceBiomassLoss> disturbanceBiomassLosses = landUseEmissionsService
                                .getAllDisturbanceBiomassLoss(year, landCategory);
                return ResponseEntity.ok(disturbanceBiomassLosses);
        }

        @PutMapping("/disturbanceBiomassLoss/{id}")
        @Operation(summary = "Update disturbance biomass loss record")
        public ResponseEntity<DisturbanceBiomassLoss> updateDisturbanceBiomassLoss(
                        @PathVariable UUID id,
                        @Valid @RequestBody DisturbanceBiomassLossDto disturbanceBiomassLossDto) {
                DisturbanceBiomassLoss disturbanceBiomassLoss = landUseEmissionsService
                                .updateDisturbanceBiomassLoss(id, disturbanceBiomassLossDto);
                return ResponseEntity.ok(disturbanceBiomassLoss);
        }

        @DeleteMapping("/disturbanceBiomassLoss/{id}")
        @Operation(summary = "Delete disturbance biomass loss record")
        public ResponseEntity<ApiResponse> deleteDisturbanceBiomassLoss(@PathVariable UUID id) {
                landUseEmissionsService.deleteDisturbanceBiomassLoss(id);
                return ResponseEntity.ok(new ApiResponse(true, "Disturbance Biomass Loss deleted successfully", null));
        }

        @GetMapping("/disturbanceBiomassLoss/template")
        @Operation(summary = "Download Disturbance Biomass Loss Excel template")
        public ResponseEntity<byte[]> downloadDisturbanceBiomassLossTemplate() {
                byte[] bytes = landUseEmissionsService.generateDisturbanceBiomassLossExcelTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", "disturbance_biomass_loss_template.xlsx");
                return ResponseEntity.ok().headers(headers).body(bytes);
        }

        @PostMapping("/disturbanceBiomassLoss/excel")
        @Operation(summary = "Upload Disturbance Biomass Loss records from Excel")
        public ResponseEntity<ApiResponse> createDisturbanceBiomassLossFromExcel(@RequestParam("file") MultipartFile file) {
                Map<String, Object> result = landUseEmissionsService.createDisturbanceBiomassLossFromExcel(file);
                return ResponseEntity.ok(new ApiResponse(true, uploadMessage(result), result));
        }

        // FirewoodRemovalBiomassLoss endpoints
        @PostMapping("/firewoodRemovalBiomassLoss")
        public ResponseEntity<FirewoodRemovalBiomassLoss> createFirewoodRemovalBiomassLoss(
                        @Valid @RequestBody FirewoodRemovalBiomassLossDto firewoodRemovalBiomassLossDto) {
                FirewoodRemovalBiomassLoss firewoodRemovalBiomassLoss = landUseEmissionsService
                                .createFirewoodRemovalBiomassLoss(firewoodRemovalBiomassLossDto);
                return ResponseEntity.ok(firewoodRemovalBiomassLoss);
        }

        @GetMapping("/firewoodRemovalBiomassLoss")
        public ResponseEntity<List<FirewoodRemovalBiomassLoss>> getAllFirewoodRemovalBiomassLoss(
                        @RequestParam(required = false) Integer year,
                        @RequestParam(required = false) LandCategory landCategory) {
                List<FirewoodRemovalBiomassLoss> firewoodRemovalBiomassLosses = landUseEmissionsService
                                .getAllFirewoodRemovalBiomassLoss(year, landCategory);
                return ResponseEntity.ok(firewoodRemovalBiomassLosses);
        }

        @PutMapping("/firewoodRemovalBiomassLoss/{id}")
        @Operation(summary = "Update firewood removal biomass loss record")
        public ResponseEntity<FirewoodRemovalBiomassLoss> updateFirewoodRemovalBiomassLoss(
                        @PathVariable UUID id,
                        @Valid @RequestBody FirewoodRemovalBiomassLossDto firewoodRemovalBiomassLossDto) {
                FirewoodRemovalBiomassLoss firewoodRemovalBiomassLoss = landUseEmissionsService
                                .updateFirewoodRemovalBiomassLoss(id, firewoodRemovalBiomassLossDto);
                return ResponseEntity.ok(firewoodRemovalBiomassLoss);
        }

        @DeleteMapping("/firewoodRemovalBiomassLoss/{id}")
        @Operation(summary = "Delete firewood removal biomass loss record")
        public ResponseEntity<ApiResponse> deleteFirewoodRemovalBiomassLoss(@PathVariable UUID id) {
                landUseEmissionsService.deleteFirewoodRemovalBiomassLoss(id);
                return ResponseEntity
                                .ok(new ApiResponse(true, "Firewood Removal Biomass Loss deleted successfully", null));
        }

        @GetMapping("/firewoodRemovalBiomassLoss/template")
        @Operation(summary = "Download Firewood Removal Biomass Loss Excel template")
        public ResponseEntity<byte[]> downloadFirewoodRemovalBiomassLossTemplate() {
                byte[] bytes = landUseEmissionsService.generateFirewoodRemovalBiomassLossExcelTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", "firewood_removal_biomass_loss_template.xlsx");
                return ResponseEntity.ok().headers(headers).body(bytes);
        }

        @PostMapping("/firewoodRemovalBiomassLoss/excel")
        @Operation(summary = "Upload Firewood Removal Biomass Loss records from Excel")
        public ResponseEntity<ApiResponse> createFirewoodRemovalBiomassLossFromExcel(@RequestParam("file") MultipartFile file) {
                Map<String, Object> result = landUseEmissionsService.createFirewoodRemovalBiomassLossFromExcel(file);
                return ResponseEntity.ok(new ApiResponse(true, uploadMessage(result), result));
        }

        // HarvestedBiomassLoss endpoints
        @PostMapping("/harvestedBiomassLoss")
        public ResponseEntity<HarvestedBiomassLoss> createHarvestedBiomassLoss(
                        @Valid @RequestBody HarvestedBiomassLossDto harvestedBiomassLossDto) {
                HarvestedBiomassLoss harvestedBiomassLoss = landUseEmissionsService
                                .createHarvestedBiomassLoss(harvestedBiomassLossDto);
                return ResponseEntity.ok(harvestedBiomassLoss);
        }

        @GetMapping("/harvestedBiomassLoss")
        public ResponseEntity<List<HarvestedBiomassLoss>> getAllHarvestedBiomassLoss(
                        @RequestParam(required = false) Integer year,
                        @RequestParam(required = false) LandCategory landCategory) {
                List<HarvestedBiomassLoss> harvestedBiomassLosses = landUseEmissionsService.getAllHarvestedBiomassLoss(
                                year,
                                landCategory);
                return ResponseEntity.ok(harvestedBiomassLosses);
        }

        @PutMapping("/harvestedBiomassLoss/{id}")
        @Operation(summary = "Update harvested biomass loss record")
        public ResponseEntity<HarvestedBiomassLoss> updateHarvestedBiomassLoss(
                        @PathVariable UUID id,
                        @Valid @RequestBody HarvestedBiomassLossDto harvestedBiomassLossDto) {
                HarvestedBiomassLoss harvestedBiomassLoss = landUseEmissionsService.updateHarvestedBiomassLoss(id,
                                harvestedBiomassLossDto);
                return ResponseEntity.ok(harvestedBiomassLoss);
        }

        @DeleteMapping("/harvestedBiomassLoss/{id}")
        @Operation(summary = "Delete harvested biomass loss record")
        public ResponseEntity<ApiResponse> deleteHarvestedBiomassLoss(@PathVariable UUID id) {
                landUseEmissionsService.deleteHarvestedBiomassLoss(id);
                return ResponseEntity.ok(new ApiResponse(true, "Harvested Biomass Loss deleted successfully", null));
        }

        @GetMapping("/harvestedBiomassLoss/template")
        @Operation(summary = "Download Harvested Biomass Loss Excel template")
        public ResponseEntity<byte[]> downloadHarvestedBiomassLossTemplate() {
                byte[] bytes = landUseEmissionsService.generateHarvestedBiomassLossExcelTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", "harvested_biomass_loss_template.xlsx");
                return ResponseEntity.ok().headers(headers).body(bytes);
        }

        @PostMapping("/harvestedBiomassLoss/excel")
        @Operation(summary = "Upload Harvested Biomass Loss records from Excel")
        public ResponseEntity<ApiResponse> createHarvestedBiomassLossFromExcel(@RequestParam("file") MultipartFile file) {
                Map<String, Object> result = landUseEmissionsService.createHarvestedBiomassLossFromExcel(file);
                return ResponseEntity.ok(new ApiResponse(true, uploadMessage(result), result));
        }

        // RewettedMineralWetlands endpoints (no LandCategory filter)
        @PostMapping("/rewettedMineralWetlands")
        public ResponseEntity<RewettedMineralWetlands> createRewettedMineralWetlands(
                        @Valid @RequestBody RewettedMineralWetlandsDto rewettedMineralWetlandsDto) {
                RewettedMineralWetlands rewettedMineralWetlands = landUseEmissionsService
                                .createRewettedMineralWetlands(rewettedMineralWetlandsDto);
                return ResponseEntity.ok(rewettedMineralWetlands);
        }

        @GetMapping("/rewettedMineralWetlands")
        public ResponseEntity<List<RewettedMineralWetlands>> getAllRewettedMineralWetlands(
                        @RequestParam(required = false) Integer year) {
                List<RewettedMineralWetlands> rewettedMineralWetlands = landUseEmissionsService
                                .getAllRewettedMineralWetlands(year);
                return ResponseEntity.ok(rewettedMineralWetlands);
        }

        @PutMapping("/rewettedMineralWetlands/{id}")
        @Operation(summary = "Update rewetted mineral wetlands record")
        public ResponseEntity<RewettedMineralWetlands> updateRewettedMineralWetlands(
                        @PathVariable UUID id,
                        @Valid @RequestBody RewettedMineralWetlandsDto rewettedMineralWetlandsDto) {
                RewettedMineralWetlands rewettedMineralWetlands = landUseEmissionsService.updateRewettedMineralWetlands(
                                id,
                                rewettedMineralWetlandsDto);
                return ResponseEntity.ok(rewettedMineralWetlands);
        }

        @DeleteMapping("/rewettedMineralWetlands/{id}")
        @Operation(summary = "Delete rewetted mineral wetlands record")
        public ResponseEntity<ApiResponse> deleteRewettedMineralWetlands(@PathVariable UUID id) {
                landUseEmissionsService.deleteRewettedMineralWetlands(id);
                return ResponseEntity.ok(new ApiResponse(true, "Rewetted Mineral Wetlands deleted successfully", null));
        }

        @GetMapping("/rewettedMineralWetlands/template")
        @Operation(summary = "Download Rewetted Mineral Wetlands Excel template")
        public ResponseEntity<byte[]> downloadRewettedMineralWetlandsTemplate() {
                byte[] bytes = landUseEmissionsService.generateRewettedMineralWetlandsExcelTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", "rewetted_mineral_wetlands_template.xlsx");
                return ResponseEntity.ok().headers(headers).body(bytes);
        }

        @PostMapping("/rewettedMineralWetlands/excel")
        @Operation(summary = "Upload Rewetted Mineral Wetlands records from Excel")
        public ResponseEntity<ApiResponse> createRewettedMineralWetlandsFromExcel(@RequestParam("file") MultipartFile file) {
                Map<String, Object> result = landUseEmissionsService.createRewettedMineralWetlandsFromExcel(file);
                return ResponseEntity.ok(new ApiResponse(true, uploadMessage(result), result));
        }

        private static String uploadMessage(Map<String, Object> result) {
                int savedCount = (Integer) result.get("savedCount");
                int skippedCount = (Integer) result.get("skippedCount");
                return String.format("Upload completed. %d record(s) saved successfully. %d record(s) skipped.", savedCount, skippedCount);
        }

        // ============= MINI DASHBOARDS =============

        @Operation(summary = "Get Land Use dashboard summary", description = "Retrieves land use emissions summary from all 5 modules.")
        @GetMapping("/dashboard/summary")
        public ResponseEntity<ApiResponse> getLandUseDashboardSummary(
                        @RequestParam(required = false, value = "startingYear") Integer startingYear,
                        @RequestParam(required = false, value = "endingYear") Integer endingYear) {
                return ResponseEntity.status(HttpStatus.OK).body(
                                new ApiResponse(true, "Land Use dashboard summary fetched successfully",
                                                landUseEmissionsService.getLandUseDashboardSummary(startingYear,
                                                                endingYear)));
        }

        @Operation(summary = "Get Land Use dashboard graph", description = "Retrieves land use emissions graph data by year.")
        @GetMapping("/dashboard/graph")
        public ResponseEntity<ApiResponse> getLandUseDashboardGraph(
                        @RequestParam(required = false, value = "startingYear") Integer startingYear,
                        @RequestParam(required = false, value = "endingYear") Integer endingYear) {
                return ResponseEntity.status(HttpStatus.OK).body(
                                new ApiResponse(true, "Land Use dashboard graph fetched successfully",
                                                landUseEmissionsService.getLandUseDashboardGraph(startingYear,
                                                                endingYear)));
        }
}
