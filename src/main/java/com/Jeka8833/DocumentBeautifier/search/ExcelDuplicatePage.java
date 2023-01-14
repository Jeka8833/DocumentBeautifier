package com.Jeka8833.DocumentBeautifier.search;

import com.Jeka8833.DocumentBeautifier.ColumnName;
import com.Jeka8833.DocumentBeautifier.excel.ExcelCell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelDuplicatePage {
    private static final Logger logger = LogManager.getLogger(ExcelDuplicatePage.class);
    private final Map<String, List<FilterPage>> filterList = new HashMap<>();
    private final ColumnName[] columnNames;

    public ExcelDuplicatePage(ColumnName[] columnNames) {
        this.columnNames = columnNames;
    }

    public void create(Path output) {
        try (Workbook workbook = new XSSFWorkbook()) {
            for (Map.Entry<String, List<FilterPage>> entry : filterList.entrySet()) {
                Sheet sheet = workbook.createSheet(entry.getKey());

                Row row = sheet.createRow(0);
                ExcelCell.writeCell(row, 0, "Match");
                ExcelCell.writeCell(row, 1, "Row");
                ExcelCell.writeCell(row, 2, "Sheet");
                ExcelCell.writeCell(row, 3, "Path");
                for (int i = 0; i < columnNames.length; i++) {
                    ExcelCell.writeCell(row, 4 + i, columnNames[i].getName());
                }

                int rowNumber = 1;
                for (FilterPage page : entry.getValue()) {
                    row = sheet.createRow(rowNumber++);
                    ExcelCell.writeCell(row, 0, page.name());

                    for (Map.Entry<String, List<DBRow>> filterEntry : page.duplicates().entrySet()) {
                        row = sheet.createRow(rowNumber++);
                        ExcelCell.writeCell(row, 0, filterEntry.getKey());

                        for (DBRow rowData : filterEntry.getValue()) {
                            ExcelCell.writeCell(row, 1, Integer.toString(rowData.cell().getRowIndex() + 1));
                            ExcelCell.writeCell(row, 2, rowData.sheet().getSheet().getSheetName());
                            ExcelCell.writeCell(row, 3, rowData.sheet().getReader().getInputFile().toString());
                            for (int i = 0; i < columnNames.length; i++) {
                                ColumnName columnNamePos = rowData.sheet().getColumnNames().get(columnNames[i]);
                                if (columnNamePos != null && columnNamePos.getPosX() >= 0) {
                                    Cell cell = rowData.cell().getRow().getCell(columnNamePos.getPosX());
                                    ExcelCell.writeCell(row, 4 + i, ExcelCell.getText(cell));
                                }
                            }

                            row = sheet.createRow(rowNumber++);
                        }
                    }
                }
            }
            try (OutputStream stream = Files.newOutputStream(output)) {
                workbook.write(stream);
            }
        } catch (IOException e) {
            logger.error("Fail create Excel document", e);
        }
    }

    public void addFilter(String sheetName, FilterPage filterPage) {
        if (!filterList.containsKey(sheetName)) filterList.put(sheetName, new ArrayList<>());

        filterList.get(sheetName).add(filterPage);
    }

    public record FilterPage(String name, Map<String, List<DBRow>> duplicates) {
    }
}
