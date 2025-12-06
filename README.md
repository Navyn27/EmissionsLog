# EmissionsLog - GHG Emissions Management System

## Overview

EmissionsLog is a comprehensive Spring Boot-based greenhouse gas (GHG) emissions tracking and mitigation management system. The application enables organizations to track Scope 1, 2, and 3 emissions across multiple sectors (Energy, Agriculture, Waste, Land Use) while also managing carbon sequestration and emissions mitigation projects.

## Technology Stack

- **Backend Framework:** Spring Boot 3.4.2
- **Language:** Java 21
- **Database:** PostgreSQL with JPA/Hibernate
- **Security:** Spring Security with JWT authentication
- **Build Tool:** Maven
- **API Documentation:** Swagger/OpenAPI (available at `/api-docs`)
- **Excel Integration:** Apache POI for data import/export
- **Containerization:** Docker support

**Server Configuration:**
- Default Port: 8088
- Context Path: `/api/v1`
- Full Base URL: `http://localhost:8088/api/v1`

## Project Architecture

### Core Design Patterns

1. **Repository Pattern:** JPA repositories with `JpaSpecificationExecutor` for dynamic filtering
2. **Service Layer Pattern:** Business logic separated from controllers
3. **DTO Pattern:** Data Transfer Objects for API requests/responses
4. **Specification Pattern:** Dynamic query building using Spring Data JPA Specifications
5. **Abstract Base Classes:** Shared validation and year-based constraints
6. **Enum Constants:** Type-safe constants for emission factors, units, and categories

### Module Structure

The project is organized into several major modules under `/src/main/java/com/navyn/emissionlog/`:

```
emissionlog/
├── modules/
│   ├── agricultureEmissions/        # AFOLU sector emissions
│   ├── stationaryEmissions/         # Fixed installation emissions (fuel combustion)
│   ├── transportEmissions/          # Mobile source emissions
│   ├── waste/                       # Waste management emissions
│   ├── landUseEmissions/            # Land use change emissions
│   ├── mitigationProjects/          # Carbon sequestration and mitigation tracking
│   └── eicv/                        # Household sanitation data (EICV reports)
├── models/                          # Core entities (Activity, Fuel, Region, etc.)
├── utils/                           # Utilities (DashboardData, ResponseWrapper, etc.)
├── Enums/                           # Enumerations (GWP, units, categories, etc.)
├── config/                          # Spring configurations
└── security/                        # JWT authentication and authorization
```

## Emissions Tracking Modules

### 1. Agriculture Emissions (AFOLU)

**Location:** `/modules/agricultureEmissions/`

Tracks agricultural and forestry emissions using IPCC methodologies. Each emission type is a separate entity with specific calculations.

**Sub-modules:**
- **Aquaculture Emissions:** N₂O from fish farming
- **Enteric Fermentation:** CH₄ from livestock digestion (14 species types)
- **Manure Management:** CH₄ and N₂O from manure (12 livestock types, 13 management systems)
- **Rice Cultivation:** CH₄ from paddy fields (4 ecosystems, 3 water regimes, 2 cultivation types)
- **Liming:** CO₂ from lime application (4 lime types)
- **Animal Manure & Compost:** N₂O from organic amendments
- **Synthetic Fertilizer:** N₂O from synthetic nitrogen fertilizers
- **Urea Application:** CO₂ from urea hydrolysis
- **Crop Residue Burning:** CH₄, N₂O, and NOx from biomass burning

**Key Features:**
- Unique enums per module prevent cross-contamination of options
- Auto-calculation of emissions using standardized factors
- Extends `AgricultureAbstractClass` for year validation
- No `description` field (requirement-specific)

**Example Entity:** `EntericFermentationEmissions`
- Input: year, livestock species, population, average weight
- Calculated: gross energy, methane emissions, CO₂eq

**API Pattern:**
```
POST /agriculture/{module}         # Create record
GET  /agriculture/{module}         # Get all with filters
GET  /agriculture/{module}/{id}    # Get by ID
PUT  /agriculture/{module}/{id}    # Update record
DELETE /agriculture/{module}/{id}  # Delete record
```

### 2. Stationary Emissions

**Location:** `/modules/stationaryEmissions/`

Tracks emissions from stationary combustion sources (boilers, generators, furnaces).

**Key Features:**
- Fuel-based calculations with extensive fuel database
- Unit conversion system (volume, mass, energy)
- Region-specific emission factors
- Heating values and density conversions

**Calculation:**
```
Emissions = FuelAmount × EmissionFactor × HeatingValue
```

### 3. Transport Emissions

**Location:** `/modules/transportEmissions/`

Dual-approach system for mobile source emissions.

**Calculation Methods:**

**Method 1: Fuel-Based**
- Input: Fuel type, amount, region, transport type, engine type
- Uses `TransportFuelEmissionFactors`
- Emission = FuelAmount × EmissionFactor

**Method 2: Vehicle-Data-Based**
- Input: Vehicle type, distance traveled, fuel type, region
- Uses `TransportVehicleDataEmissionFactors`
- Emission = Distance × EmissionFactor

**Enums:**
- `TransportModes`: ROAD, RAIL, AIR, SEA, PIPELINE, OFFROAD, OTHER
- `VehicleEngineType`: FOUR_STROKE, TWO_STROKE, OFF_ROAD_TRUCK, ANY
- `TransportType`: 20+ types (RAIL, AGRICULTURE_EQUIPMENT, MARINE, AIRCRAFT, etc.)

**Key Feature:** Flexible wildcard matching with "ANY" values for engine types and transport types with priority-based specificity scoring.

### 4. Waste Emissions

**Location:** `/modules/waste/`

Tracks GHG emissions from waste management activities.

**Data Structure:**
- Abstract base class: `WasteDataAbstract`
- Year-based tracking with validation
- Separate CH₄, N₂O, fossil CO₂, and biogenic CO₂ tracking

### 5. Land Use Emissions

**Location:** `/modules/landUseEmissions/`

Tracks carbon stock changes from land use activities.

**Models:**
- `BiomassGain`: Carbon sequestration from biomass growth
- `DisturbanceBiomassLoss`: Emissions from disturbances
- `FirewoodRemovalBiomassLoss`: Emissions from wood harvesting
- `HarvestedBiomassLoss`: Emissions from harvest activities
- `RewettedMineralWetlands`: Emissions/sequestration from wetland restoration

**Features:**
- `LandCategory` enum for land classification
- Specification-based filtering by year and land category
- Calculated fields exclude from DTO (GainedBiomassCarbon, etc.)

## Mitigation Projects Module

**Location:** `/modules/mitigationProjects/`

Tracks carbon sequestration and emissions reduction projects. Currently **17 active projects** (10 AFOLU + 7 Waste).

### AFOLU Mitigation Projects (10)

#### 1. Wetland Parks Mitigation
**Unique Feature:** Composite uniqueness (year + treeCategory)
- **Categories:** BAMBOO_SPP, ACACIA_SPP, BROADLEAF_TREES
- **Input:** year, treeCategory, cumulativeArea, areaPlanted, abovegroundBiomassAGB
- **Auto-fetches:** Previous year's AGB from database
- **Calculation:**
  ```
  AGB Growth = Current AGB - Previous Year AGB
  Biomass Growth = AGB Growth × 0.66
  Total Biomass = Cumulative Area × Biomass Growth
  Carbon Increase = Total Biomass × 0.47
  Mitigated Emissions = (Carbon × 3.6666667) / 1000 ktCO₂e
  ```
- **Endpoint:** `/mitigation/wetlandParks`

#### 2. Settlement Trees Mitigation
**Unique Feature:** Simple year uniqueness, includes belowground biomass
- **Input:** year, cumulativeNumberOfTrees, numberOfTreesPlanted, agbSingleTreePreviousYear, agbSingleTreeCurrentYear
- **User provides:** Both previous and current AGB (not auto-fetched)
- **Calculation:** Total Biomass = Aboveground × (1 + 0.27) for BGB inclusion
- **Endpoint:** `/mitigation/settlementTrees`

#### 3. Street Trees Mitigation
**Unique Feature:** Identical to Settlement Trees
- Same structure and calculations as Settlement Trees
- **Endpoint:** `/mitigation/streetTrees`

#### 4. Green Fences Mitigation
**Unique Feature:** Household-based carbon sequestration
- **Input:** year, cumulativeNumberOfHouseholds, numberOfHouseholdsWith10m2Fence, agbOf10m2LiveFence
- **Note:** Uses AGB ONLY for emissions (not AGB+BGB)
- **Direct input:** AGB in tonnes DM (not m³)
- **Endpoint:** `/mitigation/greenFences`

#### 5. Crop Rotation Mitigation
**Unique Feature:** Different BGB ratio (0.22 vs 0.27)
- **Input:** year, croplandUnderCropRotation, abovegroundBiomass, increasedBiomass
- **Calculation:** Total Biomass = cropland × ABG × increased × (1 + 0.22)
- **Endpoint:** `/mitigation/cropRotation`

#### 6. Zero Tillage Mitigation
**Unique Feature:** Soil carbon + NET emissions (accounts for urea offset)
- **Input:** year, areaUnderZeroTillage (simplest: only 2 fields!)
- **Calculation:**
  ```
  Carbon Increase = area × 0.37
  Gross Savings = carbon × 3.6666667 / 1000
  Urea Applied = area × 0.1
  Urea Emissions = urea × 0.2
  NET Savings = Gross - (Urea Emissions / 1000)
  ```
- **Endpoint:** `/mitigation/zeroTillage`

#### 7. Protective Forest Mitigation
**Unique Feature:** Composite uniqueness (year + category), 2 species
- **Categories:** PINUS_SPP, EUCALYPTUS_SPP
- **Auto-fetches:** Previous year's AGB from database
- **Similar to:** Wetland Parks but only 2 categories
- **Endpoint:** `/mitigation/protectiveForest`

#### 8-10. Improved MMS (3 Independent Modules)
**Previously:** One combined module
**Now Split Into:**

**8. Manure Covering Mitigation**
- **Strategy:** N₂O Reduction (30%)
- **Constant:** 21.5 tonnes CO₂e/cow/year
- **Input:** year, numberOfCows
- **Endpoint:** `/mitigation/manure-covering`

**9. Adding Straw Mitigation**
- **Strategy:** CH₄ Reduction (30%)
- **Constant:** 0.781 tonnes CO₂e/cow/year
- **Input:** year, numberOfCows
- **Endpoint:** `/mitigation/adding-straw`

**10. Daily Spread Mitigation**
- **Strategy:** CH₄ Reduction (50% - highest!)
- **Constant:** 0.781 tonnes CO₂e/cow/year
- **Input:** year, numberOfCows
- **Endpoint:** `/mitigation/daily-spread`

### Waste Mitigation Projects (7)

#### 1. Waste-to-Energy (WtE)
**Unique Feature:** First waste mitigation project
- **Input:** year, wasteToWtE (t/year), bauEmissionsSolidWaste
- **Constant:** 0.7 tCO₂eq/t (net emission factor)
- **Calculation:** GHG Reduction = 0.7 × waste
- **Endpoint:** `/mitigation/wasteToEnergy`

#### 2. Landfill Gas Utilization
**Unique Feature:** Year-based activation (active AFTER 2028)
- **Logic:** If year ≤ 2028, projectReduction = 0; else calculate
- **Input:** year, bauSolidWasteEmissions, projectReduction40PercentEfficiency
- **Endpoint:** `/mitigation/landfillGasUtilization`

#### 3. MBT/Aerobic Composting
**Unique Feature:** Operation status enum controls calculations
- **Status Enum:** CONSTRUCTION_PRE_OP (0 days), HALF_YEAR_OPERATION (182.5 days), FULL_OPERATION (365 days)
- **Input in:** tons/DAY (converted to tons/year based on status)
- **Constant:** 1.12 tCO₂eq/ton
- **Endpoint:** `/mitigation/mbtComposting`

#### 4. EPR Circular Economy Plastic Waste
**Unique Feature:** Year-over-year growth tracking
- **Input:** year, bauSolidWasteEmissions, plasticWasteGrowthFactor, recyclingRateWithEPR, plasticWasteBaseTonnesPerYear (first year only)
- **Growth:** Current = Previous × GrowthFactor
- **Compares:** EPR rate vs 3% BAU baseline
- **Constant:** 2.60 tCO₂eq/ton
- **Endpoint:** `/mitigation/eprPlasticWaste`

#### 5. Kigali FSTP (Fecal Sludge Treatment Plant)
**Unique Feature:** Phase-based capacity at Masaka
- **Phases:** NONE (0), PHASE_I (200 m³/day), PHASE_II (1000), PHASE_III (1500)
- **Efficiency:** 85%
- **Constant:** 70 kg CO₂e per m³ sludge
- **Endpoint:** `/mitigation/kigaliFSTP`

#### 6. Kigali WWTP (Wastewater Treatment Plant)
**Unique Feature:** Year-based household connection rates at GITICYINYONI
- **Connection Rates:** ≤2026: 0%, 2027: 65%, 2028: 70%, 2029: 75%, 2030+: 80%
- **Phases:** NONE (0), PHASE_I (12,000 m³/day), PHASE_II (20,000), PHASE_III (50,000)
- **Connected Households:** 208,000 (fixed)
- **Efficiency:** 85%
- **Constant:** 5.60 kg CO₂e per m³
- **Endpoint:** `/mitigation/kigaliWWTP`

#### 7. ISWM (Integrated Solid Waste Management)
**Unique Feature:** Simplest structure (direct user input)
- **Input:** year, bauEmissions, annualReduction
- **Calculation:** Adjusted = BAU - Reduction (straightforward subtraction)
- **No constants, no enums, no complex logic**
- **Endpoint:** `/mitigation/iswm`

### Unit Conversion System (7 AFOLU Projects)

All AFOLU mitigation projects support flexible unit input with automatic conversion:

**Unit Enums:**
- `AreaUnits`: ha, m², acres, etc. → Standard: hectares
- `VolumeUnits`: m³, liters, gallons → Standard: cubic meters
- `BiomassUnit`: tonnes DM, kg DM, grams DM → Standard: tonnes DM
- `BiomassDensityUnit`: tonnes DM/ha, kg DM/ha → Standard: tonnes DM/ha
- `VolumePerAreaUnit`: m³/ha, m³/m² → Standard: m³/ha

**Pattern:** User inputs value + unit → Service converts to standard → Calculates → Stores in standard units

## Energy Mitigation Projects

### Cookstove Mitigation
**Location:** `/modules/mitigationProjects/energy/cookstove/`

**Unique Feature:** Multi-stove-type tracking with year-based cumulative calculations

**Components:**
- **StoveType:** Master data for stove types (name, baselinePercentage)
- **StoveMitigationYear:** Year-specific installation data

**Input:** year, stoveTypeId, unitsInstalledThisYear, bau

**Calculations:**
```
differenceInstalled = unitsThisYear - previousYearUnits
constant = (baselinePercentage × 0.15) / 0.25
avoidedEmissions = (constant × differenceInstalled) × 0.029 × ((4×112×0.647) + 8.43) × 10⁻³
totalAvoidedEmission = sum of all stove types for this year
adjustment = BAU - totalAvoidedEmission
```

**Key Features:**
- Auto-fetches previous year units for same stove type
- Supports multiple stove types per year
- Year-aggregated totals across all stove types

**Endpoints:**
- `POST /mitigation/cookstoves` - Create installation record
- `GET /mitigation/cookstoves` - Get all records
- `GET /mitigation/cookstoves/{id}` - Get by ID
- `PUT /mitigation/cookstoves/{id}` - Update record
- `DELETE /mitigation/cookstoves/{id}` - Delete record
- `GET /mitigation/cookstoves/stove-type/{stoveTypeId}` - Filter by stove type
- `GET /mitigation/cookstoves/year/{year}` - Filter by year

## Dashboard System

**Location:** `/utils/DashboardData.java`, `/modules/activity/ActivityServiceImpl.java`, `/modules/mitigationProjects/dashboard/`

### Main Dashboard (Emissions)

**Purpose:** Aggregate total emissions from all sources

**Endpoints:**
- `GET /activities/stats/dashboard` - Overall summary (optional year range)
- `GET /activities/stats/dashboard/graph/groupedByMonth?year={year}` - Monthly breakdown
- `GET /activities/stats/dashboard/graph/groupedByYear?startingYear={start}&endingYear={end}` - Yearly breakdown

**Aggregated Sources (9):**
1. Activities (Transport & Stationary)
2. Waste Data
3. Aquaculture Emissions
4. Enteric Fermentation Emissions
5. Liming Emissions
6. Animal Manure & Compost Emissions
7. Rice Cultivation Emissions
8. Synthetic Fertilizer Emissions
9. Urea Emissions

**Emissions Tracked:**
- `totalN2OEmissions`
- `totalFossilCO2Emissions`
- `totalBioCO2Emissions`
- `totalCH4Emissions`
- `totalCO2EqEmissions` (calculated using GWP factors)

**CO₂ Equivalent Calculation:**
```
CO₂eq = FossilCO₂ + BioCO₂ + (CH₄ × 25) + (N₂O × 298)
```

**Global Warming Potential (GWP) Factors:**
- CH₄ (Methane): 25
- N₂O (Nitrous Oxide): 298
- CO₂ (Carbon Dioxide): 1 (baseline)

### Mitigation Dashboard

**Purpose:** Track total mitigation (carbon sequestration and emissions reductions)

**Endpoints:**
- `GET /mitigation/dashboard/summary?startingYear={start}&endingYear={end}` - Summary across year range
- `GET /mitigation/dashboard/graph?startingYear={start}&endingYear={end}` - Yearly breakdown for graphs

**Aggregated Projects (17):**
- All 10 AFOLU projects
- All 7 Waste projects

**Dashboard Data Fields:**
- `totalMitigationEmissions` (ktCO₂e)
- `netEmissions` (Gross Emissions - Total Mitigation)
- Year-based grouping for trend analysis

**Calculation Flow:**
1. Fetch all mitigation records in year range
2. Group by year
3. Sum mitigation values for each project
4. Calculate total mitigation per year
5. Compute net emissions (from main dashboard - mitigation)

## Core Models

### Activity
**Purpose:** Main emission activity record (transport and stationary)

**Fields:**
- `activityYear`: Timestamp of activity
- `fossilCO2Emissions`, `bioCO2Emissions`, `CH4Emissions`, `N2OEmissions`
- `CO2EqEmissions`: Auto-calculated using GWP factors
- Relationships: Fuel, Region, ActivityData (polymorphic: Transport or Stationary)

### Fuel
**Purpose:** Fuel database with emission properties

**Fields:**
- `fuelName`, `fuelCategory`, `emissionBasis`
- `heatingValue`, `density`
- `fossilCO2EmissionFactor`, `bioCO2EmissionFactor`
- `CH4EmissionFactor`, `N2OEmissionFactor`

**Checksum:** SHA-256 for duplicate detection

### Region
**Purpose:** Geographic tracking

**Fields:**
- `regionName`, `regionCode`
- Used for region-specific emission factors

### Emission Factors

**Transport Emission Factors:**
- `TransportFuelEmissionFactors`: Fuel-based calculations
- `TransportVehicleDataEmissionFactors`: Vehicle-based calculations

**Stationary Emission Factors:**
- Stored within Fuel entities
- Region and fuel-type specific

## Validation & Error Handling

**Location:** `/exceptions/GlobalExceptionHandler.java`

**Features:**
- 23+ exception handlers with user-friendly messages
- Security-hardened (no internal details exposed)
- Proper HTTP status codes (409 for duplicates, 401 for auth, 403 for forbidden)
- SLF4J logging infrastructure
- Special handling for duplicate year entries
- JWT filter exception handling

**Validation Patterns:**
- `@Valid` annotations on all controller POST/PUT methods
- Validation annotations on DTOs (@NotNull, @Min, @Max, etc.)
- Year validation in `AgricultureAbstractClass` (inherited by all agriculture entities)

**User-Friendly Messages:**
```
"A record for this year already exists. Please use a different year or update the existing record."
"Year is required"
"Fish production cannot be negative"
"JWT token has expired"
"You don't have permission to access this resource"
```

## API Response Format

**Wrapper:** `ResponseWrapper<T>` utility class

**Success Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... }
}
```

**Error Response:**
```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

## Authentication & Security

**Method:** JWT (JSON Web Token) based authentication

**Security Configuration:**
- Spring Security with custom JWT filter
- Bearer token authentication
- Role-based access control (if implemented)

**Swagger Security:**
```java
@SecurityRequirement(name = "BearerAuth")
```

**Token Flow:**
1. User authenticates → receives JWT token
2. Client includes token in Authorization header: `Bearer {token}`
3. JWT filter validates token on each request
4. Expired tokens return 401 Unauthorized

## Database Schema Concepts

### Uniqueness Constraints

**Year Uniqueness (Simple):**
- Most agriculture emissions: One record per year
- Example: `UreaEmissions`, `SyntheticFertilizerEmissions`

**Composite Uniqueness:**
- Wetland Parks: (year + treeCategory)
- Protective Forest: (year + category)
- Allows multiple records per year for different categories

**ID-based (No year constraint):**
- Activities: Multiple activities per year allowed
- Mitigation projects with installation data: Tracked by stove type and year separately

### Abstract Base Classes

**AgricultureAbstractClass:**
```java
@MappedSuperclass
protected abstract class AgricultureAbstractClass {
    private Year year;
    // Shared year validation logic
}
```

**WasteDataAbstract:**
```java
@MappedSuperclass
protected abstract class WasteDataAbstract {
    private Year year;
    // Shared waste data fields
}
```

### Relationships

**Many-to-One:**
- Activity → Fuel
- Activity → Region
- StoveMitigationYear → StoveType
- TransportActivityData → Activity

**One-to-One:**
- Activity ↔ ActivityData (polymorphic: Transport or Stationary)

## Calculation Methodologies

### IPCC Compliance

The system follows **IPCC (Intergovernmental Panel on Climate Change)** methodologies for GHG calculations:

**Tier Levels:**
- Tier 1: Default emission factors
- Tier 2: Country-specific emission factors
- Tier 3: Model-based calculations (where applicable)

### Agriculture Calculations

**Enteric Fermentation (CH₄):**
```
CH₄ = (GE / 55.65) × (EF / 100) × populationCount
GE (Gross Energy) = species-specific formula
EF (Emission Factor) = species-specific value
```

**Manure Management (CH₄ & N₂O):**
```
CH₄ = VS × Bo × 0.67 × MCF × populationCount / 1000
VS (Volatile Solids) = species-specific
Bo = 0.24 m³ CH₄/kg VS
MCF = Management system specific

Direct N₂O = Nex × MS% × EF(direct) × 44/28
Indirect N₂O (volatilization) = Nex × MS% × Frac(gasms) × EF(indirect) × 44/28
Indirect N₂O (leaching) = Nex × MS% × Frac(leach) × EF(leach) × 44/28
```

**Rice Cultivation (CH₄):**
```
Annual CH₄ = Daily Emissions × Cultivation Period × Scaling Factor
Daily Emissions = EF × Area × Organic Amendment Factor × Water Regime Factor
```

**Synthetic Fertilizer (N₂O):**
```
N₂O-N = Qty × EF × Frac(leach) × EF(leach)
N₂O = N₂O-N × 44/28
```

**Urea (CO₂):**
```
CO₂ = Qty × 0.20 × 44/12
```

### Mitigation Calculations

**Biomass-Based (Trees, Wetlands):**
```
AGB Growth = Current AGB - Previous AGB
Aboveground Biomass = AGB Growth × 0.66 (m³ to tonnes DM)
Total Biomass = Aboveground × (1 + BGB_ratio)  [0.22 or 0.27]
Carbon = Total Biomass × 0.47
CO₂e Sequestered = Carbon × 3.6666667 (44/12 ratio) / 1000
```

**Waste Mitigation:**
```
Avoided Emissions = Waste Diverted × Emission Factor
Net Reduction = Avoided - Process Emissions (if any)
```

**Soil Carbon (Zero Tillage):**
```
Carbon Increase = Area × 0.37 tonnes C/ha
Gross Savings = Carbon × 3.6666667 / 1000
Urea Offset = (Area × 0.1 × 0.2) / 1000
NET Savings = Gross Savings - Urea Offset
```

## Common Patterns

### Repository Pattern
```java
public interface XRepository extends JpaRepository<X, UUID>, JpaSpecificationExecutor<X> {
    Optional<X> findByYear(int year);
    List<X> findByYearBetween(int start, int end);
}
```

### Service Pattern
```java
@Service
public class XServiceImpl implements XService {
    private final XRepository repository;
    
    @Transactional
    public X create(XDto dto) {
        // Validation
        // Calculation
        // Save
    }
    
    public List<X> findAll(Specification<X> spec) {
        return repository.findAll(spec);
    }
}
```

### Controller Pattern
```java
@RestController
@RequestMapping("/api/path")
@SecurityRequirement(name = "BearerAuth")
public class XController {
    
    @PostMapping
    public ResponseEntity<X> create(@Valid @RequestBody XDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }
    
    @GetMapping
    public ResponseEntity<List<X>> getAll(
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) Category category
    ) {
        Specification<X> spec = buildSpec(year, category);
        return ResponseEntity.ok(service.findAll(spec));
    }
}
```

### Specification Pattern
```java
public class XSpecifications {
    public static Specification<X> hasYear(int year) {
        return (root, query, cb) -> cb.equal(root.get("year"), year);
    }
    
    public static Specification<X> hasCategory(Category cat) {
        return (root, query, cb) -> cb.equal(root.get("category"), cat);
    }
}
```

## Development Guidelines

### Adding New Emissions Module

1. Create entity extending appropriate abstract class
2. Create DTO with validation annotations
3. Create repository with `JpaRepository` + `JpaSpecificationExecutor`
4. Create service interface and implementation with calculation logic
5. Create controller with CRUD endpoints
6. Add specification class for filtering
7. Update dashboard aggregation (if applicable)
8. Document calculation methodology

### Adding New Mitigation Project

1. Create constants enum (if needed)
2. Create entity with year constraint
3. Create DTO with `@NotNull` annotations
4. Create repository with `findByYear()` method
5. Create service with auto-calculation logic
6. Create controller following standard pattern
7. Add to `MitigationDashboardServiceImpl` aggregation
8. Implement unit conversion (if applicable)
9. Update total project count in documentation

### Adding Unit Conversion

1. Create or update appropriate enum in `/Enums/Metrics/`
2. Add unit field to DTO with `@NotNull`
3. Convert to standard unit in service implementation before calculations
4. Store converted value in database
5. Follow pattern from existing conversions

## Testing Endpoints

### Authentication
```bash
# Login (example - adjust to actual auth endpoint)
POST /auth/login
Body: { "username": "...", "password": "..." }
Response: { "token": "eyJ..." }

# Use token in subsequent requests
Header: Authorization: Bearer eyJ...
```

### Create Emissions Record
```bash
POST /agriculture/syntheticFertilizerEmissions
Header: Authorization: Bearer {token}
Body: {
  "year": 2024,
  "fertilizerName": "Urea",
  "qty": 1000.5,
  "unit": "KG"
}
```

### Query with Filters
```bash
GET /agriculture/entericFermentationEmissions?year=2024&species=DAIRY_CATTLE
Header: Authorization: Bearer {token}
```

### Dashboard Access
```bash
GET /activities/stats/dashboard?startingYear=2020&endingYear=2024
GET /mitigation/dashboard/summary?startingYear=2020&endingYear=2024
```

## Key Enums Reference

### GWP (Global Warming Potential)
```java
CO2(1.0), CH4(25.0), N2O(298.0)
```

### Livestock Types (Enteric Fermentation)
```java
DAIRY_CATTLE, NON_DAIRY_CATTLE, BUFFALO, SWINE, SHEEP, GOATS, 
CAMELS, HORSES, MULES_ASSES, POULTRY_CHICKEN, POULTRY_DUCKS, 
POULTRY_TURKEYS, POULTRY_GEESE, POULTRY_OTHER
```

### Manure Management Systems
```java
PASTURE_RANGE_PADDOCK, DAILY_SPREAD, SOLID_STORAGE, DRY_LOT,
LIQUID_SLURRY, ANAEROBIC_LAGOON, ANAEROBIC_DIGESTER,
COMPOSTING_INTENSIVE, COMPOSTING_STATIC_PILE, DEEP_BEDDING,
POULTRY_MANURE_WITH_LITTER, POULTRY_MANURE_WITHOUT_LITTER,
BURNED_FOR_FUEL
```

### Rice Cultivation
```java
Ecosystems: IRRIGATED, RAIN_FED_UPLAND, DEEP_WATER, SHALLOW_WATER
Water Regimes: CONTINUOUSLY_FLOODED, INTERMITTENTLY_FLOODED, RAIN_FED
Cultivation Types: TRANSPLANTED, DIRECT_SEEDED
```

### Lime Types
```java
CALCITIC_LIMESTONE, DOLOMITE_LIMESTONE, DOLOMITE, MARL
```

### Transport Modes
```java
ROAD, RAIL, AIR, SEA, PIPELINE, OFFROAD, OTHER
```

### Unit Types
```java
AreaUnits: HECTARE, SQUARE_METER, ACRE, SQUARE_KILOMETER
VolumeUnits: CUBIC_METER, LITER, GALLON, BARREL
MassUnits: KILOGRAM, TONNE, POUND, TON
```

## Important Notes for AI Understanding

1. **Calculated Fields:** Never included in DTOs - computed by service layer
2. **Year Validation:** Most modules use `AgricultureAbstractClass` for consistent year validation
3. **Enum Isolation:** Each module has unique enums to prevent option cross-contamination
4. **Auto-Fetch Pattern:** Some projects (Wetland Parks, Protective Forest) auto-fetch previous year data
5. **Composite Keys:** Some entities use (year + category) for uniqueness
6. **Dashboard Integration:** New modules MUST be added to dashboard aggregation methods
7. **Unit Conversion:** Always convert to standard units before calculations, store standard units
8. **Transactional Methods:** Use `@Transactional` for create/update/delete operations
9. **Specification Pattern:** Use for dynamic filtering instead of hardcoded queries
10. **Error Messages:** Keep user-friendly, never expose internal details

## Future Considerations

- [ ] Land use emissions integration into main dashboard
- [ ] Scope 2 and Scope 3 emissions tracking expansion
- [ ] Advanced reporting and analytics
- [ ] Export to international formats (GHG Protocol, CDP)
- [ ] Multi-tenancy support
- [ ] Advanced role-based access control
- [ ] Real-time monitoring and alerts
- [ ] Machine learning for emissions forecasting

## License

[Add appropriate license information]

## Contact

[Add contact information for project maintainers]
