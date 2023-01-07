package com.Jeka8833.DocumentBeautifier.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;

public class ExcelCell {

    public static Cell getCell(Row row, int column, CellType cellType) {
        Cell cell = row.getCell(column);
        if (cell != null) return cell;

        return row.createCell(column, cellType);
    }

    public static String readCell(Row row, int column) {
        return getText(row.getCell(column));
    }

    public static String getText(Cell cell) {
        if (cell == null) return "";

        if (cell.getCellType() == CellType.NUMERIC) {
            return NumberToTextConverter.toText(cell.getNumericCellValue());
        } else if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        }
        return "";
    }

    public static Cell writeCell(Row row, int column, String text) {
        Cell cell = row.createCell(column, CellType.STRING);
        cell.setCellValue(text);
        return cell;
    }
}
