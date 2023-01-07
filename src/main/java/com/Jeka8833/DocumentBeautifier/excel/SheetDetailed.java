package com.Jeka8833.DocumentBeautifier.excel;

import com.Jeka8833.DocumentBeautifier.ColumnName;
import com.Jeka8833.DocumentBeautifier.mods.Mod;
import com.Jeka8833.DocumentBeautifier.util.MySet;
import org.apache.poi.ss.usermodel.*;

import java.util.*;

public class SheetDetailed {

    private final Sheet sheet;
    private final ExcelReader reader;
    private final MySet<ColumnName> columnNames = new MySet<>();

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

    public void setColumnNames(Collection<ColumnName> columnNames) {
        this.columnNames.clear();
        this.columnNames.addAll(columnNames);
    }

    public void readColumnNames(Collection<Mod> mods) {
        columnNames.clear();

        ColumnName[] names = mods.stream()
                .flatMap(mod -> Arrays.stream(mod.getNeededColumn()))
                .toArray(ColumnName[]::new);

        boolean stop = false;
        for (Row row : sheet) {
            if (stop) return;

            for (Cell cell : row) {
                if (cell.getCellType() != CellType.STRING) continue;

                String[] columnBlocks = cell.getStringCellValue().split(";");
                for (String block : columnBlocks) {
                    for (ColumnName name : names) {
                        if (name.getColumnIndex().equalsIgnoreCase(block.strip())) {
                            name.setPosX(cell.getColumnIndex());
                            startPosY = cell.getRowIndex() + 1;
                            columnNames.add(name);
                            stop = true;
                        }
                    }
                }
            }
        }
    }

    public MySet<ColumnName> getColumnNames() {
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
