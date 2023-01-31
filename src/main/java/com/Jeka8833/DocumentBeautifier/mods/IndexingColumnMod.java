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

    @Override
    public ColumnHeader[] getNeededColumn() {
        return columnHeaders;
    }

    @Override
    public void process(SheetDetailed sheet, ColumnHeader column, Cell cell) {
    }

    @Override
    public String formatText(ColumnHeader column, String text) {
        return text;
    }

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
}
