package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.swap;

import com.navyn.emissionlog.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mitigation/swaps")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class SwapController {

    private final SwapRepository swapRepository;

    @GetMapping
    @Operation(summary = "Get all swaps", description = "Fetches all swaps for dropdown (e.g. Wetlands Rewetting mitigation).")
    public ResponseEntity<ApiResponse> getAllSwaps() {
        List<Swap> swaps = swapRepository.findAll();
        return ResponseEntity.ok(new ApiResponse(true, "Swaps fetched successfully", swaps));
    }
}
