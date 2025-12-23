package com.navyn.emissionlog.modules.mitigationProjects.intervention.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for creating and updating an Intervention")
public class InterventionDto {

    @Schema(description = "The name of the intervention", example = "Renewable Energy Initiative")
    @NotBlank(message = "Intervention name cannot be blank")
    @Size(min = 2, max = 255, message = "Intervention name must be between 2 and 255 characters")
    private String name;
}

