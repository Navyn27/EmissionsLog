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

    // This hashmap is responsible for reading data from crop rotation mitigation
    // Excel files
    // and mapping it to DTOs.
    private static final Map<String, String> cropRotationToDtoMap = new HashMap<>();
    static {
        cropRotationToDtoMap.put("Year", "year");
        cropRotationToDtoMap.put("Cropland Under Crop Rotation", "croplandUnderCropRotation");
        cropRotationToDtoMap.put("Cropland Area Unit", "croplandAreaUnit");
        cropRotationToDtoMap.put("Aboveground Biomass", "abovegroundBiomass");
        cropRotationToDtoMap.put("Aboveground Biomass Unit", "abovegroundBiomassUnit");
        cropRotationToDtoMap.put("Increased Biomass", "increasedBiomass");
        cropRotationToDtoMap.put("Increased Biomass Unit", "increasedBiomassUnit");
    }

    public static <T> List<T> readExcel(InputStream inputStream, Class<T> dtoClass, ExcelType excelType)
            throws IOException {
        List<T> result = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheet(findSheetName(excelType));

            if (sheet == null) {
                throw new IOException("Sheet not found");
            }

            // Determine header row index based on ExcelType
            int headerRowIndex = getHeaderRowIndex(excelType);
            int firstDataRowIndex = headerRowIndex + 1;

            // Get header row
            Row headerRow = sheet.getRow(headerRowIndex);
            if (headerRow == null) {
                throw new IOException("Header row not found at index " + headerRowIndex);
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
        } catch (ReflectiveOperationException | IllegalArgumentException e) {
            e.printStackTrace();
            throw new IOException("Error mapping Excel data to DTO", e);
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
            default:
                // Other templates have headers at row 0
                return 0;
        }
    }

    private static <T> void setFieldValue(T dto, Field field, Cell cell) throws IllegalAccessException {
        try {
            switch (field.getType().getSimpleName()) {
                case "String":
                    if (cell.getCellType() == CellType.STRING) {
                        field.set(dto, cell.getStringCellValue());
                    } else if (cell.getCellType() == CellType.BLANK) {
                        break;
                    } else {
                        field.set(dto, String.valueOf(cell.getNumericCellValue()));
                    }
                    break;
                case "Integer":
                    if (cell.getCellType() == CellType.NUMERIC) {
                        field.set(dto, (int) cell.getNumericCellValue());
                    } else if (cell.getCellType() == CellType.BLANK) {
                        break;
                    } else {
                        throw new IllegalArgumentException("Cell type is not numeric for Integer field");
                    }
                    break;
                case "BigDecimal":
                    if (cell.getCellType() == CellType.NUMERIC) {
                        field.set(dto, BigDecimal.valueOf(cell.getNumericCellValue()));
                    } else if (cell.getCellType() == CellType.BLANK) {
                        break;
                    } else {
                        throw new IllegalArgumentException("Cell type is not numeric for BigDecimal field");
                    }
                    break;
                case "Double":
                    if (cell.getCellType() == CellType.NUMERIC) {
                        Double val = cell.getNumericCellValue();
                        field.set(dto, val);
                    } else if (cell.getCellType() == CellType.BLANK) {
                        break;
                    } else {
                        throw new IllegalArgumentException("Cell type is not numeric for Double field");
                    }
                    break;
                default:
                    // Handle enum types
                    if (field.getType().isEnum()) {
                        if (cell.getCellType() == CellType.STRING) {
                            String enumValue = cell.getStringCellValue().trim();
                            try {
                                @SuppressWarnings("unchecked")
                                Enum<?> enumVal = Enum.valueOf((Class<Enum>) field.getType(), enumValue);
                                field.set(dto, enumVal);
                            } catch (IllegalArgumentException e) {
                                throw new IllegalArgumentException("Invalid enum value '" + enumValue + "' for field "
                                        + field.getName() + ". Valid values: "
                                        + java.util.Arrays.toString(field.getType().getEnumConstants()));
                            }
                        } else if (cell.getCellType() == CellType.BLANK) {
                            break;
                        } else {
                            throw new IllegalArgumentException("Cell type is not string for Enum field");
                        }
                    } else {
                        throw new IllegalArgumentException(
                                "Unsupported field type: " + field.getType().getSimpleName());
                    }
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error setting field value for " + field.getName() + " with value: " + cell + "of type:"
                    + cell.getCellType());
            e.printStackTrace();
            throw e;
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
            case CROP_ROTATION_MITIGATION:
                return "Crop Rotation Mitigation";
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
            case CROP_ROTATION_MITIGATION:
                return cropRotationToDtoMap.get(header);
            default:
                return "";
        }
    }
}
