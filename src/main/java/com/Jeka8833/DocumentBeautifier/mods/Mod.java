package com.Jeka8833.DocumentBeautifier.mods;

import com.Jeka8833.DocumentBeautifier.ColumnName;
import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import com.Jeka8833.DocumentBeautifier.util.MySet;
import org.apache.poi.ss.usermodel.Cell;

public interface Mod {

    ColumnName[] getNeededColumn();

    void process(SheetDetailed sheet, ColumnName column, Cell cell);

    String formatText(SheetDetailed sheet, ColumnName column, Cell cell);

}
