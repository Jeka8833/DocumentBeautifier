package com.Jeka8833.DocumentBeautifier.mods;

import com.Jeka8833.DocumentBeautifier.excel.ExcelCell;
import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import com.Jeka8833.DocumentBeautifier.header.ColumnHeader;
import com.Jeka8833.DocumentBeautifier.util.Util;
import org.apache.poi.ss.usermodel.Cell;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Stream;

public class AddressMod extends Mod {
    public @Nullable ColumnHeader input = new ColumnHeader("ADDRESS_IN", "Address");
    public @Nullable ColumnHeader output = new ColumnHeader("ADDRESS_OUT", "Address");

    public boolean printFormattingWarning = true;
    public String replaceStreetType = "";

    @NotNull
    @Override
    public ColumnHeader[] getNeededColumn() {
        if (input == null) return new ColumnHeader[0];

        return Stream.of(input, output)
                .filter(Objects::nonNull)
                .map(ColumnHeader::clone)
                .toArray(ColumnHeader[]::new);
    }

    @Override
    public void process(@NotNull SheetDetailed sheet, @NotNull ColumnHeader column, @NotNull Cell cell) {
        if (!column.equals(input)) return;

        boolean containsOutputField = sheet.getColumnNames().contains(output);
        if (!containsOutputField && !printFormattingWarning) return;

        String text = ExcelCell.getText(cell);
        String textFormatted = formatText(column, text);

        if (!text.equals(textFormatted)) {
            if (printFormattingWarning) cell.setCellStyle(sheet.yellowColorStyle());

            if (containsOutputField) {
                int poxX = sheet.getColumnNames().get(output).getPosX();
                ExcelCell.writeCell(cell.getRow(), poxX, textFormatted);
            }
        }
    }

    @NotNull
    @Override
    public String formatText(@NotNull ColumnHeader column, @NotNull String text) {
        if (!column.equals(input)) return text;

        text = Util.replaceEnglish(text)
                .toLowerCase()
                .replaceAll("[^0-9а-яёіїєґщ/\\\\№#’\\-.,\\s]+", "")
                .strip();

        if (text.isEmpty()) return "";

        String[] partName = text.split("[.,\\s]+");
        StringBuilder stringBuilder = new StringBuilder();
        if (replaceStreetType.isEmpty()) {
            stringBuilder.append(formatFirstPart(partName[0]));
        } else {
            stringBuilder.append(replaceStreetType);
        }

        int index = findIndexPart(partName, !replaceStreetType.isEmpty());
        for (int i = !replaceStreetType.isEmpty() ? 0 : 1; i < partName.length; i++) {
            String part = partName[i];
            if (i > index) {
                stringBuilder.append(part);
            } else {
                stringBuilder.append(Character.toUpperCase(part.charAt(0)))
                        .append(part.length() > 1 ? part.substring(1) :
                                (Character.isDigit(part.charAt(0)) ? "" : "."));
            }
            if (index == i) {
                stringBuilder.append(", ");
            } else {
                stringBuilder.append(' ');
            }
        }

        return stringBuilder.toString().stripTrailing();
    }

    private static String formatFirstPart(String text) {
        return text.replaceAll("(ву|ул).*", "вул.")
                .replaceAll("п.*", "пров.")
                .replaceAll("(в`|в'|вї|вь|въ|в’).*", "в'їзд ")
                .replaceAll("б.*", "б-р ")
                .replaceAll("т.*", "тупик ");
    }

    private static int findIndexPart(@NotNull String[] parts, boolean includeFirst) {
        final int endIndex = includeFirst ? 0 : 1;
        for (int i = parts.length - 1; i >= endIndex; i--) {
            if (!hasNumber(parts[i])) return i;
        }
        return parts.length;
    }

    private static boolean hasNumber(String text) {
        for (char c : text.toCharArray()) {
            if (Character.isDigit(c) || c == '/') return true;
        }
        return false;
    }
}
