# Agriculture Emissions Update Methods Implementation Guide

## Overview
This guide provides the implementation pattern for adding update methods to all 16 agriculture emission types.

## Pattern for Each Emission Type

### 1. Service Implementation Pattern

```java
@Override
public [EmissionType] update[EmissionType](Long id, [EmissionType]Dto dto) {
    // 1. Find existing record
    [EmissionType] emissions = [repository].findById(id)
        .orElseThrow(() -> new RuntimeException("[EmissionType] record not found with id: " + id));
    
    // 2. Update all input fields from DTO
    emissions.setYear(dto.getYear());
    // ... set other DTO fields ...
    
    // 3. Recalculate all derived fields
    // ... same calculation logic as create method ...
    
    // 4. Save and return
    return [repository].save(emissions);
}
```

### 2. Controller Pattern

```java
@PutMapping("/{id}")
@Operation(summary = "Update [emission type] record")
public ResponseEntity<ApiResponse> update[EmissionType](
        @PathVariable Long id,
        @Valid @RequestBody [EmissionType]Dto dto) {
    [EmissionType] emissions = service.update[EmissionType](id, dto);
    return ResponseEntity.ok(new ApiResponse(true, "[EmissionType] updated successfully", emissions));
}
```

## Emission Types to Implement (16 total)

1. **Aquaculture** - Fish production N2O
2. **Enteric Fermentation** - Livestock CH4 from digestion  
3. **Liming** - CO2 from lime application
4. **Animal Manure and Compost** - CH4 and N2O from organic amendments
5. **Rice Cultivation** - CH4 from flooded rice fields
6. **Synthetic Fertilizer** - N2O from fertilizer application
7. **Urea** - CO2 from urea hydrolysis
8. **Burning** - Emissions from biomass burning
9. **Crop Residues** - N2O from crop residue decomposition
10. **Pasture Excretion** - N2O from animal excreta on pastures
11. **Mineral Soil** - Emissions from land use change
12. **Volatilization** - Indirect N2O from NH3/NOx volatilization
13. **Leaching** - Indirect N2O from N leaching
14. **Atmospheric Deposition** - N2O from deposited N
15. **Leaching and Runoff** - N2O from leached/runoff N
16. **Manure Management** - Complex emissions from manure storage

## Example: Aquaculture Update Implementation

```java
@Override
public AquacultureEmissions updateAquacultureEmissions(Long id, AquacultureEmissionsDto dto) {
    AquacultureEmissions emissions = aquacultureEmissionsRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Aquaculture emissions record not found with id: " + id));
    
    // Update fields
    emissions.setYear(dto.getYear());
    emissions.setActivityDesc(dto.getActivityDesc());
    emissions.setFishProduction(dto.getFishProduction());
    
    // Recalculate derived fields
    emissions.setN2ONEmissions(dto.getFishProduction() * AFOLUConstants.FISH_N20_EF.getValue());
    emissions.setN2OEmissions(dto.getFishProduction() * AFOLUConstants.FISH_N20_EF.getValue() * 44/28 * 1000000);
    emissions.setCO2EqEmissions(emissions.getN2OEmissions() * GWP.N2O.getValue());
    
    return aquacultureEmissionsRepository.save(emissions);
}
```

## Next Steps

1. Add all 16 update method signatures to `AgricultureEmissionsService.java` ✅ DONE
2. Implement all 16 update methods in `AgricultureEmissionsServiceImpl.java`
3. Add 16 PUT endpoints to `AgricultureEmissionsController.java`
