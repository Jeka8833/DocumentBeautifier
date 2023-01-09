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
                int rowNumber = 0;
                Row row = sheet.createRow(rowNumber++);

                int columnPos = 0;
                ExcelCell.writeCell(row, columnPos++, "Match");
                ExcelCell.writeCell(row, columnPos++, "Row");
                for (ColumnName name : columnNames) ExcelCell.writeCell(row, columnPos++, name.getName());

                for (FilterPage page : entry.getValue()) {
                    row = sheet.createRow(rowNumber++);
                    ExcelCell.writeCell(row, 0, page.name());

                    for (Map.Entry<String, List<SearchDB.DBRow>> filterEntry : page.duplicates().entrySet()) {
                        List<SearchDB.DBRow> data = filterEntry.getValue();

                        row = sheet.createRow(rowNumber++);
                        ExcelCell.writeCell(row, 0, filterEntry.getKey());
                        for (SearchDB.DBRow rowData : data) {
                            columnPos = 1;
                            ExcelCell.writeCell(row, columnPos++, "" + (rowData.getCell().getRowIndex() + 1));
                            for (ColumnName name : columnNames) {
                                ColumnName columnNamePos = rowData.getSheet().getColumnNames().get(name);
                                if (columnNamePos == null) {
                                    ExcelCell.writeCell(row, columnPos++, "");
                                } else {
                                    Cell cell = rowData.getSheet().getSheet()
                                            .getRow(rowData.getCell().getRowIndex())
                                            .getCell(columnNamePos.getPosX());

                                    ExcelCell.writeCell(row, columnPos++, ExcelCell.getText(cell));
                                }
                            }

                            row = sheet.createRow(rowNumber++);
                        }
                    }
                }
            }
            workbook.write(Files.newOutputStream(output));
        } catch (IOException e) {
            logger.error("Fail create Excel document", e);
        }
    }

    public void addFilter(String sheetName, FilterPage filterPage) {
        if (!filterList.containsKey(sheetName)) filterList.put(sheetName, new ArrayList<>());

        filterList.get(sheetName).add(filterPage);
    }

    public record FilterPage(String name, Map<String, List<SearchDB.DBRow>> duplicates) {
    }
}
