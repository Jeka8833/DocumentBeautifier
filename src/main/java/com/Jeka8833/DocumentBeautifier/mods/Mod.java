package com.Jeka8833.DocumentBeautifier.mods;

import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import com.Jeka8833.DocumentBeautifier.header.ColumnHeader;
import org.apache.poi.ss.usermodel.Cell;
import org.jetbrains.annotations.NotNull;

public interface Mod extends Cloneable {

    ColumnHeader[] getNeededColumn();

    void process(SheetDetailed sheet, ColumnHeader column, Cell cell);

    String formatText(ColumnHeader column, String text);

    Mod setParameters(@NotNull String param);
}
