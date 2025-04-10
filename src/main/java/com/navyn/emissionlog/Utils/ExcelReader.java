package com.navyn.emissionlog.Utils;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.Enums.RegionGroup;
import com.navyn.emissionlog.Enums.TransportType;
import com.navyn.emissionlog.Enums.VehicleEngineType;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

public class ExcelReader {

    // This hashmap is responsible for reading data from stationary emissions Excel files and mapping it to DTOs.
    private static final Map<String, String> stationaryEmissionsToDtoMap = new HashMap<>();
    static {
        stationaryEmissionsToDtoMap.put("Fuel Type", "fuelType");
        stationaryEmissionsToDtoMap.put("Fuel", "fuel");
        stationaryEmissionsToDtoMap.put("Description", "fuelDescription");
        stationaryEmissionsToDtoMap.put("Lower Heating Value (LHV) (or NCV)", "lowerHeatingValue");
        stationaryEmissionsToDtoMap.put("Emission","emission");
        stationaryEmissionsToDtoMap.put("Energy basis", "energyBasis");
        stationaryEmissionsToDtoMap.put("Mass basis", "massBasis");
        stationaryEmissionsToDtoMap.put("Fuel density of Liquids", "fuelDensityLiquids");
        stationaryEmissionsToDtoMap.put("Fuel density of Gases", "fuelDensityGases");
        stationaryEmissionsToDtoMap.put("Liquid basis", "liquidBasis");
        stationaryEmissionsToDtoMap.put("Gas basis", "gasBasis");
    }

    // This hashmap is responsible for reading data from transport emissions by fuel Excel files and mapping it to DTOs.
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
    }

    // This hashmap is responsible for reading data from transport emissions by vehicle data Excel files and mapping it to DTOs.
    private static final Map<String, String> transportEmissionsByVehicleDataDtoMap = new HashMap<>();
    static {
        transportEmissionsByVehicleDataDtoMap.put("Region", "regionGroup");
        transportEmissionsByVehicleDataDtoMap.put("Vehicle", "vehicle");
        transportEmissionsByVehicleDataDtoMap.put("Size", "size");
        transportEmissionsByVehicleDataDtoMap.put("% Weight Laden", "weightLaden");
        transportEmissionsByVehicleDataDtoMap.put("Vehicle Year", "vehicleYear");
        transportEmissionsByVehicleDataDtoMap.put("Fuel", "fuel");
        transportEmissionsByVehicleDataDtoMap.put("Fuel Type", "fuelType");
        transportEmissionsByVehicleDataDtoMap.put("CO2 EF", "CO2EmissionFactor");
        transportEmissionsByVehicleDataDtoMap.put("CH4 EF", "CH4EmissionFactor");
        transportEmissionsByVehicleDataDtoMap.put("N2O EF", "N2OEmissionFactor");
    }

    public static <T> List<T> readEmissionsExcel(InputStream inputStream, Class<T> dtoClass, ExcelType excelType) throws IOException {
        List<T> result = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = excelType.equals(ExcelType.FUEL_STATIONARY_EMISSIONS)? workbook.getSheet("Stationary Emissions"):
                                     excelType.equals(ExcelType.FUEL_TRANSPORT_EMISSIONS) ? workbook.getSheet("Transport Fuel Emissions"):
                                            workbook.getSheet("Vehicle Based");
            if (sheet == null) {
                throw new IOException("Sheet not found");
            }
            Iterator<Row> rowIterator = sheet.iterator();

            if (rowIterator.hasNext()) {
                Row headerRow = rowIterator.next();
                List<String> headers = new ArrayList<>();
                headerRow.forEach(cell -> headers.add(cell.getStringCellValue().trim()));

                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    if (isRowEmpty(row)) continue;

                    T dto = dtoClass.getDeclaredConstructor().newInstance();
                    for (int i = 0; i < headers.size(); i++) {
                        Cell cell = row.getCell(i);
                        if (cell == null) continue;

                        String header = headers.get(i);
                        String fieldName = excelType.equals(ExcelType.FUEL_STATIONARY_EMISSIONS)? stationaryEmissionsToDtoMap.get(header):
                                                    excelType.equals(ExcelType.FUEL_TRANSPORT_EMISSIONS) ? transportEmissionsByFuelDtoMap.get(header) :
                                                            transportEmissionsByVehicleDataDtoMap.get(header);

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
            }
        } catch (ReflectiveOperationException | IllegalArgumentException e) {
            e.printStackTrace();
            throw new IOException("Error mapping Excel data to DTO", e);
        }
        return result;
    }

    private static <T> void setFieldValue(T dto, Field field, Cell cell) throws IllegalAccessException {
        try {
            switch (field.getType().getSimpleName()) {
                case "String":
                    if (cell.getCellType() == CellType.STRING) {
                        field.set(dto, cell.getStringCellValue());
                    }else if(cell.getCellType() == CellType.BLANK){
                        break;
                    }else {
                        field.set(dto, String.valueOf(cell.getNumericCellValue()));
                    }
                    break;
                case "Integer":
                    if (cell.getCellType() == CellType.NUMERIC) {
                        field.set(dto, (int) cell.getNumericCellValue());
                    }else if(cell.getCellType() == CellType.BLANK){
                        break;
                    } else {
                        throw new IllegalArgumentException("Cell type is not numeric for Integer field");
                    }
                    break;
                case "BigDecimal":
                    if (cell.getCellType() == CellType.NUMERIC) {
                        field.set(dto, BigDecimal.valueOf(cell.getNumericCellValue()));
                    }else if(cell.getCellType() == CellType.BLANK){
                        break;
                    } else {
                        throw new IllegalArgumentException("Cell type is not numeric for BigDecimal field");
                    }
                    break;
                case "Double":
                    if (cell.getCellType() == CellType.NUMERIC) {
                        Double val = cell.getNumericCellValue();
                        field.set(dto, val);
                    }else if(cell.getCellType() == CellType.BLANK){
                        break;
                    } else {
                        throw new IllegalArgumentException("Cell type is not numeric for Double field");
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported field type: " + field.getType().getSimpleName());
            }
        } catch (Exception e) {
            System.err.println("Error setting field value for " + field.getName() + " with value: " + cell + "of type:" + cell.getCellType());
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
}
