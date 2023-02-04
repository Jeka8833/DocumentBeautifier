package com.Jeka8833.DocumentBeautifier.mods;

import com.Jeka8833.DocumentBeautifier.excel.ExcelCell;
import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import com.Jeka8833.DocumentBeautifier.header.ColumnHeader;
import org.apache.poi.ss.usermodel.Cell;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CombineIPNPassportMod extends Mod {

    public @Nullable ColumnHeader input = new ColumnHeader("IPN_PASS_CMB");
    private final @Nullable IPNMod ipnMod;
    private final @Nullable PassportMod passportMod;

    public CombineIPNPassportMod(@Nullable IPNMod ipnMod, @Nullable PassportMod passportMod) {
        this.ipnMod = ipnMod;
        this.passportMod = passportMod;
    }

    @NotNull
    @Override
    public ColumnHeader[] getNeededColumn() {
        if (input == null) return new ColumnHeader[0];
        return new ColumnHeader[]{input};
    }

    @Override
    public void process(@NotNull SheetDetailed sheet, @NotNull ColumnHeader column, @NotNull Cell cell) {
        if (!column.equals(input)) return;

        String text = ExcelCell.getText(cell);
        if (passportMod != null && passportMod.input != null && isPassportValid(text)) {
            passportMod.process(sheet, passportMod.input, cell);
        } else if (ipnMod != null && ipnMod.input != null) {
            ipnMod.process(sheet, ipnMod.input, cell);
        }
    }

    @NotNull
    @Override
    public String formatText(@NotNull ColumnHeader column, @NotNull String text) {
        if (passportMod != null && isPassportValid(text)) return passportMod.formatText(column, text);
        if (ipnMod != null) return ipnMod.formatText(column, text);
        return text;
    }


    @Override
    public boolean isValid(@NotNull String text) {
        return isIPNValid(text) || isPassportValid(text);
    }

    @NotNull
    @Override
    public Mod setParameters(@NotNull String param) {
        return this;
    }

    public boolean isPassportValid(@NotNull String text) {
        return passportMod != null && passportMod.input != null &&
                passportMod.isValid(passportMod.formatText(passportMod.input, text));
    }

    public boolean isIPNValid(@NotNull String text) {
        return ipnMod != null && ipnMod.input != null && ipnMod.isValid(ipnMod.formatText(ipnMod.input, text));
    }

    public boolean inSkipMod(Mod mod) {
        return mod instanceof PassportMod || mod instanceof IPNMod;
    }
}
