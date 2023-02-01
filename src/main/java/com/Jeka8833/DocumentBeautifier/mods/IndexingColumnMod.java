package com.Jeka8833.DocumentBeautifier.mods;

import com.Jeka8833.DocumentBeautifier.header.ColumnHeader;
import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import com.Jeka8833.DocumentBeautifier.header.ColumnParser;
import org.apache.poi.ss.usermodel.Cell;
import org.jetbrains.annotations.NotNull;

public class IndexingColumnMod implements Mod {

    private final ColumnHeader[] columnHeaders;

    public IndexingColumnMod() {
        this(new ColumnHeader[0]);
    }

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
        if (param.length() >= 7) {
            try {
                return ColumnParser.updateModParameter((IndexingColumnMod) super.clone(), param);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    @Override
    public boolean isValid(@NotNull String text) {
        return true;
    }
}
