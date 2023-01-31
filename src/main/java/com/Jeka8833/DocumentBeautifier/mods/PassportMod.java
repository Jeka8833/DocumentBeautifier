package com.Jeka8833.DocumentBeautifier.mods;

import com.Jeka8833.DocumentBeautifier.header.ColumnHeader;
import com.Jeka8833.DocumentBeautifier.excel.ExcelCell;
import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import com.Jeka8833.DocumentBeautifier.header.ColumnParser;
import com.Jeka8833.DocumentBeautifier.util.Util;
import org.apache.poi.ss.usermodel.Cell;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Stream;

public class PassportMod implements Mod {

    public @Nullable ColumnHeader input = new ColumnHeader("PASSPORT_IN", "Passport");
    public @Nullable ColumnHeader output = new ColumnHeader("PASSPORT_OUT", "Passport");

    public boolean printFormattingWarning = true;
    public boolean printPassportError = true;

    @Override
    public ColumnHeader[] getNeededColumn() {
        if (input == null) return new ColumnHeader[0];

        return Stream.of(input, output)
                .filter(Objects::nonNull)
                .map(ColumnHeader::clone)
                .toArray(ColumnHeader[]::new);
    }

    @Override
    public void process(SheetDetailed sheet, ColumnHeader column, Cell cell) {
        if (!column.equals(input)) return;

        boolean containsOutputField = sheet.getColumnNames().contains(output);
        if (!containsOutputField && !printFormattingWarning) return;

        String text = ExcelCell.getText(cell);
        String textFormatted = formatText(column, text);

        if (!isPassportCode(textFormatted)) {
            if (printPassportError)
                cell.setCellStyle(sheet.redColorStyle());
            return;
        }

        if (!text.equals(textFormatted)) {
            if (printFormattingWarning) cell.setCellStyle(sheet.yellowColorStyle());

            if (containsOutputField) {
                int poxX = sheet.getColumnNames().get(output).getPosX();
                ExcelCell.writeCell(cell.getRow(), poxX, textFormatted);
            }
        }
    }

    @Override
    public String formatText(ColumnHeader column, String text) {
        if (!column.equals(input)) return text;

        return Util.replaceEnglish(text).toUpperCase().replaceAll("[^А-Я0-9ІЇЄЩҐЁ]+", "");
    }

    @Override
    public Mod setParameters(@NotNull String param) {
        if (param.length() >= 7) {
            try {
                return ColumnParser.updateModParameter((PassportMod) super.clone(), param);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    private static boolean isPassportCode(String text) {
        return text.matches("[А-ЯІЇЄЩҐЁ]{2}[0-9]{6}") || text.matches("[0-9]{9}");
    }
}
