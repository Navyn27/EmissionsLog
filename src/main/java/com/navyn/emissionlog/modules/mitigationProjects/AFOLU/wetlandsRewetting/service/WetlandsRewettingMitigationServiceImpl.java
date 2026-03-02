package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.service;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.dtos.WetlandsRewettingParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.dtos.WetlandsRewettingMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.dtos.WetlandsRewettingMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.models.WetlandsRewettingMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.repositories.WetlandsRewettingMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.swap.Swap;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.swap.SwapRepository;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.repositories.InterventionRepository;
import com.navyn.emissionlog.utils.ExcelReader;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static com.navyn.emissionlog.utils.Specifications.MitigationSpecifications.hasYear;

@Service
@RequiredArgsConstructor
public class WetlandsRewettingMitigationServiceImpl implements WetlandsRewettingMitigationService {

    private static final double CO2_TO_C_RATIO = 44.0 / 12.0;

    private final WetlandsRewettingMitigationRepository repository;
    private final WetlandsRewettingParameterService parameterService;
    private final SwapRepository swapRepository;
    private final InterventionRepository interventionRepository;

    @Override
    @Transactional
    public WetlandsRewettingMitigationResponseDto create(WetlandsRewettingMitigationDto dto) {
        WetlandsRewettingParameterResponseDto param = parameterService.getLatestActive();
        Swap swap = swapRepository.findById(dto.getSwapId())
                .orElseThrow(() -> new RuntimeException("Swap not found with id: " + dto.getSwapId()));

        WetlandsRewettingMitigation mitigation = new WetlandsRewettingMitigation();
        mitigation.setYear(dto.getYear());
        mitigation.setAreaRewettedMineralWetlandsHa(dto.getAreaRewettedMineralWetlandsHa());
        mitigation.setSwap(swap);
        if (dto.getInterventionId() != null) {
            Intervention intervention = interventionRepository.findById(dto.getInterventionId())
                    .orElseThrow(() -> new RuntimeException("Intervention not found with id: " + dto.getInterventionId()));
            mitigation.setIntervention(intervention);
        } else {
            mitigation.setIntervention(null);
        }
        computeAndSetCalculatedFields(mitigation, param);
        WetlandsRewettingMitigation saved = repository.save(mitigation);
        return toResponseDto(saved);
    }

    @Override
    @Transactional
    public WetlandsRewettingMitigationResponseDto update(UUID id, WetlandsRewettingMitigationDto dto) {
        WetlandsRewettingParameterResponseDto param = parameterService.getLatestActive();
        WetlandsRewettingMitigation mitigation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wetlands Rewetting Mitigation not found with id: " + id));
        Swap swap = swapRepository.findById(dto.getSwapId())
                .orElseThrow(() -> new RuntimeException("Swap not found with id: " + dto.getSwapId()));

        mitigation.setYear(dto.getYear());
        mitigation.setAreaRewettedMineralWetlandsHa(dto.getAreaRewettedMineralWetlandsHa());
        mitigation.setSwap(swap);
        if (dto.getInterventionId() != null) {
            Intervention intervention = interventionRepository.findById(dto.getInterventionId())
                    .orElseThrow(() -> new RuntimeException("Intervention not found with id: " + dto.getInterventionId()));
            mitigation.setIntervention(intervention);
        } else {
            mitigation.setIntervention(null);
        }
        computeAndSetCalculatedFields(mitigation, param);
        WetlandsRewettingMitigation updated = repository.save(mitigation);
        return toResponseDto(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Wetlands Rewetting Mitigation not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WetlandsRewettingMitigationResponseDto> getAll(Integer year) {
        Specification<WetlandsRewettingMitigation> spec = Specification.where(null);
        if (year != null) {
            spec = spec.and(hasYear(year));
        }
        List<WetlandsRewettingMitigation> list = repository.findAll(spec, Sort.by(Sort.Direction.ASC, "year"));
        return list.stream().map(this::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WetlandsRewettingMitigationResponseDto> getById(UUID id) {
        return repository.findById(id).map(this::toResponseDto);
    }

    private void computeAndSetCalculatedFields(WetlandsRewettingMitigation mitigation, WetlandsRewettingParameterResponseDto param) {
        double areaHa = mitigation.getAreaRewettedMineralWetlandsHa();
        double ch4Factor = param.getCh4EmissionFactorPerHaPerYear();
        double gwpMethane = param.getGwpMethane();
        double carbonSeqFactor = param.getCarbonSequestrationFactorPerHaPerYear();

        // 1. CH4 emissions (tonnes/year), then kilotonnes/year
        double ch4TonnesPerYear = areaHa * ch4Factor;
        mitigation.setCh4EmissionsKilotonnesPerYear(ch4TonnesPerYear / 1000.0);

        // 2. Emissions in CO2e (tonnes/year)
        double emissionsCo2eTonnesPerYear = ch4TonnesPerYear * gwpMethane;
        mitigation.setEmissionsCo2eTonnesPerYear(emissionsCo2eTonnesPerYear);

        // 3. Sequestration of carbon (tonnes C/year)
        double sequestrationTonnesC = areaHa * carbonSeqFactor;
        mitigation.setSequestrationTonnesC(sequestrationTonnesC);

        // 4. CO2e value of sequestration (tonnes CO2e/year)
        double co2eValueOfSequestration = sequestrationTonnesC * CO2_TO_C_RATIO;
        mitigation.setCo2eValueOfSequestrationTonnes(co2eValueOfSequestration);

        // 5. Total mitigation (tonnes CO2e/year)
        double totalMitigation = co2eValueOfSequestration - emissionsCo2eTonnesPerYear;
        mitigation.setTotalMitigationTonnesCo2e(totalMitigation);
    }

    private WetlandsRewettingMitigationResponseDto toResponseDto(WetlandsRewettingMitigation mitigation) {
        WetlandsRewettingMitigationResponseDto dto = new WetlandsRewettingMitigationResponseDto();
        dto.setId(mitigation.getId());
        dto.setYear(mitigation.getYear());
        dto.setAreaRewettedMineralWetlandsHa(mitigation.getAreaRewettedMineralWetlandsHa());
        dto.setCh4EmissionsKilotonnesPerYear(mitigation.getCh4EmissionsKilotonnesPerYear());
        dto.setEmissionsCo2eTonnesPerYear(mitigation.getEmissionsCo2eTonnesPerYear());
        dto.setSequestrationTonnesC(mitigation.getSequestrationTonnesC());
        dto.setCo2eValueOfSequestrationTonnes(mitigation.getCo2eValueOfSequestrationTonnes());
        dto.setTotalMitigationTonnesCo2e(mitigation.getTotalMitigationTonnesCo2e());
        dto.setCreatedAt(mitigation.getCreatedAt());
        dto.setUpdatedAt(mitigation.getUpdatedAt());

        if (mitigation.getSwap() != null) {
            Hibernate.initialize(mitigation.getSwap());
            Swap swap = mitigation.getSwap();
            dto.setSwap(new WetlandsRewettingMitigationResponseDto.SwapInfo(swap.getId(), swap.getName()));
        } else {
            dto.setSwap(null);
        }
        if (mitigation.getIntervention() != null) {
            Hibernate.initialize(mitigation.getIntervention());
            Intervention intervention = mitigation.getIntervention();
            dto.setIntervention(new WetlandsRewettingMitigationResponseDto.InterventionInfo(intervention.getId(), intervention.getName()));
        } else {
            dto.setIntervention(null);
        }
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateExcelTemplate() {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Wetlands Rewetting Mitigation");

            XSSFCellStyle titleStyle = (XSSFCellStyle) workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 18);
            titleFont.setColor(IndexedColors.WHITE.getIndex());
            titleStyle.setFont(titleFont);
            titleStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 11);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setWrapText(true);

            XSSFCellStyle dataStyle = (XSSFCellStyle) workbook.createCellStyle();
            Font dataFont = workbook.createFont();
            dataFont.setFontHeightInPoints((short) 10);
            dataStyle.setFont(dataFont);
            dataStyle.setAlignment(HorizontalAlignment.LEFT);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            XSSFCellStyle numberStyle = (XSSFCellStyle) workbook.createCellStyle();
            numberStyle.cloneStyleFrom(dataStyle);
            DataFormat dataFormat = workbook.createDataFormat();
            numberStyle.setDataFormat(dataFormat.getFormat("#,##0.00"));
            numberStyle.setAlignment(HorizontalAlignment.RIGHT);

            XSSFCellStyle yearStyle = (XSSFCellStyle) workbook.createCellStyle();
            yearStyle.cloneStyleFrom(dataStyle);
            yearStyle.setAlignment(HorizontalAlignment.CENTER);

            int rowIdx = 0;
            List<Swap> swaps = swapRepository.findAll();
            String[] swapNames = swaps.stream().map(Swap::getName).sorted().toArray(String[]::new);
            List<Intervention> interventions = interventionRepository.findAll();
            String[] interventionNames = interventions.stream().map(Intervention::getName).sorted().toArray(String[]::new);

            Row titleRow = sheet.createRow(rowIdx++);
            titleRow.setHeightInPoints(30);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Wetlands Rewetting Mitigation Template");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
            rowIdx++;

            String[] headers = {"Year", "Area of rewetted mineral wetlands (ha)", "Swap Name", "Intervention"};
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(22);
            for (int i = 0; i < headers.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }

            DataValidationHelper validationHelper = sheet.getDataValidationHelper();
            if (swapNames.length > 0) {
                CellRangeAddressList swapList = new CellRangeAddressList(3, 1000, 2, 2);
                DataValidationConstraint swapConstraint = validationHelper.createExplicitListConstraint(swapNames);
                DataValidation swapValidation = validationHelper.createValidation(swapConstraint, swapList);
                swapValidation.setShowErrorBox(true);
                swapValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
                swapValidation.createErrorBox("Invalid Swap", "Please select a valid swap from the dropdown.");
                swapValidation.setShowPromptBox(true);
                swapValidation.createPromptBox("Swap Name", "Select a swap from the dropdown.");
                sheet.addValidationData(swapValidation);
            }
            if (interventionNames.length > 0) {
                String[] interventionOptions = new String[interventionNames.length + 1];
                interventionOptions[0] = "";
                System.arraycopy(interventionNames, 0, interventionOptions, 1, interventionNames.length);
                CellRangeAddressList interventionList = new CellRangeAddressList(3, 1000, 3, 3);
                DataValidationConstraint interventionConstraint = validationHelper.createExplicitListConstraint(interventionOptions);
                DataValidation interventionValidation = validationHelper.createValidation(interventionConstraint, interventionList);
                interventionValidation.setShowErrorBox(true);
                interventionValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
                interventionValidation.createErrorBox("Invalid Intervention", "Please select a valid intervention from the dropdown or leave empty.");
                interventionValidation.setShowPromptBox(true);
                interventionValidation.createPromptBox("Intervention", "Select an intervention (optional).");
                sheet.addValidationData(interventionValidation);
            }

            Row exampleRow = sheet.createRow(rowIdx++);
            exampleRow.createCell(0).setCellValue(2025);
            exampleRow.getCell(0).setCellStyle(yearStyle);
            exampleRow.createCell(1).setCellValue(50.0);
            exampleRow.getCell(1).setCellStyle(numberStyle);
            Cell swapCell = exampleRow.createCell(2);
            swapCell.setCellStyle(dataStyle);
            swapCell.setCellValue(swapNames.length > 0 ? swapNames[0] : "");
            Cell interventionCell = exampleRow.createCell(3);
            interventionCell.setCellStyle(dataStyle);
            interventionCell.setCellValue(interventionNames.length > 0 ? interventionNames[0] : "");

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                if (sheet.getColumnWidth(i) < 4000) sheet.setColumnWidth(i, 4000);
            }
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generating Excel template", e);
        }
    }

    @Override
    @Transactional
    public Map<String, Object> createFromExcel(MultipartFile file) {
        List<WetlandsRewettingMitigationResponseDto> savedRecords = new ArrayList<>();
        List<Map<String, Object>> skippedMissingFields = new ArrayList<>();
        List<Map<String, Object>> skippedSwapNotFound = new ArrayList<>();
        List<Map<String, Object>> skippedInterventionNotFound = new ArrayList<>();
        List<Map<String, Object>> skippedParameterNotFound = new ArrayList<>();
        int totalProcessed = 0;

        try {
            List<WetlandsRewettingMitigationDto> dtos = ExcelReader.readExcel(
                    file.getInputStream(),
                    WetlandsRewettingMitigationDto.class,
                    ExcelType.WETLANDS_REWETTING_MITIGATION);

            for (int i = 0; i < dtos.size(); i++) {
                WetlandsRewettingMitigationDto dto = dtos.get(i);
                totalProcessed++;
                int excelRowNumber = i + 4;

                if (dto.getYear() == null) {
                    skippedMissingFields.add(Map.of("row", excelRowNumber, "reason", "Year is required"));
                    continue;
                }
                if (dto.getAreaRewettedMineralWetlandsHa() == null || dto.getAreaRewettedMineralWetlandsHa() < 0) {
                    skippedMissingFields.add(Map.of("row", excelRowNumber, "reason", "Area of rewetted mineral wetlands (ha) is required and must be >= 0"));
                    continue;
                }

                if (dto.getSwapName() != null && !dto.getSwapName().trim().isEmpty()) {
                    String name = dto.getSwapName().trim();
                    Optional<Swap> swapOpt = swapRepository.findByNameIgnoreCase(name);
                    if (swapOpt.isPresent()) {
                        dto.setSwapId(swapOpt.get().getId());
                    } else {
                        skippedSwapNotFound.add(Map.of("row", excelRowNumber, "year", dto.getYear(), "reason", "Swap '" + name + "' not found"));
                        continue;
                    }
                }
                dto.setSwapName(null);

                if (dto.getSwapId() == null) {
                    skippedMissingFields.add(Map.of("row", excelRowNumber, "reason", "Swap Name is required"));
                    continue;
                }

                if (dto.getInterventionName() != null && !dto.getInterventionName().trim().isEmpty()) {
                    String name = dto.getInterventionName().trim();
                    Optional<Intervention> interventionOpt = interventionRepository.findByNameIgnoreCase(name);
                    if (interventionOpt.isPresent()) {
                        dto.setInterventionId(interventionOpt.get().getId());
                    } else {
                        skippedInterventionNotFound.add(Map.of("row", excelRowNumber, "year", dto.getYear(), "reason", "Intervention '" + name + "' not found"));
                        continue;
                    }
                }
                dto.setInterventionName(null);

                try {
                    WetlandsRewettingMitigationResponseDto saved = create(dto);
                    savedRecords.add(saved);
                } catch (RuntimeException e) {
                    String msg = e.getMessage();
                    if (msg != null && (msg.contains("Parameter") || msg.contains("active parameter") || msg.contains("WetlandsRewettingParameter"))) {
                        skippedParameterNotFound.add(Map.of("row", excelRowNumber, "year", dto.getYear(), "reason", msg));
                    } else {
                        throw e;
                    }
                }
            }

            int totalSkipped = skippedMissingFields.size() + skippedSwapNotFound.size() + skippedInterventionNotFound.size() + skippedParameterNotFound.size();
            Map<String, Object> result = new HashMap<>();
            result.put("saved", savedRecords);
            result.put("savedCount", savedRecords.size());
            result.put("skippedCount", totalSkipped);
            result.put("skippedMissingFields", skippedMissingFields);
            result.put("skippedSwapNotFound", skippedSwapNotFound);
            result.put("skippedInterventionNotFound", skippedInterventionNotFound);
            result.put("skippedParameterNotFound", skippedParameterNotFound);
            result.put("totalProcessed", totalProcessed);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage() != null ? e.getMessage() : "Incorrect template. Please download the correct template and try again.", e);
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg != null) throw new RuntimeException(msg, e);
            throw new RuntimeException("Error processing Excel file. Please check your file and try again.", e);
        }
    }
}
