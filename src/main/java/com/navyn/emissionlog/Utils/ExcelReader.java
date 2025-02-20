package com.navyn.emissionlog.Utils;

import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

public class ExcelReader {
    private static final Map<String, String> excelToDtoMap = new HashMap<>();
    static {
        excelToDtoMap.put("Fuel Type", "fuelType");
        excelToDtoMap.put("Fuel", "fuel");
        excelToDtoMap.put("Lower Heating Value (LHV) (or NCV)", "lowerHeatingValue");
        excelToDtoMap.put("Energy basis", "energyBasis");
        excelToDtoMap.put("Mass basis", "massBasis");
        excelToDtoMap.put("Fuel density of Liquids", "fuelDensityLiquids");
        excelToDtoMap.put("Fuel density of Gases", "fuelDensityGases");
        excelToDtoMap.put("Liquid basis", "liquidBasis");
        excelToDtoMap.put("Gas basis", "gasBasis");
    }

    public static <T> List<T> readExcel(InputStream inputStream, Class<T> dtoClass) throws IOException {
        List<T> result = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheet("CH4");
            if (sheet == null) {
                throw new IOException("Sheet 'CH4' not found");
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
                        String fieldName = excelToDtoMap.get(header);
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
