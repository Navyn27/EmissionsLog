package com.navyn.emissionlog.modules.mitigationProjects.IPPU.dto;

import com.navyn.emissionlog.modules.mitigationProjects.IPPU.model.IPPUMitigation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for a list of IPPU Mitigations, including total calculated values.")
public class IPPUMitigationResponseDTO {

    @Schema(description = "List of IPPU mitigation entries.")
    private List<IPPUMitigation> mitigations;

    @Schema(description = "Total sum of 'mitigationScenario' for all entries in the list.")
    private double totalMitigationScenario;

    @Schema(description = "Total sum of 'reducedEmissionInKtCO2e' for all entries in the list.")
    private double totalReducedEmissionInKtCO2e;
}
