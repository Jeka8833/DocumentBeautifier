package com.Jeka8833.DocumentBeautifier.excel;

import com.Jeka8833.DocumentBeautifier.Document;
import com.Jeka8833.DocumentBeautifier.header.ColumnHeader;
import com.Jeka8833.DocumentBeautifier.header.ColumnParser;
import com.Jeka8833.DocumentBeautifier.util.MySet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class SheetDetailed {
    private static final Logger logger = LogManager.getLogger(SheetDetailed.class);

    private final Sheet sheet;
    private final ExcelReader reader;
    private final MySet<ColumnHeader> columnNames = new MySet<>();

    private int startPosY = 0;

    public SheetDetailed(Sheet sheet, ExcelReader reader) {
        this.sheet = sheet;
        this.reader = reader;
    }

    public Sheet getSheet() {
        return sheet;
    }


    public boolean haveColumn() {
        return !columnNames.isEmpty();
    }

    public void setColumnNames(Collection<ColumnHeader> columnHeaders) {
        this.columnNames.clear();
        this.columnNames.addAll(columnHeaders);
    }

    public void readColumnNames(Document document) {
        columnNames.clear();

        MySet<ColumnHeader> allowingLabels = new MySet<>(
                document.getMods().stream()
                        .flatMap(mod -> Arrays.stream(mod.getNeededColumn()))
                        .collect(Collectors.toList()));

        boolean stop = false;
        for (Row row : sheet) {
            if (stop) return;

            for (Cell cell : row) {
                String text = ExcelCell.getText(cell);
                MySet<ColumnHeader> header = ColumnParser.text2Columns(text,
                        allowingLabels, document.getAlternativeColumnNames());
                for (ColumnHeader label : header.keySet()) {
                    label.setPosX(cell.getColumnIndex());
                    if (columnNames.contains(label))
                        logger.warn("A column has already been defined. Contained header will be replaced: "
                                + columnNames.get(label) + " to " + label);
                    columnNames.add(label);
                    startPosY = cell.getRowIndex() + 1;
                    stop = true;
                }
            }
        }
        if (!stop)
            logger.warn("Sheet " + sheet.getSheetName() + " doesn't have header, file: " + reader.getInputFile());
    }

    public MySet<ColumnHeader> getColumnNames() {
        return columnNames;
    }

    public int getStartPosY() {
        return startPosY;
    }

    public ExcelReader getReader() {
        return reader;
    }

    private CellStyle dateStyle;

    public CellStyle dateStyle() {
        if (dateStyle != null) return dateStyle;

        dateStyle = reader.getWorkbook().createCellStyle();
        dateStyle.setDataFormat(reader.getWorkbook().getCreationHelper().createDataFormat().getFormat("dd.mm.yyyy"));
        return dateStyle;
    }

    private CellStyle yellowColorStyle;

    public CellStyle yellowColorStyle() {
        if (yellowColorStyle != null) return yellowColorStyle;

        yellowColorStyle = reader.getWorkbook().createCellStyle();
        yellowColorStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        yellowColorStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return yellowColorStyle;
    }

    private CellStyle redColorStyle;

    public CellStyle redColorStyle() {
        if (redColorStyle != null) return redColorStyle;

        redColorStyle = reader.getWorkbook().createCellStyle();
        redColorStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        redColorStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return redColorStyle;
    }
}
