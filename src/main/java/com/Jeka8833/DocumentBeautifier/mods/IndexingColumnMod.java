package com.Jeka8833.DocumentBeautifier.mods;

import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import com.Jeka8833.DocumentBeautifier.header.ColumnHeader;
import org.apache.poi.ss.usermodel.Cell;
import org.jetbrains.annotations.NotNull;

public class IndexingColumnMod extends Mod {

    private final ColumnHeader[] columnHeaders;

    public IndexingColumnMod(ColumnHeader[] columnHeaders) {
        this.columnHeaders = columnHeaders;
    }

    @NotNull
    @Override
    public ColumnHeader[] getNeededColumn() {
        return columnHeaders;
    }

    @Override
    public void process(@NotNull SheetDetailed sheet, @NotNull ColumnHeader column, @NotNull Cell cell) {
    }

    @NotNull
    @Override
    public String formatText(@NotNull ColumnHeader column, @NotNull String text) {
        return text;
    }

    @NotNull
    @Override
    public Mod setParameters(@NotNull String param) {
        return this;
    }
}
