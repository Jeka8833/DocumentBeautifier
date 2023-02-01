package com.Jeka8833.DocumentBeautifier.mods;

import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import com.Jeka8833.DocumentBeautifier.header.ColumnHeader;
import org.apache.poi.ss.usermodel.Cell;
import org.jetbrains.annotations.NotNull;

public interface Mod extends Cloneable {

    @NotNull ColumnHeader[] getNeededColumn();

    void process(@NotNull SheetDetailed sheet, @NotNull ColumnHeader column, @NotNull Cell cell);

    @NotNull String formatText(@NotNull ColumnHeader column, @NotNull String text);

    @NotNull Mod setParameters(@NotNull String param);

    boolean isValid(@NotNull String text);
}
