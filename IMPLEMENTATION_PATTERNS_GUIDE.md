# EmissionsLog - Implementation Patterns & Architecture Guide

## 📚 **TABLE OF CONTENTS**
1. [Project Structure](#project-structure)
2. [Architecture Patterns](#architecture-patterns)
3. [Module Structure Template](#module-structure-template)
4. [Code Patterns & Conventions](#code-patterns--conventions)
5. [Step-by-Step Implementation Guide](#step-by-step-implementation-guide)
6. [Complete Example](#complete-example)

---

## 🏗️ **PROJECT STRUCTURE**

```
src/main/java/com/navyn/emissionlog/
├── Config/               # JWT, Security, Swagger configuration
├── Enums/               # Enums for constants, types, and categories
├── Exceptions/          # Global exception handler
├── modules/             # Feature modules (each emission category)
│   ├── agricultureEmissions/
│   ├── LandUseEmissions/
│   ├── wasteEmissions/
│   ├── stationaryEmissions/
│   ├── transportEmissions/
│   ├── activities/
│   └── [YourNewModule]/
└── utils/              # Shared utilities, Specifications, ApiResponse
```

---

## 🎯 **ARCHITECTURE PATTERNS**

### **Layered Architecture**
```
Controller Layer  →  Service Layer  →  Repository Layer  →  Database
     ↓                    ↓                    ↓
    DTOs            Business Logic         Entities
                    Calculations         JPA Queries
```

### **Key Design Patterns Used:**
1. **Repository Pattern** - Data access abstraction
2. **Service Layer Pattern** - Business logic encapsulation
3. **DTO Pattern** - Data transfer objects
4. **Specification Pattern** - Dynamic query building
5. **Factory Pattern** - ApiResponse construction
6. **Dependency Injection** - @RequiredArgsConstructor

---

## 📦 **MODULE STRUCTURE TEMPLATE**

### **Standard Module Layout:**
```
modules/[YourModule]/
├── [YourModule]Controller.java       # REST endpoints
├── [YourModule]Service.java          # Service interface
├── [YourModule]ServiceImpl.java      # Service implementation
├── models/                            # Entity classes
│   ├── [YourAbstractClass].java      # (Optional) Base class
│   └── [SubCategory]/                # Organized by subcategory
│       └── [Entity].java
├── repositories/                      # JPA repositories
│   └── [SubCategory]/
│       └── [Entity]Repository.java
└── dtos/                             # Data Transfer Objects
    └── [SubCategory]/
        └── [Entity]Dto.java
```

### **Example from LandUseEmissions:**
```
LandUseEmissions/
├── LandUseEmissionsController.java
├── LandUseEmissionsService.java
├── LandUseEmissionsServiceImpl.java
├── models/
│   ├── BiomassGain.java
│   ├── DisturbanceBiomassLoss.java
│   └── RewettedMineralWetlands.java
├── Repositories/
│   ├── BiomassGainRepository.java
│   ├── DisturbanceBiomassLossRepository.java
│   └── RewettedMineralWetlandsRepository.java
└── Dtos/
    ├── BiomassGainDto.java
    ├── DisturbanceBiomassLossDto.java
    └── RewettedMineralWetlandsDto.java
```

---

## 🔧 **CODE PATTERNS & CONVENTIONS**

### **1. Entity (Model) Pattern**

#### **Option A: With Abstract Base Class**
```java
@MappedSuperclass
@Data
public abstract class YourAbstractClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @NotNull(message = "Year is required")
    @Min(value = 1900) @Max(value = 2100)
    @Column(unique = true)
    private int year;
}

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "your_entity_name")
public class YourEntity extends YourAbstractClass {
    private String fieldName;
    private double inputValue;
    
    // Calculated emission fields
    private double calculatedEmissions;
    private double CO2EqEmissions;
}
```

#### **Option B: Standalone Entity**
```java
@Entity
@Data
public class YourEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private Integer year = LocalDate.now().getYear();
    
    @Enumerated(EnumType.STRING)
    private YourEnumType category;
    
    private double inputValue = 0.0;
    
    // Calculated fields
    private double calculatedValue = 0.0;
    private double CO2EqEmissions = 0.0;
}
```

**Key Points:**
- ✅ Use `UUID` for primary keys
- ✅ Use `@Column(unique = true)` for year
- ✅ Include calculated emission fields
- ✅ Use `@Enumerated(EnumType.STRING)` for enums
- ✅ Use Lombok `@Data` for getters/setters
- ✅ Table names use snake_case

---

### **2. DTO Pattern**

```java
@Data
public class YourEntityDto {
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;
    
    @NotNull(message = "Category is required")
    private YourEnumType category;
    
    @NotNull(message = "Input value is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Value must be greater than 0")
    private Double inputValue;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
}
```

**Key Points:**
- ✅ Only include input fields (NOT calculated fields)
- ✅ Add validation annotations
- ✅ Use wrapper types (Integer, Double) for nullability
- ✅ Use Lombok `@Data`

---

### **3. Repository Pattern**

```java
@Repository
public interface YourEntityRepository extends JpaRepository<YourEntity, UUID>, 
                                              JpaSpecificationExecutor<YourEntity> {
    
    // Optional: Custom queries
    @Query("SELECT e FROM YourEntity e WHERE e.year BETWEEN :startYear AND :endYear ORDER BY e.year DESC")
    List<YourEntity> findByYearRange(@Param("startYear") int startYear, 
                                      @Param("endYear") int endYear);
}
```

**Key Points:**
- ✅ Extend `JpaRepository<Entity, UUID>`
- ✅ Extend `JpaSpecificationExecutor<Entity>` for filtering
- ✅ Add custom `@Query` methods if needed
- ✅ Use `@Repository` annotation

---

### **4. Service Interface Pattern**

```java
public interface YourModuleService {
    
    // Create methods - return Entity, accept DTO
    YourEntity createEntity(YourEntityDto dto);
    
    // GetAll methods - with optional filters
    List<YourEntity> getAllEntities(Integer year, YourEnumType category);
    
    // Additional CRUD methods as needed
}
```

**Key Points:**
- ✅ Create methods: `Entity create[Entity](EntityDto dto)`
- ✅ GetAll methods: `List<Entity> getAll[Entities](filters...)`
- ✅ Filters are optional (use `Integer` not `int`)

---

### **5. Service Implementation Pattern**

```java
@Service
@RequiredArgsConstructor
public class YourModuleServiceImpl implements YourModuleService {
    
    private final YourEntityRepository yourEntityRepository;
    // Inject all needed repositories
    
    @Override
    public YourEntity createEntity(YourEntityDto dto) {
        // 1. Create entity instance
        YourEntity entity = new YourEntity();
        
        // 2. Map DTO fields to entity
        entity.setYear(dto.getYear());
        entity.setCategory(dto.getCategory());
        entity.setInputValue(dto.getInputValue());
        
        // 3. Perform calculations
        double intermediateValue = dto.getInputValue() * YourConstants.CONSTANT.getValue();
        entity.setCalculatedValue(intermediateValue);
        
        // 4. Calculate CO2 equivalent
        entity.setCO2EqEmissions(
            entity.getCalculatedValue() * YourConstants.FACTOR.getValue()
        );
        
        // 5. Save and return
        return yourEntityRepository.save(entity);
    }
    
    @Override
    public List<YourEntity> getAllEntities(Integer year, YourEnumType category) {
        // Build specification with filters
        Specification<YourEntity> spec = Specification.<YourEntity>where(hasYear(year))
                .and(hasCategory(category));
        
        // Execute query with sorting
        return yourEntityRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }
}
```

**Key Points:**
- ✅ Use `@Service` and `@RequiredArgsConstructor`
- ✅ Constructor injection for repositories
- ✅ Map DTO → Entity manually
- ✅ Perform calculations before saving
- ✅ Use Specifications for filtering
- ✅ Sort by year DESC

---

### **6. Controller Pattern**

```java
@RestController
@RequestMapping("/yourModule")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class YourModuleController {
    
    private final YourModuleService yourModuleService;
    
    // CREATE endpoint
    @PostMapping("/entity")
    public ResponseEntity<YourEntity> createEntity(
            @Valid @RequestBody YourEntityDto dto) {
        YourEntity entity = yourModuleService.createEntity(dto);
        return ResponseEntity.ok(entity);
    }
    
    // GET ALL endpoint with filters
    @GetMapping("/entity")
    public ResponseEntity<List<YourEntity>> getAllEntities(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) YourEnumType category) {
        List<YourEntity> entities = yourModuleService.getAllEntities(year, category);
        return ResponseEntity.ok(entities);
    }
}
```

**Key Points:**
- ✅ Use `@RestController`
- ✅ Use `@RequestMapping("/moduleName")`
- ✅ Add `@SecurityRequirement(name = "BearerAuth")`
- ✅ Use `@RequiredArgsConstructor` for DI
- ✅ POST: `@Valid @RequestBody`
- ✅ GET: `@RequestParam(required = false)` for filters
- ✅ Return entities directly (not ApiResponse for LandUse style)
- ✅ Or use ApiResponse (for Agriculture/Waste style)

---

### **7. Specification Pattern**

```java
public class YourModuleSpecification {
    
    public static <T> Specification<T> hasYear(Integer year) {
        return (root, query, cb) -> {
            if (year == null) return cb.conjunction(); // no filter
            return cb.equal(root.get("year"), year);
        };
    }
    
    public static <T> Specification<T> hasCategory(YourEnumType category) {
        return (root, query, cb) -> {
            if (category == null) return cb.conjunction();
            return cb.equal(root.get("category"), category);
        };
    }
}
```

**Location:** `utils/Specifications/YourModuleSpecification.java`

**Key Points:**
- ✅ Generic `<T>` for reusability
- ✅ Null check → `cb.conjunction()` (no filter)
- ✅ Static methods
- ✅ Import in ServiceImpl: `import static ...Specification.*;`

---

### **8. Enum/Constants Pattern**

```java
@Getter
public enum YourConstants {
    CONSTANT_NAME_1(1.5),
    CONSTANT_NAME_2(25.0),
    CONVERSION_FACTOR(0.47);
    
    private final Double value;
    
    YourConstants(Double value) {
        this.value = value;
    }
}
```

**Location:** `Enums/YourModule/YourConstants.java`

**Key Points:**
- ✅ Use for emission factors and constants
- ✅ Use `Double` for precision
- ✅ Use Lombok `@Getter`

---

## 🚀 **STEP-BY-STEP IMPLEMENTATION GUIDE**

### **Step 1: Plan Your Module**
1. Define what emissions you're tracking
2. Identify input fields (what user provides)
3. Identify calculated fields (what you compute)
4. Identify filter fields (year, category, etc.)
5. List calculation formulas and constants

### **Step 2: Create Enums & Constants**
```bash
# Create in: Enums/YourModule/
1. YourEnumType.java        # Categories/types
2. YourConstants.java       # Calculation constants
```

### **Step 3: Create Entities**
```bash
# Create in: modules/yourModule/models/
1. YourAbstractClass.java   # (Optional) if multiple entities share fields
2. YourEntity.java          # Main entity
```

### **Step 4: Create DTOs**
```bash
# Create in: modules/yourModule/dtos/
1. YourEntityDto.java       # With validation annotations
```

### **Step 5: Create Repositories**
```bash
# Create in: modules/yourModule/repositories/
1. YourEntityRepository.java
```

### **Step 6: Create Specifications**
```bash
# Create in: utils/Specifications/
1. YourModuleSpecification.java
```

### **Step 7: Create Service**
```bash
# Create in: modules/yourModule/
1. YourModuleService.java      # Interface
2. YourModuleServiceImpl.java  # Implementation
```

### **Step 8: Create Controller**
```bash
# Create in: modules/yourModule/
1. YourModuleController.java
```

### **Step 9: Test**
1. Test POST endpoint with valid data
2. Test validation (missing fields, negative values)
3. Test GET endpoint with filters
4. Test duplicate year handling

---

## 📝 **COMPLETE EXAMPLE**

### **Scenario: Building a "SoilEmissions" Module**

#### **1. Enums**
```java
// Enums/SoilEmissions/SoilType.java
public enum SoilType {
    MINERAL,
    ORGANIC,
    PEAT
}

// Enums/SoilEmissions/SoilConstants.java
@Getter
public enum SoilConstants {
    N2O_EMISSION_FACTOR(0.01),
    CO2_CONVERSION_FACTOR(44.0/28.0);
    
    private final Double value;
    SoilConstants(Double value) { this.value = value; }
}
```

#### **2. Entity**
```java
// modules/soilEmissions/models/SoilEmission.java
@Entity
@Data
public class SoilEmission {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private Integer year;
    
    @Enumerated(EnumType.STRING)
    private SoilType soilType;
    
    private double nitrogenApplied;
    private double areaSize;
    
    // Calculated
    private double N2OEmissions;
    private double CO2EqEmissions;
}
```

#### **3. DTO**
```java
// modules/soilEmissions/dtos/SoilEmissionDto.java
@Data
public class SoilEmissionDto {
    @NotNull @Min(1900) @Max(2100)
    private Integer year;
    
    @NotNull
    private SoilType soilType;
    
    @NotNull @DecimalMin("0.0")
    private Double nitrogenApplied;
    
    @NotNull @DecimalMin("0.0")
    private Double areaSize;
}
```

#### **4. Repository**
```java
// modules/soilEmissions/repositories/SoilEmissionRepository.java
@Repository
public interface SoilEmissionRepository 
        extends JpaRepository<SoilEmission, UUID>, 
                JpaSpecificationExecutor<SoilEmission> {
}
```

#### **5. Specification**
```java
// utils/Specifications/SoilEmissionSpecification.java
public class SoilEmissionSpecification {
    public static <T> Specification<T> hasYear(Integer year) {
        return (root, query, cb) -> 
            year == null ? cb.conjunction() : cb.equal(root.get("year"), year);
    }
    
    public static <T> Specification<T> hasSoilType(SoilType soilType) {
        return (root, query, cb) -> 
            soilType == null ? cb.conjunction() : cb.equal(root.get("soilType"), soilType);
    }
}
```

#### **6. Service**
```java
// modules/soilEmissions/SoilEmissionService.java
public interface SoilEmissionService {
    SoilEmission createSoilEmission(SoilEmissionDto dto);
    List<SoilEmission> getAllSoilEmissions(Integer year, SoilType soilType);
}

// modules/soilEmissions/SoilEmissionServiceImpl.java
@Service
@RequiredArgsConstructor
public class SoilEmissionServiceImpl implements SoilEmissionService {
    private final SoilEmissionRepository repository;
    
    @Override
    public SoilEmission createSoilEmission(SoilEmissionDto dto) {
        SoilEmission emission = new SoilEmission();
        emission.setYear(dto.getYear());
        emission.setSoilType(dto.getSoilType());
        emission.setNitrogenApplied(dto.getNitrogenApplied());
        emission.setAreaSize(dto.getAreaSize());
        
        // Calculate N2O emissions
        double n2o = dto.getNitrogenApplied() * 
                     dto.getAreaSize() * 
                     SoilConstants.N2O_EMISSION_FACTOR.getValue();
        emission.setN2OEmissions(n2o);
        
        // Calculate CO2 equivalent
        emission.setCO2EqEmissions(n2o * GWP.N2O.getValue());
        
        return repository.save(emission);
    }
    
    @Override
    public List<SoilEmission> getAllSoilEmissions(Integer year, SoilType soilType) {
        Specification<SoilEmission> spec = 
            Specification.<SoilEmission>where(hasYear(year))
                         .and(hasSoilType(soilType));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }
}
```

#### **7. Controller**
```java
// modules/soilEmissions/SoilEmissionController.java
@RestController
@RequestMapping("/soilEmissions")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class SoilEmissionController {
    private final SoilEmissionService service;
    
    @PostMapping
    public ResponseEntity<SoilEmission> create(@Valid @RequestBody SoilEmissionDto dto) {
        return ResponseEntity.ok(service.createSoilEmission(dto));
    }
    
    @GetMapping
    public ResponseEntity<List<SoilEmission>> getAll(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) SoilType soilType) {
        return ResponseEntity.ok(service.getAllSoilEmissions(year, soilType));
    }
}
```

---

## ✅ **CHECKLIST FOR NEW MODULE**

- [ ] Enums created
- [ ] Constants enum created
- [ ] Entity/Model created with @Entity, @Data, UUID id
- [ ] Year field with @Column(unique = true)
- [ ] Calculated emission fields defined
- [ ] DTO created with validation annotations
- [ ] Repository created extending JpaRepository + JpaSpecificationExecutor
- [ ] Specification class created
- [ ] Service interface created
- [ ] Service implementation with calculations
- [ ] Controller with @Valid, @SecurityRequirement
- [ ] Tested POST with valid data
- [ ] Tested validation errors
- [ ] Tested GET with filters
- [ ] Tested duplicate year handling

---

## 🎓 **NAMING CONVENTIONS**

| Item | Pattern | Example |
|------|---------|---------|
| Package | camelCase | `soilEmissions` |
| Class | PascalCase | `SoilEmission` |
| Entity Table | snake_case | `soil_emission` |
| DTO | Entity + Dto | `SoilEmissionDto` |
| Repository | Entity + Repository | `SoilEmissionRepository` |
| Service | Entity + Service | `SoilEmissionService` |
| Controller | Module + Controller | `SoilEmissionController` |
| Enum | PascalCase | `SoilType` |
| Constants | UPPER_SNAKE_CASE | `N2O_EMISSION_FACTOR` |
| Fields | camelCase | `nitrogenApplied` |

---

## 🔥 **COMMON PITFALLS TO AVOID**

1. ❌ Don't include calculated fields in DTOs
2. ❌ Don't forget @Valid in controller
3. ❌ Don't use primitive int for year (use Integer)
4. ❌ Don't forget @Column(unique = true) for year
5. ❌ Don't forget JpaSpecificationExecutor in repository
6. ❌ Don't forget Sort.by DESC in getAll methods
7. ❌ Don't expose internal errors to users
8. ❌ Don't forget @SecurityRequirement on controller
9. ❌ Don't use ApiResponse inconsistently (pick a style)
10. ❌ Don't forget validation annotations on DTO

---

## 🎯 **READY TO BUILD!**

You now have a complete understanding of the codebase architecture. When you're ready to request a new feature, I'll follow these exact patterns to ensure consistency with your existing implementation.

**What to tell me for a new feature:**
1. **Module name** (e.g., "Forestry Emissions")
2. **Input fields** (what user provides)
3. **Categories/Types** (enums needed)
4. **Calculation formulas** (emission factors)
5. **Filter fields** (besides year)

I'll build it following these patterns exactly! 🚀
