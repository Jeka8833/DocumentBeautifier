package com.Jeka8833.DocumentBeautifier.mods;

import com.Jeka8833.DocumentBeautifier.excel.ExcelCell;
import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import com.Jeka8833.DocumentBeautifier.header.ColumnHeader;
import com.Jeka8833.DocumentBeautifier.header.ColumnParser;
import org.apache.poi.ss.usermodel.Cell;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CombineIPNPassportMod implements Mod {

    public @Nullable ColumnHeader input = new ColumnHeader("IPN_PASS_CMB");
    private final @Nullable IPNMod ipnMod;
    private final @Nullable PassportMod passportMod;

    public CombineIPNPassportMod() {
        this(null, null);
    }

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
        if (ipnMod != null && ipnMod.input != null && isIPNValid(text)) {
            ipnMod.process(sheet, ipnMod.input, cell);
        } else if (passportMod != null && passportMod.input != null) {
            passportMod.process(sheet, passportMod.input, cell);
        }
    }

    @NotNull
    @Override
    public String formatText(@NotNull ColumnHeader column, @NotNull String text) {
        if (ipnMod != null && isIPNValid(text)) return ipnMod.formatText(column, text);
        if (passportMod != null) return passportMod.formatText(column, text);
        return text;
    }

    @NotNull
    @Override
    public Mod setParameters(@NotNull String param) {
        if (param.length() >= 7) {
            try {
                return ColumnParser.updateModParameter((CombineIPNPassportMod) super.clone(), param);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    @Override
    public boolean isValid(@NotNull String text) {
        return isIPNValid(text) || isPassportValid(text);
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
