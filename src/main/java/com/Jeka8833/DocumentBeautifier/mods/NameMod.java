package com.Jeka8833.DocumentBeautifier.mods;

import com.Jeka8833.DocumentBeautifier.header.ColumnHeader;
import com.Jeka8833.DocumentBeautifier.excel.ExcelCell;
import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import com.Jeka8833.DocumentBeautifier.header.ColumnParser;
import com.Jeka8833.DocumentBeautifier.util.LevenshteinDistance;
import com.Jeka8833.DocumentBeautifier.util.Util;
import org.apache.poi.ss.usermodel.Cell;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Stream;

public class NameMod implements Mod {
    public @Nullable ColumnHeader input = new ColumnHeader("NAME_IN", "Name");
    public @Nullable ColumnHeader output = new ColumnHeader("NAME_OUT", "Name");

    public boolean printFormattingWarning = true;

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

        text = Util.replaceEnglish(text)
                .replace('3', 'з')
                .toLowerCase()
                .replaceAll("[^а-яёіїєґщ’\\-.,\\s]+", "").strip();

        if (text.isEmpty()) return "";
        String[] partName = text.split("[.,\\s]+");
        for (int i = 0; i < partName.length; i++) {
            String part = partName[i];
            partName[i] = Character.toUpperCase(part.charAt(0)) +
                    (part.length() > 1 ? part.substring(1) : ".");
        }
        return String.join(" ", partName);
    }

    @Override
    public Mod setParameters(@NotNull String param) {
        if (param.length() >= 7) {
            try {
                return ColumnParser.updateModParameter((NameMod) super.clone(), param);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return this;
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
