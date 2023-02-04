package com.Jeka8833.DocumentBeautifier.mods;

import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import com.Jeka8833.DocumentBeautifier.header.ColumnHeader;
import com.Jeka8833.DocumentBeautifier.header.ColumnParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.jetbrains.annotations.NotNull;

public abstract class Mod implements Cloneable {

    protected static final Logger LOGGER = LogManager.getLogger(Mod.class);

    @NotNull
    public abstract ColumnHeader[] getNeededColumn();

    public abstract void process(@NotNull SheetDetailed sheet, @NotNull ColumnHeader column, @NotNull Cell cell);

    @NotNull
    public abstract String formatText(@NotNull ColumnHeader column, @NotNull String text);

    @NotNull
    public Mod setParameters(@NotNull String param) {
        if (param.length() >= 7) {
            try {
                return ColumnParser.updateModParameter((Mod) super.clone(), param);
            } catch (CloneNotSupportedException e) {
                LOGGER.warn("Clone operation has a error", e);
            }
        }
        return this;
    }

    public boolean isValid(@NotNull String text) {
        return true;
    }
}
