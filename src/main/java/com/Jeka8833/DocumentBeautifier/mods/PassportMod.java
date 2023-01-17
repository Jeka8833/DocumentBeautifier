package com.Jeka8833.DocumentBeautifier.mods;

import com.Jeka8833.DocumentBeautifier.ColumnName;
import com.Jeka8833.DocumentBeautifier.excel.ExcelCell;
import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import com.Jeka8833.DocumentBeautifier.util.Util;
import org.apache.poi.ss.usermodel.Cell;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Stream;

public class PassportMod implements Mod {

    public @Nullable ColumnName input = new ColumnName("PASSPORT_IN", "Passport");
    public @Nullable ColumnName output = new ColumnName("PASSPORT_OUT", "Passport");

    public boolean printFormattingWarning = true;
    public boolean printPassportError = true;

    @Override
    public ColumnName[] getNeededColumn() {
        if (input == null) return new ColumnName[0];

        return Stream.of(input, output)
                .filter(Objects::nonNull)
                .map(ColumnName::clone)
                .toArray(ColumnName[]::new);
    }

    @Override
    public void process(SheetDetailed sheet, ColumnName column, Cell cell) {
        if (!column.equals(input)) return;

        String text = formatText(column, ExcelCell.getText(cell));
        if (text.isEmpty()) return;

        if (!isPassportCode(text)) {
            if (printPassportError) cell.setCellStyle(sheet.redColorStyle());
            return;
        }

        if (sheet.getColumnNames().contains(output)) {
            int poxX = sheet.getColumnNames().get(output).getPosX();
            ExcelCell.writeCell(cell.getRow(), poxX, text);
        }

        if (printFormattingWarning) {
            String textWithoutFormatting = ExcelCell.getText(cell);
            if (!text.equals(textWithoutFormatting)) cell.setCellStyle(sheet.yellowColorStyle());
        }
    }

    @Override
    public String formatText(ColumnName column, String text) {
        if (!column.equals(input)) return text;

        return Util.replaceEnglish(text.toUpperCase()).replaceAll("[^А-Я0-9ІЇ]+", "");
    }

    private static boolean isPassportCode(String text) {
        return text.matches("[А-ЯІЇ]{2}[0-9]{6}") || text.matches("[0-9]{9}");
    }
}
