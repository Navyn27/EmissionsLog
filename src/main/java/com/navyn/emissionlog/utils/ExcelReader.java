package com.navyn.emissionlog.utils;

import com.navyn.emissionlog.Enums.ExcelType;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

public class ExcelReader {

    // This hashmap is responsible for reading data from stationary emissions Excel
    // files and mapping it to DTOs.
    private static final Map<String, String> stationaryEmissionsToDtoMap = new HashMap<>();
    static {
        stationaryEmissionsToDtoMap.put("Fuel Type", "fuelType");
        stationaryEmissionsToDtoMap.put("Fuel", "fuel");
        stationaryEmissionsToDtoMap.put("Description", "fuelDescription");
        stationaryEmissionsToDtoMap.put("Lower Heating Value (LHV) (or NCV)", "lowerHeatingValue");
        stationaryEmissionsToDtoMap.put("Emission", "emission");
        stationaryEmissionsToDtoMap.put("Energy basis", "energyBasis");
        stationaryEmissionsToDtoMap.put("Mass basis", "massBasis");
        stationaryEmissionsToDtoMap.put("Fuel density of Liquids", "fuelDensityLiquids");
        stationaryEmissionsToDtoMap.put("Fuel density of Gases", "fuelDensityGases");
        stationaryEmissionsToDtoMap.put("Liquid basis", "liquidBasis");
        stationaryEmissionsToDtoMap.put("Gas basis", "gasBasis");
    }

    // This hashmap is responsible for reading data from transport emissions by fuel
    // Excel files and mapping it to DTOs.
    private static final Map<String, String> transportEmissionsByFuelDtoMap = new HashMap<>();
    static {
        transportEmissionsByFuelDtoMap.put("Region Group", "regionGroup");
        transportEmissionsByFuelDtoMap.put("Fuel", "fuel");
        transportEmissionsByFuelDtoMap.put("Fuel Type", "fuelType");
        transportEmissionsByFuelDtoMap.put("Fossil CO2 EF", "fossilCO2EmissionFactor");
        transportEmissionsByFuelDtoMap.put("Biogenic CO2 EF", "biogenicCO2EmissionFactor");
        transportEmissionsByFuelDtoMap.put("Transport Type", "transportType");
        transportEmissionsByFuelDtoMap.put("Vehicle/Engine Type", "vehicleEngineType");
        transportEmissionsByFuelDtoMap.put("CH4 EF", "CH4EmissionFactor");
        transportEmissionsByFuelDtoMap.put("N2O EF", "N2OEmissionFactor");
        transportEmissionsByFuelDtoMap.put("Basis", "basis");
    }

    // This hashmap is responsible for reading data from transport emissions by
    // vehicle data Excel files and mapping it to DTOs.
    private static final Map<String, String> transportEmissionsByVehicleDataDtoMap = new HashMap<>();
    static {
        transportEmissionsByVehicleDataDtoMap.put("Region", "regionGroup");
        transportEmissionsByVehicleDataDtoMap.put("Vehicle", "vehicle");
        transportEmissionsByVehicleDataDtoMap.put("Size", "size");
        transportEmissionsByVehicleDataDtoMap.put("Weight Laden", "weightLaden");
        transportEmissionsByVehicleDataDtoMap.put("Vehicle Year", "vehicleYear");
        transportEmissionsByVehicleDataDtoMap.put("Fuel", "fuel");
        transportEmissionsByVehicleDataDtoMap.put("Fuel Type", "fuelType");
        transportEmissionsByVehicleDataDtoMap.put("CO2 EF", "CO2EmissionFactor");
        transportEmissionsByVehicleDataDtoMap.put("CH4 EF", "CH4EmissionFactor");
        transportEmissionsByVehicleDataDtoMap.put("N2O EF", "N2OEmissionFactor");
        transportEmissionsByVehicleDataDtoMap.put("Basis", "basis");
    }

    // This hashmap is responsible for reading data from population records Excel
    // files and mapping it to DTOs.
    private static final Map<String, String> populationRecordsToDtoMap = new HashMap<>();
    static {
        populationRecordsToDtoMap.put("Year", "year");
        populationRecordsToDtoMap.put("Kigali Population", "population");
        populationRecordsToDtoMap.put("Kigali Population Annual Growth", "kigaliAnnualGrowth");
        populationRecordsToDtoMap.put("Growth", "annualGrowth");
        populationRecordsToDtoMap.put("Number of HHs", "numberOfKigaliHouseholds");
        populationRecordsToDtoMap.put("GDP Millions", "GDPMillions");
        populationRecordsToDtoMap.put("Per Capita", "GDPPerCapita");
        populationRecordsToDtoMap.put("Estimated Kigali GDP", "kigaliGDP");
    }

    // This hashmap is responsible for reading data from EICV reports Excel files
    // and mapping it to DTOs.
    private static final Map<String, String> eicvReportsToDtoMap = new HashMap<>();
    static {
        eicvReportsToDtoMap.put("Name", "name");
        eicvReportsToDtoMap.put("Year", "year");
        eicvReportsToDtoMap.put("Total Improved Sanitation", "totalImprovedSanitation");
        eicvReportsToDtoMap.put("Improved Type Not Shared With Other HH", "improvedTypeNotSharedWithOtherHH");
        eicvReportsToDtoMap.put("Flush Toilet", "flushToilet");
        eicvReportsToDtoMap.put("Protected Latrines", "protectedLatrines");
        eicvReportsToDtoMap.put("Unprotected Latrines", "unprotectedLatrines");
        eicvReportsToDtoMap.put("Others", "others");
        eicvReportsToDtoMap.put("No Toilet Facilities", "noToiletFacilities");
        eicvReportsToDtoMap.put("Total Households", "totalHouseholds");
    }

    private static final Map<String, String> solidWasteDtoMap = new HashMap<>();
    static {
        solidWasteDtoMap.put("Year", "year");
        solidWasteDtoMap.put("Food Deposited Amount", "foodDepositedAmount");
        solidWasteDtoMap.put("Garden Deposited Amount", "gardenDepositedAmount");
        solidWasteDtoMap.put("Paper Deposited Amount", "paperDepositedAmount");
        solidWasteDtoMap.put("Wood Deposited Amount", "woodDepositedAmount");
        solidWasteDtoMap.put("Textiles Deposited Amount", "textilesDepositedAmount");
        solidWasteDtoMap.put("Nappies Deposited Amount", "nappiesDepositedAmount");
        solidWasteDtoMap.put("Sludge Deposited Amount", "sludgeDepositedAmount");
        solidWasteDtoMap.put("MSW Deposited Amount", "mswDepositedAmount");
        solidWasteDtoMap.put("Industry Deposited Amount", "industryDepositedAmount");
    }

    private static final Map<String, String> industrialWasteDtoMap = new HashMap<>();
    static {
        industrialWasteDtoMap.put("Year", "year");
        industrialWasteDtoMap.put("Sugar Production Amount", "sugarProductionAmount");
        industrialWasteDtoMap.put("Beer Production Amount", "beerProductionAmount");
        industrialWasteDtoMap.put("Dairy Production Amount", "dairyProductionAmount");
        industrialWasteDtoMap.put("Meat And Poultry Production Amount", "meatAndPoultryProductionAmount");
    }

    // This hashmap is responsible for reading data from zero tillage mitigation
    // Excel files
    // and mapping it to DTOs.
    private static final Map<String, String> zeroTillageToDtoMap = new HashMap<>();
    static {
        zeroTillageToDtoMap.put("Year", "year");
        zeroTillageToDtoMap.put("Area Under Zero Tillage", "areaUnderZeroTillage");
        zeroTillageToDtoMap.put("Area Unit", "areaUnit");
        zeroTillageToDtoMap.put("Urea Applied", "ureaApplied");
        zeroTillageToDtoMap.put("Intervention", "interventionName");
    }

    // This hashmap is responsible for reading data from wetland parks mitigation
    // Excel files
    // and mapping it to DTOs.
    private static final Map<String, String> wetlandParksToDtoMap = new HashMap<>();
    static {
        wetlandParksToDtoMap.put("Year", "year");
        wetlandParksToDtoMap.put("Tree Category", "treeCategory");
        wetlandParksToDtoMap.put("Area Planted", "areaPlanted");
        wetlandParksToDtoMap.put("AGB Current Year", "abovegroundBiomassAGB");
        wetlandParksToDtoMap.put("AGB Unit", "agbUnit");
        wetlandParksToDtoMap.put("Intervention Name", "interventionName");
    }

    // This hashmap is responsible for reading data from crop rotation mitigation
    // Excel files
    // and mapping it to DTOs.
    private static final Map<String, String> cropRotationToDtoMap = new HashMap<>();
    static {
        cropRotationToDtoMap.put("Year", "year");
        cropRotationToDtoMap.put("Cropland Under Crop Rotation", "croplandUnderCropRotation");
        cropRotationToDtoMap.put("Cropland Area Unit", "croplandAreaUnit");
        cropRotationToDtoMap.put("Increased Biomass", "increasedBiomass");
        cropRotationToDtoMap.put("Intervention", "interventionName");
    }

    // This hashmap is responsible for reading data from street trees mitigation
    // Excel files
    // and mapping it to DTOs.
    private static final Map<String, String> streetTreesToDtoMap = new HashMap<>();
    static {
        streetTreesToDtoMap.put("Year", "year");
        streetTreesToDtoMap.put("Number of Trees Planted", "numberOfTreesPlanted");
        streetTreesToDtoMap.put("AGB Single Tree Current Year", "agbSingleTreeCurrentYear");
        streetTreesToDtoMap.put("AGB Unit", "agbUnit");
        streetTreesToDtoMap.put("Intervention", "interventionName");
    }

    // This hashmap is responsible for reading data from settlement trees mitigation
    // Excel files
    // and mapping it to DTOs.
    private static final Map<String, String> settlementTreesToDtoMap = new HashMap<>();
    static {
        settlementTreesToDtoMap.put("Year", "year");
        settlementTreesToDtoMap.put("Number of Trees Planted", "numberOfTreesPlanted");
        settlementTreesToDtoMap.put("AGB Single Tree Current Year", "agbSingleTreeCurrentYear");
        settlementTreesToDtoMap.put("AGB Unit", "agbUnit");
    }

    // This hashmap is responsible for reading data from daily spread mitigation
    // Excel files and mapping it to DTOs.
    private static final Map<String, String> dailySpreadToDtoMap = new HashMap<>();
    static {
        dailySpreadToDtoMap.put("Year", "year");
        dailySpreadToDtoMap.put("Number of Cows", "numberOfCows");
    }

    // This hashmap is responsible for reading data from adding straw mitigation
    // Excel files and mapping it to DTOs.
    private static final Map<String, String> addingStrawToDtoMap = new HashMap<>();
    static {
        addingStrawToDtoMap.put("Year", "year");
        addingStrawToDtoMap.put("Number of Cows", "numberOfCows");
    }

    // This hashmap is responsible for reading data from manure covering mitigation
    // Excel files and mapping it to DTOs.
    private static final Map<String, String> manureCoveringToDtoMap = new HashMap<>();
    static {
        manureCoveringToDtoMap.put("Year", "year");
        manureCoveringToDtoMap.put("Number of Cows", "numberOfCows");
    }

    // This hashmap is responsible for reading data from protective forest
    // mitigation
    // Excel files and mapping it to DTOs.
    private static final Map<String, String> protectiveForestToDtoMap = new HashMap<>();
    static {
        protectiveForestToDtoMap.put("Year", "year");
        protectiveForestToDtoMap.put("Category", "category");
        protectiveForestToDtoMap.put("Area Planted", "areaPlanted");
        protectiveForestToDtoMap.put("AGB Current Year", "agbCurrentYear");
        protectiveForestToDtoMap.put("AGB Unit", "agbUnit");
        protectiveForestToDtoMap.put("Intervention Name", "interventionName");
    }

    // This hashmap is responsible for reading data from green fences mitigation
    // Excel files and mapping it to DTOs.
    private static final Map<String, String> greenFencesToDtoMap = new HashMap<>();
    static {
        greenFencesToDtoMap.put("Year", "year");
        greenFencesToDtoMap.put("Number of Households with 10m2 Fence", "numberOfHouseholdsWith10m2Fence");
        greenFencesToDtoMap.put("AGB of 10m2 Live Fence", "agbOf10m2LiveFence");
        greenFencesToDtoMap.put("AGB Unit", "agbUnit");
        greenFencesToDtoMap.put("Intervention", "interventionName");
    }

    // This hashmap is responsible for reading data from waste to energy mitigation
    // Excel files and mapping it to DTOs.
    private static final Map<String, String> wasteToEnergyToDtoMap = new HashMap<>();
    static {
        wasteToEnergyToDtoMap.put("Year", "year");
        wasteToEnergyToDtoMap.put("Waste to WtE", "wasteToWtE");
        wasteToEnergyToDtoMap.put("Waste to WtE Unit", "wasteToWtEUnit");
        wasteToEnergyToDtoMap.put("BAU Emissions Solid Waste", "bauEmissionsSolidWaste");
        wasteToEnergyToDtoMap.put("BAU Emissions Unit", "bauEmissionsUnit");
    }

    // This hashmap is responsible for reading data from landfill gas utilization
    // mitigation
    // Excel files and mapping it to DTOs.
    private static final Map<String, String> landfillGasUtilizationToDtoMap = new HashMap<>();
    static {
        landfillGasUtilizationToDtoMap.put("Year", "year");
        landfillGasUtilizationToDtoMap.put("BAU Solid Waste Emissions", "bauSolidWasteEmissions");
        landfillGasUtilizationToDtoMap.put("BAU Solid Waste Emissions Unit", "bauSolidWasteEmissionsUnit");
        landfillGasUtilizationToDtoMap.put("Project Reduction (40% Efficiency)", "projectReduction40PercentEfficiency");
        landfillGasUtilizationToDtoMap.put("Project Reduction Unit", "projectReductionUnit");
        landfillGasUtilizationToDtoMap.put("BAU Grand Total", "bauGrandTotal");
        landfillGasUtilizationToDtoMap.put("BAU Grand Total Unit", "bauGrandTotalUnit");
    }

    // This hashmap is responsible for reading data from MBT composting mitigation
    // Excel files and mapping it to DTOs.
    private static final Map<String, String> mbtCompostingToDtoMap = new HashMap<>();
    static {
        mbtCompostingToDtoMap.put("Year", "year");
        mbtCompostingToDtoMap.put("Operation Status", "operationStatus");
        mbtCompostingToDtoMap.put("Organic Waste Treated Tons Per Day", "organicWasteTreatedTonsPerDay");
        mbtCompostingToDtoMap.put("Organic Waste Treated Unit", "organicWasteTreatedUnit");
        mbtCompostingToDtoMap.put("BAU Emission Biological Treatment", "bauEmissionBiologicalTreatment");
        mbtCompostingToDtoMap.put("BAU Emission Unit", "bauEmissionUnit");
    }

    // This hashmap is responsible for reading data from EPR plastic waste
    // mitigation
    // Excel files and mapping it to DTOs.
    private static final Map<String, String> eprPlasticWasteToDtoMap = new HashMap<>();
    static {
        eprPlasticWasteToDtoMap.put("Year", "year");
        eprPlasticWasteToDtoMap.put("BAU Solid Waste Emissions", "bauSolidWasteEmissions");
        eprPlasticWasteToDtoMap.put("BAU Solid Waste Emissions Unit", "bauSolidWasteEmissionsUnit");
        eprPlasticWasteToDtoMap.put("Plastic Waste Growth Factor", "plasticWasteGrowthFactor");
        eprPlasticWasteToDtoMap.put("Recycling Rate With EPR", "recyclingRateWithEPR");
        eprPlasticWasteToDtoMap.put("Plastic Waste Base Tonnes Per Year", "plasticWasteBaseTonnesPerYear");
    }

    // This hashmap is responsible for reading data from Kigali FSTP mitigation
    // Excel files and mapping it to DTOs.
    private static final Map<String, String> kigaliFSTPToDtoMap = new HashMap<>();
    static {
        kigaliFSTPToDtoMap.put("Year", "year");
        kigaliFSTPToDtoMap.put("Project Phase", "projectPhase");
        kigaliFSTPToDtoMap.put("Phase Capacity Per Day", "phaseCapacityPerDay");
        kigaliFSTPToDtoMap.put("Phase Capacity Unit", "phaseCapacityUnit");
        kigaliFSTPToDtoMap.put("Plant Operational Efficiency", "plantOperationalEfficiency");
    }

    // This hashmap is responsible for reading data from Kigali WWTP mitigation
    // Excel files and mapping it to DTOs.
    private static final Map<String, String> kigaliWWTPToDtoMap = new HashMap<>();
    static {
        kigaliWWTPToDtoMap.put("Year", "year");
        kigaliWWTPToDtoMap.put("Project Phase", "projectPhase");
        kigaliWWTPToDtoMap.put("Phase Capacity Per Day", "phaseCapacityPerDay");
        kigaliWWTPToDtoMap.put("Phase Capacity Unit", "phaseCapacityUnit");
        kigaliWWTPToDtoMap.put("Connected Households", "connectedHouseholds");
        kigaliWWTPToDtoMap.put("Connected Households Percentage", "connectedHouseholdsPercentage");
    }

    // This hashmap is responsible for reading data from ISWM mitigation
    // Excel files and mapping it to DTOs.
    private static final Map<String, String> iswmToDtoMap = new HashMap<>();
    static {
        iswmToDtoMap.put("Year", "year");
        iswmToDtoMap.put("Waste Processed", "wasteProcessed");
        iswmToDtoMap.put("Degradable Organic Fraction", "degradableOrganicFraction");
        iswmToDtoMap.put("Landfill Avoidance", "landfillAvoidance");
        iswmToDtoMap.put("Composting Emission Factor", "compostingEF");
        iswmToDtoMap.put("BAU Emission", "bauEmission");
        iswmToDtoMap.put("BAU Emission Unit", "bauEmissionUnit");
    }

    // This hashmap is responsible for reading data from IPPU mitigation
    // Excel files and mapping it to DTOs.
    private static final Map<String, String> ippuToDtoMap = new HashMap<>();
    static {
        ippuToDtoMap.put("Year", "year");
        ippuToDtoMap.put("BAU", "bau");
        ippuToDtoMap.put("F-Gas Name", "fGasName");
        ippuToDtoMap.put("Amount of Avoided F-Gas", "amountOfAvoidedFGas");
        ippuToDtoMap.put("GWP Factor", "gwpFactor");
    }

    // This hashmap is responsible for reading data from cookstove mitigation
    // Excel files and mapping it to DTOs.
    private static final Map<String, String> cookstoveToDtoMap = new HashMap<>();
    static {
        cookstoveToDtoMap.put("Year", "year");
        cookstoveToDtoMap.put("Stove Type Name", "stoveTypeName");
        cookstoveToDtoMap.put("Baseline Percentage", "baselinePercentage");
        cookstoveToDtoMap.put("Units Installed This Year", "unitsInstalledThisYear");
        cookstoveToDtoMap.put("BAU", "bau");
    }

    // This hashmap is responsible for reading data from light bulb mitigation
    // Excel files and mapping it to DTOs.
    private static final Map<String, String> lightBulbToDtoMap = new HashMap<>();
    static {
        lightBulbToDtoMap.put("Year", "year");
        lightBulbToDtoMap.put("Total Installed Bulbs Per Year", "totalInstalledBulbsPerYear");
        lightBulbToDtoMap.put("Reduction Capacity Per Bulb", "reductionCapacityPerBulb");
        lightBulbToDtoMap.put("Emission Factor", "emissionFactor");
        lightBulbToDtoMap.put("BAU", "bau");
    }

    public static <T> List<T> readExcel(InputStream inputStream, Class<T> dtoClass, ExcelType excelType)
            throws IOException {
        List<T> result = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            String expectedSheetName = findSheetName(excelType);
            Sheet sheet = workbook.getSheet(expectedSheetName);

            // If exact match not found, try to find sheet by partial name match (handles
            // Excel's 31-char truncation)
            if (sheet == null) {
                // Try to find sheet by partial match (for Excel's 31-character sheet name
                // limit)
                String sheetNamePrefix = expectedSheetName.length() > 31
                        ? expectedSheetName.substring(0, 31)
                        : expectedSheetName;

                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                    String actualSheetName = workbook.getSheetName(i);
                    if (actualSheetName.equals(expectedSheetName) ||
                            actualSheetName.startsWith(sheetNamePrefix) ||
                            expectedSheetName.startsWith(actualSheetName) ||
                            actualSheetName.startsWith(expectedSheetName)) {
                        sheet = workbook.getSheetAt(i);
                        break;
                    }
                }
            }

            if (sheet == null) {
                // List available sheets for better error message
                StringBuilder availableSheets = new StringBuilder();
                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                    if (i > 0)
                        availableSheets.append(", ");
                    availableSheets.append("'").append(workbook.getSheetName(i)).append("'");
                }
                throw new IOException("Template format error: Sheet '" + expectedSheetName
                        + "' not found. Available sheets: " + availableSheets.toString()
                        + ". Please download the correct template and use it without modifying the sheet name.");
            }

            // Determine header row index based on ExcelType
            int headerRowIndex = getHeaderRowIndex(excelType);
            int firstDataRowIndex = headerRowIndex + 1;

            // Get header row
            Row headerRow = sheet.getRow(headerRowIndex);
            if (headerRow == null) {
                throw new IOException("Template format error: Header row not found at row " + (headerRowIndex + 1)
                        + ". Please download the correct template and do not modify the header row.");
            }

            List<String> headers = new ArrayList<>();
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell != null) {
                    headers.add(cell.getStringCellValue().trim());
                } else {
                    headers.add("");
                }
            }

            // Read data rows starting from firstDataRowIndex
            for (int rowIndex = firstDataRowIndex; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null || isRowEmpty(row))
                    continue;

                T dto = dtoClass.getDeclaredConstructor().newInstance();
                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = row.getCell(i);
                    if (cell == null)
                        continue;

                    String header = headers.get(i);
                    if (header == null || header.trim().isEmpty())
                        continue;

                    String fieldName = findHeaderInSheet(excelType, header);

                    if (fieldName != null) {
                        Field field = dtoClass.getDeclaredField(fieldName);
                        if (field != null) {
                            field.setAccessible(true);
                            setFieldValue(dto, field, cell);
                        }
                    }
                }
                result.add(dto);
            }
        } catch (IllegalArgumentException e) {
            // This is usually an enum validation error or type mismatch
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("Invalid value")) {
                throw new IOException(errorMsg, e);
            } else if (errorMsg != null && errorMsg.contains("Cell type")) {
                throw new IOException("Incorrect data type. Please check your Excel file.", e);
            } else {
                throw new IOException("Invalid data format. Please check your Excel file and try again.", e);
            }
        } catch (ReflectiveOperationException e) {
            throw new IOException("Incorrect template. Please download the correct template and try again.", e);
        }
        return result;
    }

    /**
     * Returns the row index where headers are located for the given ExcelType.
     * For most sheets, headers are at row 0, but some templates have title rows
     * before headers.
     */
    private static int getHeaderRowIndex(ExcelType excelType) {
        switch (excelType) {
            case EICV_REPORT:
                // EICV template has: Row 0 = Title, Row 1 = Blank, Row 2 = Headers
                return 2;
            case CROP_ROTATION_MITIGATION:
                // Crop Rotation template has: Row 0 = Title, Row 1 = Blank, Row 2 = Headers
                return 2;
            case DAILY_SPREAD_MITIGATION:
                // Daily Spread template has: Row 0 = Title, Row 1 = Blank, Row 2 = Headers
                return 2;
            case ADDING_STRAW_MITIGATION:
                // Adding Straw template has: Row 0 = Title, Row 1 = Blank, Row 2 = Headers
                return 2;
            case MANURE_COVERING_MITIGATION:
                // Manure Covering template has: Row 0 = Title, Row 1 = Blank, Row 2 = Headers
                return 2;
            case ZERO_TILLAGE_MITIGATION:
                // Zero Tillage template has: Row 0 = Title, Row 1 = Blank, Row 2 = Headers
                return 2;
            case WETLAND_PARKS_MITIGATION:
                // Wetland Parks template has: Row 0 = Title, Row 1 = Blank, Row 2 = Headers
                return 2;
            case STREET_TREES_MITIGATION:
                // Street Trees template has: Row 0 = Title, Row 1 = Blank, Row 2 = Headers
                return 2;
            case SETTLEMENT_TREES_MITIGATION:
                // Settlement Trees template has: Row 0 = Title, Row 1 = Blank, Row 2 = Headers
                return 2;
            case PROTECTIVE_FOREST_MITIGATION:
                // Protective Forest template has: Row 0 = Title, Row 1 = Blank, Row 2 = Headers
                return 2;
            case GREEN_FENCES_MITIGATION:
                // Green Fences template has: Row 0 = Title, Row 1 = Blank, Row 2 = Headers
                return 2;
            case WASTE_TO_ENERGY_MITIGATION:
            case LANDFILL_GAS_UTILIZATION_MITIGATION:
            case MBT_COMPOSTING_MITIGATION:
            case EPR_PLASTIC_WASTE_MITIGATION:
            case KIGALI_FSTP_MITIGATION:
            case KIGALI_WWTP_MITIGATION:
            case ISWM_MITIGATION:
            case IPPU_MITIGATION:
            case COOKSTOVE_MITIGATION:
            case LIGHT_BULB_MITIGATION:
                // All waste mitigation templates, IPPU, Cookstove and LightBulb have: Row 0 = Title, Row 1
                // = Blank,
                // Row 2 = Headers
                return 2;
            default:
                // Other templates have headers at row 0
                return 0;
        }
    }

    private static <T> void setFieldValue(T dto, Field field, Cell cell) throws IllegalAccessException {
        try {
            // Handle formula cells by evaluating them
            CellType cellType = cell.getCellType();
            if (cellType == CellType.FORMULA) {
                cellType = cell.getCachedFormulaResultType();
            }

            switch (field.getType().getSimpleName()) {
                case "String":
                    if (cellType == CellType.STRING) {
                        field.set(dto, cell.getStringCellValue().trim());
                    } else if (cellType == CellType.BLANK) {
                        break;
                    } else if (cellType == CellType.NUMERIC) {
                        // Convert numeric to string, handling integers without decimal
                        double numValue = cell.getNumericCellValue();
                        if (numValue == (int) numValue) {
                            field.set(dto, String.valueOf((int) numValue));
                        } else {
                            field.set(dto, String.valueOf(numValue));
                        }
                    } else {
                        field.set(dto, String.valueOf(getCellValueAsString(cell)));
                    }
                    break;
                case "Integer":
                case "int": // Handle both Integer wrapper and primitive int
                    if (cellType == CellType.NUMERIC) {
                        int intValue = (int) cell.getNumericCellValue();
                        field.set(dto, intValue); // Autoboxing handles both Integer and int
                    } else if (cellType == CellType.BLANK) {
                        break;
                    } else if (cellType == CellType.STRING) {
                        // Try to parse string as integer
                        try {
                            String stringValue = cell.getStringCellValue().trim();
                            int intValue = Integer.parseInt(stringValue);
                            field.set(dto, intValue); // Autoboxing handles both Integer and int
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Cannot convert '" + cell.getStringCellValue()
                                    + "' to Integer for field " + field.getName());
                        }
                    } else {
                        throw new IllegalArgumentException(
                                "Cell type is not numeric or string for Integer field " + field.getName());
                    }
                    break;
                case "BigDecimal":
                    if (cellType == CellType.NUMERIC) {
                        field.set(dto, BigDecimal.valueOf(cell.getNumericCellValue()));
                    } else if (cellType == CellType.BLANK) {
                        break;
                    } else if (cellType == CellType.STRING) {
                        try {
                            String stringValue = cell.getStringCellValue().trim();
                            field.set(dto, new BigDecimal(stringValue));
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Cannot convert '" + cell.getStringCellValue()
                                    + "' to BigDecimal for field " + field.getName());
                        }
                    } else {
                        throw new IllegalArgumentException(
                                "Cell type is not numeric or string for BigDecimal field " + field.getName());
                    }
                    break;
                case "Double":
                case "double": // Handle both Double wrapper and primitive double
                    if (cellType == CellType.NUMERIC) {
                        double val = cell.getNumericCellValue();
                        field.set(dto, val); // Autoboxing handles both Double and double
                    } else if (cellType == CellType.BLANK) {
                        break;
                    } else if (cellType == CellType.STRING) {
                        try {
                            String stringValue = cell.getStringCellValue().trim();
                            double doubleValue = Double.parseDouble(stringValue);
                            field.set(dto, doubleValue); // Autoboxing handles both Double and double
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Cannot convert '" + cell.getStringCellValue()
                                    + "' to Double for field " + field.getName());
                        }
                    } else {
                        throw new IllegalArgumentException(
                                "Cell type is not numeric or string for Double field " + field.getName());
                    }
                    break;
                default:
                    // Handle enum types
                    if (field.getType().isEnum()) {
                        String enumValue = null;
                        if (cellType == CellType.STRING) {
                            enumValue = cell.getStringCellValue().trim();
                        } else if (cellType == CellType.BLANK) {
                            break;
                        } else if (cellType == CellType.NUMERIC) {
                            // Try to convert numeric to string for enum
                            double numValue = cell.getNumericCellValue();
                            if (numValue == (int) numValue) {
                                enumValue = String.valueOf((int) numValue);
                            } else {
                                enumValue = String.valueOf(numValue);
                            }
                        } else {
                            throw new IllegalArgumentException(
                                    "Cell type is not string, numeric, or blank for Enum field " + field.getName()
                                            + ". Cell type: " + cellType);
                        }

                        if (enumValue != null) {
                            // Try exact match first
                            try {
                                @SuppressWarnings("unchecked")
                                Enum<?> enumVal = Enum.valueOf((Class<Enum>) field.getType(), enumValue);
                                field.set(dto, enumVal);
                            } catch (IllegalArgumentException e1) {
                                // Try case-insensitive match
                                try {
                                    @SuppressWarnings("unchecked")
                                    Enum<?>[] enumConstants = ((Class<Enum>) field.getType()).getEnumConstants();
                                    for (Enum<?> enumConstant : enumConstants) {
                                        if (enumConstant.name().equalsIgnoreCase(enumValue)) {
                                            field.set(dto, enumConstant);
                                            return;
                                        }
                                    }
                                    throw new IllegalArgumentException("Invalid enum value '" + enumValue
                                            + "' for field " + field.getName() + ". Valid values are: " +
                                            Arrays.toString(Arrays.stream(enumConstants).map(Enum::name).toArray()));
                                } catch (IllegalArgumentException e2) {
                                    throw new IllegalArgumentException("Invalid enum value '" + enumValue
                                            + "' for field " + field.getName()
                                            + ". Please select a value from the dropdown list.");
                                }
                            }
                        }
                    } else {
                        throw new IllegalArgumentException(
                                "Unsupported field type: " + field.getType().getSimpleName() + " for field "
                                        + field.getName());
                    }
                    break;
            }
        } catch (IllegalArgumentException e) {
            // Re-throw IllegalArgumentException as-is (these are our validation errors)
            throw e;
        } catch (Exception e) {
            System.err.println("Error setting field value for " + field.getName() + " with value: "
                    + getCellValueAsString(cell) + " of type: " + cell.getCellType());
            e.printStackTrace();
            throw new IllegalAccessException("Error processing field " + field.getName() + ": " + e.getMessage());
        }
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "null";
        }
        try {
            CellType cellType = cell.getCellType();
            if (cellType == CellType.FORMULA) {
                cellType = cell.getCachedFormulaResultType();
            }

            switch (cellType) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    double numValue = cell.getNumericCellValue();
                    if (numValue == (int) numValue) {
                        return String.valueOf((int) numValue);
                    } else {
                        return String.valueOf(numValue);
                    }
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case BLANK:
                    return "(blank)";
                case ERROR:
                    return "(error)";
                default:
                    return "(unknown)";
            }
        } catch (Exception e) {
            return "(error reading cell: " + e.getMessage() + ")";
        }
    }

    private static boolean isRowEmpty(Row row) {
        for (int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private static String findSheetName(ExcelType excelType) {
        switch (excelType) {
            case FUEL_STATIONARY_EMISSIONS:
                return "Stationary Emissions";
            case FUEL_TRANSPORT_EMISSIONS:
                return "Transport Fuel Emissions";
            case POPULATION_RECORDS:
                return "Population Records";
            case VEHICLE_DATA_TRANSPORT_EMISSIONS:
                return "Vehicle Based";
            case EICV_REPORT:
                return "EICV Reports";
            case SOLID_WASTE_STARTER_DATA:
                return "Solid Waste Starter Data";
            case INDUSTRIAL_WASTE_STARTER_DATA:
                return "Industrial Waste Starter Data";
            case ZERO_TILLAGE_MITIGATION:
                return "Zero Tillage Mitigation";
            case WETLAND_PARKS_MITIGATION:
                return "Wetland Parks Mitigation";
            case CROP_ROTATION_MITIGATION:
                return "Crop Rotation Mitigation";
            case STREET_TREES_MITIGATION:
                return "Street Trees Mitigation";
            case SETTLEMENT_TREES_MITIGATION:
                return "Settlement Trees Mitigation";
            case DAILY_SPREAD_MITIGATION:
                return "Daily Spread Mitigation";
            case ADDING_STRAW_MITIGATION:
                return "Adding Straw Mitigation";
            case MANURE_COVERING_MITIGATION:
                return "Manure Covering Mitigation";
            case PROTECTIVE_FOREST_MITIGATION:
                return "Protective Forest Mitigation";
            case GREEN_FENCES_MITIGATION:
                return "Green Fences Mitigation";
            case WASTE_TO_ENERGY_MITIGATION:
                return "Waste to Energy Mitigation";
            case LANDFILL_GAS_UTILIZATION_MITIGATION:
                return "Landfill Gas Utilization";
            case MBT_COMPOSTING_MITIGATION:
                return "MBT Composting Mitigation";
            case EPR_PLASTIC_WASTE_MITIGATION:
                return "EPR Plastic Waste Mitigation";
            case KIGALI_FSTP_MITIGATION:
                return "Kigali FSTP Mitigation";
            case KIGALI_WWTP_MITIGATION:
                return "Kigali WWTP Mitigation";
            case ISWM_MITIGATION:
                return "ISWM Mitigation";
            case IPPU_MITIGATION:
                return "IPPU Mitigation";
            case COOKSTOVE_MITIGATION:
                return "Cookstove Mitigation";
            case LIGHT_BULB_MITIGATION:
                return "Light Bulb Mitigation";
            default:
                return "";
        }
    }

    private static String findHeaderInSheet(ExcelType excelType, String header) {
        switch (excelType) {
            case FUEL_STATIONARY_EMISSIONS:
                return stationaryEmissionsToDtoMap.get(header);
            case FUEL_TRANSPORT_EMISSIONS:
                return transportEmissionsByFuelDtoMap.get(header);
            case POPULATION_RECORDS:
                return populationRecordsToDtoMap.get(header);
            case VEHICLE_DATA_TRANSPORT_EMISSIONS:
                return transportEmissionsByVehicleDataDtoMap.get(header);
            case EICV_REPORT:
                return eicvReportsToDtoMap.get(header);
            case SOLID_WASTE_STARTER_DATA:
                return solidWasteDtoMap.get(header);
            case INDUSTRIAL_WASTE_STARTER_DATA:
                return industrialWasteDtoMap.get(header);
            case ZERO_TILLAGE_MITIGATION:
                return zeroTillageToDtoMap.get(header);
            case WETLAND_PARKS_MITIGATION:
                return wetlandParksToDtoMap.get(header);
            case CROP_ROTATION_MITIGATION:
                return cropRotationToDtoMap.get(header);
            case STREET_TREES_MITIGATION:
                return streetTreesToDtoMap.get(header);
            case SETTLEMENT_TREES_MITIGATION:
                return settlementTreesToDtoMap.get(header);
            case DAILY_SPREAD_MITIGATION:
                return dailySpreadToDtoMap.get(header);
            case ADDING_STRAW_MITIGATION:
                return addingStrawToDtoMap.get(header);
            case MANURE_COVERING_MITIGATION:
                return manureCoveringToDtoMap.get(header);
            case PROTECTIVE_FOREST_MITIGATION:
                return protectiveForestToDtoMap.get(header);
            case GREEN_FENCES_MITIGATION:
                return greenFencesToDtoMap.get(header);
            case WASTE_TO_ENERGY_MITIGATION:
                return wasteToEnergyToDtoMap.get(header);
            case LANDFILL_GAS_UTILIZATION_MITIGATION:
                return landfillGasUtilizationToDtoMap.get(header);
            case MBT_COMPOSTING_MITIGATION:
                return mbtCompostingToDtoMap.get(header);
            case EPR_PLASTIC_WASTE_MITIGATION:
                return eprPlasticWasteToDtoMap.get(header);
            case KIGALI_FSTP_MITIGATION:
                return kigaliFSTPToDtoMap.get(header);
            case KIGALI_WWTP_MITIGATION:
                return kigaliWWTPToDtoMap.get(header);
            case ISWM_MITIGATION:
                return iswmToDtoMap.get(header);
            case IPPU_MITIGATION:
                return ippuToDtoMap.get(header);
            case COOKSTOVE_MITIGATION:
                return cookstoveToDtoMap.get(header);
            case LIGHT_BULB_MITIGATION:
                return lightBulbToDtoMap.get(header);
            default:
                return "";
        }
    }
}
