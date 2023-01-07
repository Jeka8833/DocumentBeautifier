package com.Jeka8833.DocumentBeautifier.mods;

import com.Jeka8833.DocumentBeautifier.ColumnName;
import com.Jeka8833.DocumentBeautifier.excel.ExcelCell;
import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import org.apache.poi.ss.usermodel.Cell;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Stream;

public class NameMod implements Mod {
    private @Nullable ColumnName input = new ColumnName("NAME_IN", "Name");
    private @Nullable ColumnName output = new ColumnName("NAME_OUT", "Name");

    private boolean printFormattingWarning = true;

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
    public String formatText(SheetDetailed sheet, ColumnName column, Cell cell) {
        String text = ExcelCell.getText(cell)
                .strip()
                .toLowerCase();

        if (text.isEmpty()) return "";
        String[] partName = text.split("[.,\\s-]+");
        for (int i = 0; i < partName.length; i++) {
            String part = partName[i];
            partName[i] = Character.toUpperCase(part.charAt(0)) +
                    (part.length() > 1 ? part.substring(1) : ".");
        }
        return String.join(" ", partName);
    }
}
