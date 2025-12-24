package com.navyn.emissionlog.modules.mitigationProjects.IPPU.service;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.modules.mitigationProjects.IPPU.dto.IPPUMitigationDTO;
import com.navyn.emissionlog.modules.mitigationProjects.IPPU.dto.IPPUMitigationResponseDTO;
import com.navyn.emissionlog.modules.mitigationProjects.IPPU.model.IPPUMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.IPPU.repository.IIPPURepository;
import com.navyn.emissionlog.utils.ExcelReader;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class IPPUServiceImpl implements IIPPUService {
    private final IIPPURepository iippuRepository;

    @Override
    public IPPUMitigation save(IPPUMitigationDTO ippuMitigationDTO) {
        IPPUMitigation ippuMitigation = new IPPUMitigation();
        updateIPPUMitigationFromDTO(ippuMitigation, ippuMitigationDTO);
        return iippuRepository.save(ippuMitigation);
    }

    @Override
    public IPPUMitigationResponseDTO findAll() {
        List<IPPUMitigation> mitigations = iippuRepository.findAll();
        double totalMitigationScenario = calculateTotal(mitigations, "mitigationScenario");
        double totalReducedEmissionInKtCO2e = calculateTotal(mitigations, "reducedEmissionInKtCO2e");
        return new IPPUMitigationResponseDTO(mitigations, totalMitigationScenario, totalReducedEmissionInKtCO2e);
    }

    @Override
    public Optional<IPPUMitigation> findById(UUID id) {
        return iippuRepository.findById(id);
    }

    @Override
    public void deleteById(UUID id) {
        iippuRepository.deleteById(id);
    }

    @Override
    public IPPUMitigation update(UUID id, IPPUMitigationDTO ippuMitigationDTO) {
        IPPUMitigation ippuMitigation = iippuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("IPPUMitigation not found with id: " + id));
        updateIPPUMitigationFromDTO(ippuMitigation, ippuMitigationDTO);
        return iippuRepository.save(ippuMitigation);
    }

    @Override
    public IPPUMitigationResponseDTO findByYear(int year) {
        List<IPPUMitigation> mitigations = iippuRepository.findByYear(year);
        double totalMitigationScenario = calculateTotal(mitigations, "mitigationScenario");
        double totalReducedEmissionInKtCO2e = calculateTotal(mitigations, "reducedEmissionInKtCO2e");
        return new IPPUMitigationResponseDTO(mitigations, totalMitigationScenario, totalReducedEmissionInKtCO2e);
    }

    private void updateIPPUMitigationFromDTO(IPPUMitigation ippuMitigation, IPPUMitigationDTO ippuMitigationDTO) {
        ippuMitigation.setYear(ippuMitigationDTO.getYear());
        ippuMitigation.setBau(ippuMitigationDTO.getBau());
        ippuMitigation.setFGasName(ippuMitigationDTO.getFGasName());
        ippuMitigation.setAmountOfAvoidedFGas(ippuMitigationDTO.getAmountOfAvoidedFGas());
        ippuMitigation.setGwpFactor(ippuMitigationDTO.getGwpFactor());

        double reducedEmissionInKgCO2e = ippuMitigationDTO.getAmountOfAvoidedFGas() * ippuMitigationDTO.getGwpFactor();
        ippuMitigation.setReducedEmissionInKgCO2e(reducedEmissionInKgCO2e);

        double reducedEmissionInKtCO2e = reducedEmissionInKgCO2e / 1000000;
        ippuMitigation.setReducedEmissionInKtCO2e(reducedEmissionInKtCO2e);

        ippuMitigation.setMitigationScenario(ippuMitigationDTO.getBau() - reducedEmissionInKtCO2e);
    }

    private double calculateTotal(List<IPPUMitigation> mitigations, String field) {
        return mitigations.stream()
                .mapToDouble(m -> {
                    return switch (field) {
                        case "mitigationScenario" -> m.getMitigationScenario();
                        case "reducedEmissionInKtCO2e" -> m.getReducedEmissionInKtCO2e();
                        default -> 0.0;
                    };
                })
                .sum();
    }

    @Override
    public byte[] generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("IPPU Mitigation");

            // Create title style
            XSSFCellStyle titleStyle = (XSSFCellStyle) workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 18);
            titleFont.setColor(IndexedColors.WHITE.getIndex());
            titleFont.setFontName("Calibri");
            titleStyle.setFont(titleFont);
            titleStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            titleStyle.setBorderTop(BorderStyle.MEDIUM);
            titleStyle.setBorderBottom(BorderStyle.MEDIUM);
            titleStyle.setBorderLeft(BorderStyle.MEDIUM);
            titleStyle.setBorderRight(BorderStyle.MEDIUM);

            // Create header style
            XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 11);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontName("Calibri");
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setWrapText(true);

            // Create data style
            XSSFCellStyle dataStyle = (XSSFCellStyle) workbook.createCellStyle();
            Font dataFont = workbook.createFont();
            dataFont.setFontName("Calibri");
            dataFont.setFontHeightInPoints((short) 10);
            dataStyle.setFont(dataFont);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setAlignment(HorizontalAlignment.LEFT);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataStyle.setWrapText(true);

            // Create alternate data style
            XSSFCellStyle alternateDataStyle = (XSSFCellStyle) workbook.createCellStyle();
            Font altDataFont = workbook.createFont();
            altDataFont.setFontName("Calibri");
            altDataFont.setFontHeightInPoints((short) 10);
            alternateDataStyle.setFont(altDataFont);
            alternateDataStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            alternateDataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            alternateDataStyle.setBorderBottom(BorderStyle.THIN);
            alternateDataStyle.setBorderTop(BorderStyle.THIN);
            alternateDataStyle.setBorderLeft(BorderStyle.THIN);
            alternateDataStyle.setBorderRight(BorderStyle.THIN);
            alternateDataStyle.setAlignment(HorizontalAlignment.LEFT);
            alternateDataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            alternateDataStyle.setWrapText(true);

            // Create number style
            XSSFCellStyle numberStyle = (XSSFCellStyle) workbook.createCellStyle();
            Font numFont = workbook.createFont();
            numFont.setFontName("Calibri");
            numFont.setFontHeightInPoints((short) 10);
            numberStyle.setFont(numFont);
            DataFormat dataFormat = workbook.createDataFormat();
            numberStyle.setDataFormat(dataFormat.getFormat("#,##0.00"));
            numberStyle.setBorderBottom(BorderStyle.THIN);
            numberStyle.setBorderTop(BorderStyle.THIN);
            numberStyle.setBorderLeft(BorderStyle.THIN);
            numberStyle.setBorderRight(BorderStyle.THIN);
            numberStyle.setAlignment(HorizontalAlignment.RIGHT);
            numberStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Create year style (centered number)
            XSSFCellStyle yearStyle = (XSSFCellStyle) workbook.createCellStyle();
            yearStyle.cloneStyleFrom(dataStyle);
            yearStyle.setAlignment(HorizontalAlignment.CENTER);

            int rowIdx = 0;

            // Title row
            Row titleRow = sheet.createRow(rowIdx++);
            titleRow.setHeightInPoints(30);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("IPPU Mitigation Template");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

            rowIdx++; // Blank row

            // Create header row
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(22);
            String[] headers = {
                    "Year",
                    "BAU",
                    "F-Gas Name",
                    "Amount of Avoided F-Gas",
                    "GWP Factor"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create example data rows
            Object[] exampleData1 = {
                    2024,
                    150.5,
                    "Perfluoromethane (PFC-14)",
                    5000.0,
                    7390.0
            };

            Object[] exampleData2 = {
                    2025,
                    180.75,
                    "Perfluoroethane (PFC-116)",
                    6000.0,
                    12200.0
            };

            // First example row
            Row exampleRow1 = sheet.createRow(rowIdx++);
            exampleRow1.setHeightInPoints(18);
            for (int i = 0; i < exampleData1.length; i++) {
                Cell cell = exampleRow1.createCell(i);
                if (i == 0) { // Year
                    cell.setCellStyle(yearStyle);
                    cell.setCellValue(((Number) exampleData1[i]).intValue());
                } else if (i == 1 || i == 3 || i == 4) { // BAU, Amount, GWP Factor (numbers)
                    cell.setCellStyle(numberStyle);
                    cell.setCellValue(((Number) exampleData1[i]).doubleValue());
                } else { // F-Gas Name (text)
                    cell.setCellStyle(dataStyle);
                    cell.setCellValue((String) exampleData1[i]);
                }
            }

            // Second example row with alternate style
            Row exampleRow2 = sheet.createRow(rowIdx++);
            exampleRow2.setHeightInPoints(18);
            for (int i = 0; i < exampleData2.length; i++) {
                Cell cell = exampleRow2.createCell(i);
                if (i == 0) { // Year
                    CellStyle altYearStyle = workbook.createCellStyle();
                    altYearStyle.cloneStyleFrom(alternateDataStyle);
                    altYearStyle.setAlignment(HorizontalAlignment.CENTER);
                    cell.setCellStyle(altYearStyle);
                    cell.setCellValue(((Number) exampleData2[i]).intValue());
                } else if (i == 1 || i == 3 || i == 4) { // BAU, Amount, GWP Factor (numbers)
                    CellStyle altNumStyle = workbook.createCellStyle();
                    altNumStyle.cloneStyleFrom(numberStyle);
                    altNumStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    altNumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cell.setCellStyle(altNumStyle);
                    cell.setCellValue(((Number) exampleData2[i]).doubleValue());
                } else { // F-Gas Name (text)
                    cell.setCellStyle(alternateDataStyle);
                    cell.setCellValue((String) exampleData2[i]);
                }
            }

            // Auto-size columns with limits
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                int currentWidth = sheet.getColumnWidth(i);
                int minWidth = 3000;
                int maxWidth = 20000;
                if (currentWidth < minWidth) {
                    sheet.setColumnWidth(i, minWidth);
                } else if (currentWidth > maxWidth) {
                    sheet.setColumnWidth(i, maxWidth);
                }
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generating Excel template", e);
        }
    }

    @Override
    @Transactional
    public Map<String, Object> createIPPUMitigationFromExcel(MultipartFile file) {
        List<IPPUMitigation> savedRecords = new ArrayList<>();
        List<String> skippedRecords = new ArrayList<>();
        int totalProcessed = 0;

        try {
            List<IPPUMitigationDTO> dtos = ExcelReader.readExcel(
                    file.getInputStream(),
                    IPPUMitigationDTO.class,
                    ExcelType.IPPU_MITIGATION);

            for (int i = 0; i < dtos.size(); i++) {
                IPPUMitigationDTO dto = dtos.get(i);
                totalProcessed++;
                int actualRowNumber = i + 1 + 3; // +1 for 1-based, +3 for title(1) + blank(1) + header(1)

                // Validate required fields
                List<String> missingFields = new ArrayList<>();
                if (dto.getYear() == 0) {
                    missingFields.add("Year");
                }
                if (dto.getBau() < 0) {
                    missingFields.add("BAU");
                }
                if (dto.getFGasName() == null || dto.getFGasName().trim().isEmpty()) {
                    missingFields.add("F-Gas Name");
                }
                if (dto.getAmountOfAvoidedFGas() <= 0) {
                    missingFields.add("Amount of Avoided F-Gas");
                }
                if (dto.getGwpFactor() <= 0) {
                    missingFields.add("GWP Factor");
                }

                if (!missingFields.isEmpty()) {
                    throw new RuntimeException(String.format(
                            "Missing required fields: %s. Please fill in all required fields in your Excel file.",
                            String.join(", ", missingFields)));
                }

                // Check if record with same year AND fGasName already exists (composite
                // uniqueness)
                if (iippuRepository.findByYearAndFGasName(dto.getYear(), dto.getFGasName()).isPresent()) {
                    skippedRecords.add(dto.getYear() + "-" + dto.getFGasName());
                    continue; // Skip this row
                }

                // Create the record
                IPPUMitigation saved = save(dto);
                savedRecords.add(saved);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("saved", savedRecords);
            result.put("savedCount", savedRecords.size());
            result.put("skippedCount", skippedRecords.size());
            result.put("skippedRecords", skippedRecords);
            result.put("totalProcessed", totalProcessed);

            return result;
        } catch (IOException e) {
            // Re-throw IOException with user-friendly message
            String message = e.getMessage();
            if (message != null) {
                throw new RuntimeException(message, e);
            } else {
                throw new RuntimeException(
                        "Incorrect template. Please download the correct template and try again.",
                        e);
            }
        } catch (NullPointerException e) {
            // Handle null pointer exceptions with clear message
            throw new RuntimeException(
                    "Missing required fields. Please fill in all required fields in your Excel file.", e);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null) {
                throw new RuntimeException(errorMsg, e);
            }
            throw new RuntimeException("Error processing Excel file. Please check your file and try again.", e);
        }
    }
}
