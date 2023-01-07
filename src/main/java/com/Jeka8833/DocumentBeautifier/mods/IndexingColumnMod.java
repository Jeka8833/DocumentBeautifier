package com.Jeka8833.DocumentBeautifier.mods;

import com.Jeka8833.DocumentBeautifier.ColumnName;
import com.Jeka8833.DocumentBeautifier.excel.ExcelCell;
import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import org.apache.poi.ss.usermodel.Cell;

public class IndexingColumnMod implements Mod {

    private final ColumnName[] columnNames;

    public IndexingColumnMod(ColumnName[] columnNames) {
        this.columnNames = columnNames;
    }

    @Override
    public ColumnName[] getNeededColumn() {
        return columnNames;
    }

    @Override
    public void process(SheetDetailed sheet, ColumnName column, Cell cell) {
    }

    @Override
    public String formatText(SheetDetailed sheet, ColumnName column, Cell cell) {
        return ExcelCell.getText(cell);
    }
}
