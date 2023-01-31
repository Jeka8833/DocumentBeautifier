package com.Jeka8833.DocumentBeautifier.mods;

import com.Jeka8833.DocumentBeautifier.header.ColumnHeader;
import com.Jeka8833.DocumentBeautifier.excel.ExcelCell;
import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import com.Jeka8833.DocumentBeautifier.header.ColumnParser;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.apache.poi.ss.usermodel.Cell;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

public class PhoneMod implements Mod {

    private static final PhoneNumberUtil PHONE_UTIL = PhoneNumberUtil.getInstance();
    private static final PhoneNumberToCarrierMapper CARRIER_MAPPER = PhoneNumberToCarrierMapper.getInstance();

    public @Nullable ColumnHeader input = new ColumnHeader("PHONE_IN", "Phone");
    public @Nullable ColumnHeader output = new ColumnHeader("PHONE_OUT", "Phone");
    public @Nullable ColumnHeader typeOutput = new ColumnHeader("PHONE_TYPE_OUT", "Phone type");
    public @Nullable ColumnHeader operatorOutput = new ColumnHeader("PHONE_OPERATOR_OUT", "Phone operator");

    public String region = "UA";
    public PhoneNumberUtil.PhoneNumberFormat numberFormat = PhoneNumberUtil.PhoneNumberFormat.NATIONAL;

    public boolean printFormattingWarning = true;
    public boolean printPhoneError = true;

    @Override
    public ColumnHeader[] getNeededColumn() {
        if (input == null) return new ColumnHeader[0];

        return Stream.of(input, output, typeOutput, operatorOutput)
                .filter(Objects::nonNull)
                .map(ColumnHeader::clone)
                .toArray(ColumnHeader[]::new);
    }

    @Override
    public void process(SheetDetailed sheet, ColumnHeader column, Cell cell) {
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
    public String formatText(ColumnHeader column, String text) {
        if (!column.equals(input)) return text;

        text = text.replaceAll("[^0-9]+", "");
        if (text.isEmpty()) return "";

        try {
            return PHONE_UTIL.format(PHONE_UTIL.parse(text, region), numberFormat);
        } catch (NumberParseException ignored) {
        }
        return text;
    }

    @Override
    public Mod setParameters(@NotNull String param) {
        if (param.length() >= 7) {
            try {
                return ColumnParser.updateModParameter((PhoneMod) super.clone(), param);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return this;
    }
}
