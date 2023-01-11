package com.Jeka8833.DocumentBeautifier.mods;

import com.Jeka8833.DocumentBeautifier.ColumnName;
import com.Jeka8833.DocumentBeautifier.excel.ExcelCell;
import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import com.Jeka8833.DocumentBeautifier.util.LevenshteinDistance;
import org.apache.poi.ss.usermodel.Cell;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class NameMod implements Mod {
    public @Nullable ColumnName input = new ColumnName("NAME_IN", "Name");
    public @Nullable ColumnName output = new ColumnName("NAME_OUT", "Name");

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
        if (!column.equals(input)) return;

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

    @Override
    public String formatText(ColumnName column, String text) {
        if (!column.equals(input)) return text;

        text = text.strip().replaceAll(" {2,}", " ")
                .toLowerCase();

        if (text.isEmpty()) return "";
        String[] partName = text.split("[.,\\s]+");
        for (int i = 0; i < partName.length; i++) {
            String part = partName[i];
            partName[i] = Character.toUpperCase(part.charAt(0)) +
                    (part.length() > 1 ? part.substring(1) : ".");
        }
        return String.join(" ", partName);
    }

    public static boolean compareName(String name1, String name2, int mistakes) {
        String[] name1Array = name1.split("[.,\\s]+");
        String[] name2Array = name2.split("[.,\\s]+");

        if (name1Array.length != name2Array.length) return false;

        for (int i = 0; i < name1Array.length; i++) {
            if (name1Array[i].length() == 1 || name2Array[i].length() == 1) {
                if (Character.toLowerCase(name1Array[i].charAt(0)) != Character.toLowerCase(name2Array[i].charAt(0)))
                    return false;
            } else {
                int value = LevenshteinDistance.limitedCompare(name1Array[i], name2Array[i], mistakes);
                if (value == -1) return false;
                mistakes -= value;
            }
        }
        return true;
    }
}
