package com.Jeka8833.DocumentBeautifier.mods.search;

import com.Jeka8833.DocumentBeautifier.ColumnName;
import com.Jeka8833.DocumentBeautifier.excel.ExcelCell;
import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import com.Jeka8833.DocumentBeautifier.mods.Mod;
import com.Jeka8833.DocumentBeautifier.util.MySet;
import org.apache.poi.ss.usermodel.Cell;

public class SearchMod implements Mod {

    private final MySet<ColumnName> names;
    private final SearchDB searchDB;

    public SearchMod(MySet<ColumnName> names, SearchDB searchDB) {
        this.names = names;
        this.searchDB = searchDB;
    }

    @Override
    public ColumnName[] getNeededColumn() {
        return names.toArray(ColumnName[]::new);
    }

    @Override
    public void process(SheetDetailed sheet, ColumnName column, Cell cell) {
        if (!names.contains(column)) return;

        String text = ExcelCell.getText(cell);
        if (text.isBlank()) return;

        searchDB.add(sheet, column, cell, text);
    }

    @Override
    public String formatText(SheetDetailed sheet, ColumnName column, Cell cell) {
        return ExcelCell.getText(cell);
    }
}
