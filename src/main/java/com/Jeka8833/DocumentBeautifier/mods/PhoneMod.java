package com.Jeka8833.DocumentBeautifier.mods;

import com.Jeka8833.DocumentBeautifier.ColumnName;
import com.Jeka8833.DocumentBeautifier.excel.ExcelCell;
import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.apache.poi.ss.usermodel.Cell;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

public class PhoneMod implements Mod {

    private static final PhoneNumberUtil PHONE_UTIL = PhoneNumberUtil.getInstance();
    private static final PhoneNumberToCarrierMapper CARRIER_MAPPER = PhoneNumberToCarrierMapper.getInstance();

    public @Nullable ColumnName input = new ColumnName("PHONE_IN", "Phone");
    public @Nullable ColumnName output = new ColumnName("PHONE_OUT", "Phone");
    public @Nullable ColumnName typeOutput = new ColumnName("PHONE_TYPE_OUT", "Phone type");
    public @Nullable ColumnName operatorOutput = new ColumnName("PHONE_OPERATOR_OUT", "Phone operator");

    public String region = "UA";
    public PhoneNumberUtil.PhoneNumberFormat numberFormat = PhoneNumberUtil.PhoneNumberFormat.NATIONAL;

    public boolean printFormattingWarning = true;
    public boolean printPhoneError = true;

    @Override
    public ColumnName[] getNeededColumn() {
        if (input == null) return new ColumnName[0];

        return Stream.of(input, output, typeOutput, operatorOutput)
                .filter(Objects::nonNull)
                .map(ColumnName::clone)
                .toArray(ColumnName[]::new);
    }

    @Override
    public void process(SheetDetailed sheet, ColumnName column, Cell cell) {
        if (!column.equals(input)) return;

        String text = formatText(column, ExcelCell.getText(cell));
        if (text.isEmpty()) return;
        try {
            Phonenumber.PhoneNumber swissNumberProto = PHONE_UTIL.parse(text, region);

            if (sheet.getColumnNames().contains(output)) {
                int poxX = sheet.getColumnNames().get(output).getPosX();
                ExcelCell.writeCell(cell.getRow(), poxX, text);
            }

            if (printFormattingWarning) {
                String textWithoutFormatting = ExcelCell.getText(cell);
                if (!text.equals(textWithoutFormatting)) cell.setCellStyle(sheet.yellowColorStyle());
            }

            if (printPhoneError) {
                if (!PHONE_UTIL.isValidNumber(swissNumberProto)) cell.setCellStyle(sheet.redColorStyle());
            }

            if (sheet.getColumnNames().contains(typeOutput)) {
                int poxX = sheet.getColumnNames().get(typeOutput).getPosX();
                ExcelCell.writeCell(cell.getRow(), poxX, PHONE_UTIL.getNumberType(swissNumberProto).toString());
            }

            if (sheet.getColumnNames().contains(operatorOutput)) {
                int poxX = sheet.getColumnNames().get(operatorOutput).getPosX();
                ExcelCell.writeCell(cell.getRow(), poxX,
                        CARRIER_MAPPER.getNameForNumber(swissNumberProto, Locale.ENGLISH));
            }
        } catch (NumberParseException ignored) {
            if (printPhoneError) cell.setCellStyle(sheet.redColorStyle());
        }
    }

    @Override
    public String formatText(ColumnName column, String text) {
        if (!column.equals(input)) return text;

        text = text.replaceAll("\\D+", "");
        if (text.isEmpty()) return "";

        try {
            return PHONE_UTIL.format(PHONE_UTIL.parse(text, region), numberFormat);
        } catch (NumberParseException ignored) {
        }
        return text;
    }
}
