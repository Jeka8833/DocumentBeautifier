package com.Jeka8833.DocumentBeautifier.mods;

import com.Jeka8833.DocumentBeautifier.excel.ExcelCell;
import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import com.Jeka8833.DocumentBeautifier.header.ColumnHeader;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;
import org.apache.poi.ss.usermodel.Cell;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

public class PhoneMod extends Mod {

    private static final PhoneNumberUtil PHONE_UTIL = PhoneNumberUtil.getInstance();
    private static final PhoneNumberOfflineGeocoder OFFLINE_GEOCODER = PhoneNumberOfflineGeocoder.getInstance();
    private static final PhoneNumberToCarrierMapper CARRIER_MAPPER = PhoneNumberToCarrierMapper.getInstance();

    public @Nullable ColumnHeader input = new ColumnHeader("PHONE_IN", "Phone");
    public @Nullable ColumnHeader output = new ColumnHeader("PHONE_OUT", "Phone");
    public @Nullable ColumnHeader typeOutput = new ColumnHeader("PHONE_TYPE_OUT", "Phone type");
    public @Nullable ColumnHeader operatorOutput = new ColumnHeader("PHONE_OPERATOR_OUT", "Phone operator");

    public String state = "UA";
    public PhoneNumberUtil.PhoneNumberFormat numberFormat = PhoneNumberUtil.PhoneNumberFormat.NATIONAL;

    public boolean printFormattingWarning = true;
    public boolean printPhoneError = true;

    @NotNull
    @Override
    public ColumnHeader[] getNeededColumn() {
        if (input == null) return new ColumnHeader[0];

        return Stream.of(input, output, typeOutput, operatorOutput)
                .filter(Objects::nonNull)
                .map(ColumnHeader::clone)
                .toArray(ColumnHeader[]::new);
    }

    @Override
    public void process(@NotNull SheetDetailed sheet, @NotNull ColumnHeader column, @NotNull Cell cell) {
        if (!column.equals(input)) return;

        String text = formatText(column, ExcelCell.getText(cell));
        if (text.isEmpty()) return;
        try {
            Phonenumber.PhoneNumber swissNumberProto = PHONE_UTIL.parse(text, state);

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
                String phoneInfo = CARRIER_MAPPER.getNameForNumber(swissNumberProto, Locale.ENGLISH);
                ExcelCell.writeCell(cell.getRow(), poxX,
                        phoneInfo.isEmpty() ?
                                OFFLINE_GEOCODER.getDescriptionForNumber(swissNumberProto, Locale.ENGLISH) : phoneInfo);
            }
        } catch (NumberParseException ignored) {
            if (printPhoneError) cell.setCellStyle(sheet.redColorStyle());
        }
    }

    @NotNull
    @Override
    public String formatText(@NotNull ColumnHeader column, @NotNull String text) {
        if (!column.equals(input)) return text;

        text = text.replaceAll("[^0-9]+", "");
        if (text.isEmpty()) return "";

        try {
            return PHONE_UTIL.format(PHONE_UTIL.parse(text, state), numberFormat);
        } catch (NumberParseException ignored) {
        }
        return text;
    }

    @Override
    public boolean isValid(@NotNull String text) {
        try {
            return PHONE_UTIL.isValidNumber(PHONE_UTIL.parse(text, state));
        } catch (Exception ignore) {
            return false;
        }
    }
}
