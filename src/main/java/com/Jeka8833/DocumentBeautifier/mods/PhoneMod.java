package com.Jeka8833.DocumentBeautifier.mods;

import com.Jeka8833.DocumentBeautifier.ColumnName;
import com.Jeka8833.DocumentBeautifier.excel.ExcelCell;
import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.apache.poi.ss.usermodel.Cell;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Stream;

public class PhoneMod implements Mod {

    private static final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    private @Nullable ColumnName input = new ColumnName("PHONE_IN", "Phone");
    private @Nullable ColumnName output = new ColumnName("PHONE_OUT", "Phone");

    private boolean printFormattingWarning = true;
    private boolean printPhoneError = true;

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
        if (column.equals(input)) {
            String text = formatText(sheet, column, cell);
            if (text.isEmpty()) return;
            try {
                Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(text, "UA");

                if (sheet.getColumnNames().contains(output)) {
                    int poxX = sheet.getColumnNames().get(output).getPosX();
                    ExcelCell.writeCell(cell.getRow(), poxX, text);
                }

                if (printFormattingWarning) {
                    String textWithoutFormatting = ExcelCell.getText(cell);
                    if (!text.equals(textWithoutFormatting)) cell.setCellStyle(sheet.yellowColorStyle());
                }

                if (printPhoneError) {
                    if (!phoneUtil.isValidNumber(swissNumberProto)) cell.setCellStyle(sheet.redColorStyle());
                }
            } catch (NumberParseException ignored) {
                if (printPhoneError) cell.setCellStyle(sheet.redColorStyle());
            }
        }
    }

    @Override
    public String formatText(SheetDetailed sheet, ColumnName column, Cell cell) {
        String text = ExcelCell.getText(cell).replaceAll("\\D", "");
        if (text.isEmpty()) return "";

        try {
            return phoneUtil.format(phoneUtil.parse(text, "UA"), PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        } catch (NumberParseException ignored) {
        }
        return text;
    }
}
