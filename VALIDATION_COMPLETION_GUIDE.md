# Validation Completion Guide

## ✅ ALREADY COMPLETED

### DTOs with Validation:
- ✅ AgricultureAbstractClass (base class)
- ✅ AquacultureEmissionsDto
- ✅ EntericFermentationEmissionsDto
- ✅ LimingEmissionsDto
- ✅ UreaEmissionsDto
- ✅ BiomassGainDto
- ✅ DisturbanceBiomassLossDto
- ✅ RewettedMineralWetlandsDto
- ✅ SolidWasteDto
- ✅ WasteWaterDto

### Controllers with @Valid (Partial):
- ✅ AgricultureEmissionsController (4 of 14 methods)

---

## 📋 TODO: Add Validation to Remaining DTOs

### Agriculture DTOs - Apply This Pattern:

```java
import jakarta.validation.constraints.*;

@Data
public class RiceCultivationEmissionsDto {
    
    @NotBlank(message = "Rice ecosystem is required")
    private String riceEcosystem;
    
    @NotNull(message = "Water regime is required")
    private WaterRegime waterRegime;
    
    @NotNull(message = "Harvested area is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Harvested area must be greater than 0")
    private double harvestedArea;
    
    @NotNull(message = "Cultivation period is required")
    @Min(value = 1, message = "Cultivation period must be at least 1")
    private int cultivationPeriod;
    
    @NotNull(message = "Year is required")
    @Min(value = 1900) @Max(value = 2100)
    private int year;
}
```

Apply to:
- RiceCultivationEmissionsDto
- BurningEmissionsDto
- SyntheticFertilizerEmissionsDto
- AnimalManureAndCompostEmissionsDto
- CropResiduesEmissionsDto
- PastureExcretionsEmissionsDto
- MineralSoilEmissionsDto
- VolatilizationEmissionsDto
- LeachingEmissionsDto
- AtmosphericDepositionEmissionsDto
- LeachingAndRunoffEmissionsDto

### LandUse DTOs - Apply Pattern:
- FirewoodRemovalBiomassLossDto
- HarvestedBiomassLossDto

### Waste DTOs - Apply Pattern:
- IndustrialWasteDto
- GeneralWasteByPopulationDto

---

## 🔧 TODO: Add @Valid to ALL Controller Methods

### Quick Fix Script:

Replace `@RequestBody` with `@Valid @RequestBody` in:

1. **AgricultureEmissionsController** - 10 remaining methods
2. **LandUseEmissionsController** - ALL methods
3. **WasteController** - ALL methods
4. **ActivityController** - ALL methods (if exists)
5. **EICVReportController** - ALL methods
6. **PopulationRecordsController** - ALL methods

### Search & Replace:
```
Find: @RequestBody
Replace: @Valid @RequestBody
```

**IMPORTANT:** Also add the import at the top:
```java
import jakarta.validation.constraints.Valid;
```

---

## 🎯 VALIDATION PATTERNS BY FIELD TYPE

### Year Field:
```java
@NotNull(message = "Year is required")
@Min(value = 1900, message = "Year must be 1900 or later")
@Max(value = 2100, message = "Year must be 2100 or earlier")
private int year;
```

### Positive Number (>0):
```java
@NotNull(message = "[Field] is required")
@DecimalMin(value = "0.0", inclusive = false, message = "[Field] must be greater than 0")
private double field;
```

### Non-Negative Number (>=0):
```java
@NotNull(message = "[Field] is required")
@DecimalMin(value = "0.0", inclusive = true, message = "[Field] cannot be negative")
private double field;
```

### Required String:
```java
@NotBlank(message = "[Field] is required")
@Size(max = 255, message = "[Field] cannot exceed 255 characters")
private String field;
```

### Required Enum:
```java
@NotNull(message = "[Field] is required")
private EnumType field;
```

### Required UUID:
```java
@NotNull(message = "[Field] is required")
private UUID field;
```

---

## ✅ TESTING VALIDATION

After completing, test with:

### Missing Required Field:
```json
POST /agriculture/aquacultureEmissions
{
  "activityDesc": "Test"
}
Response: "Year is required"
```

### Negative Value:
```json
POST /agriculture/aquacultureEmissions
{
  "year": 2024,
  "fishProduction": -10
}
Response: "Fish production cannot be negative"
```

### Duplicate Year:
```json
POST /agriculture/aquacultureEmissions
{
  "year": 2023
}
Response: "A record for this year already exists..."
```

---

## 🎉 COMPLETION CHECKLIST

- [ ] All Agriculture DTOs have validation
- [ ] All LandUse DTOs have validation  
- [ ] All Waste DTOs have validation
- [ ] All Controller POST methods have @Valid
- [ ] All Controller PUT methods have @Valid
- [ ] Test duplicate year validation
- [ ] Test negative number validation
- [ ] Test required field validation
- [ ] Test string length validation
