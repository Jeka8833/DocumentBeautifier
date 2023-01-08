package com.Jeka8833.DocumentBeautifier.mods;

import com.Jeka8833.DocumentBeautifier.ColumnName;
import com.Jeka8833.DocumentBeautifier.excel.ExcelCell;
import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import org.apache.poi.ss.usermodel.Cell;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Stream;

public class AddressMod implements Mod {
    public @Nullable ColumnName input = new ColumnName("ADDRESS_IN", "Address");
    public @Nullable ColumnName output = new ColumnName("ADDRESS_OUT", "Address");

    public boolean printFormattingWarning = true;

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
            String text = formatText(column, ExcelCell.getText(cell));
            if (text.isEmpty()) return;

            if (sheet.getColumnNames().contains(output)) {
                int poxX = sheet.getColumnNames().get(output).getPosX();
                ExcelCell.writeCell(cell.getRow(), poxX, text);
            }

            if (printFormattingWarning) {
                String textWithoutFormatting = ExcelCell.getText(cell);
                if (!text.equals(textWithoutFormatting)) cell.setCellStyle(sheet.yellowColorStyle());
            }
        }
    }

    @Override
    public String formatText(ColumnName column, String text) {
        if(!column.equals(input)) return text;
        text = text.strip().replaceAll(" {2,}", " ")
                .toLowerCase();

        if (text.isEmpty()) return "";

        String[] partName = text.split("[.,\\s]+");
        StringBuilder stringBuilder = new StringBuilder(formatFirstPart(partName[0]));

        int index = findIndexPart(partName);
        for (int i = 1; i < partName.length; i++) {
            String part = partName[i];
            if (i > index) {
                stringBuilder.append(part);
            } else {
                stringBuilder.append(Character.toUpperCase(part.charAt(0)))
                        .append(part.length() > 1 ? part.substring(1) : ".");
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

    private static int findIndexPart(String[] parts) {
        for (int i = parts.length - 1; i > 0; i--) {
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
