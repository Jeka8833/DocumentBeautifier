package com.Jeka8833.DocumentBeautifier.mods;

import com.Jeka8833.DocumentBeautifier.ColumnName;
import com.Jeka8833.DocumentBeautifier.excel.ExcelCell;
import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Stream;

public class IPNMod implements Mod {

    public @Nullable ColumnName input = new ColumnName("IPN_IN", "IPN");
    public @Nullable ColumnName output = new ColumnName("IPN_OUT", "IPN");
    public @Nullable ColumnName dateOutput = new ColumnName("IPN_DATE_OUT", "IPN Date");
    public @Nullable ColumnName genderOutput = new ColumnName("IPN_GENDER_OUT", "IPN Gender");
    public @Nullable ColumnName ageOutput = new ColumnName("IPN_AGE_OUT", "IPN Age");

    public @Nullable LocalDate minDate = LocalDate.now().minusYears(125);
    public @Nullable LocalDate maxDate = LocalDate.now().minusDays(1);

    public @Nullable String male = "Male";
    public @Nullable String female = "Female";

    public boolean printFormattingWarning = true;
    public boolean printIPNError = true;

    @Override
    public ColumnName[] getNeededColumn() {
        if (input == null) return new ColumnName[]{};

        return Stream.of(input, output, dateOutput, genderOutput, ageOutput)
                .filter(Objects::nonNull)
                .map(ColumnName::clone)
                .toArray(ColumnName[]::new);
    }

    @Override
    public void process(SheetDetailed sheet, ColumnName column, Cell cell) {
        if (column.equals(input)) {
            String text = formatText(column, ExcelCell.getText(cell));
            if (text.isEmpty()) return;

            if (text.length() != 10) {
                if (printIPNError) cell.setCellStyle(sheet.redColorStyle());
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

            LocalDate age = LocalDate.of(1899, 12, 31)
                    .plusDays(Integer.parseInt(text.substring(0, 5)));

            int[] number = Arrays.stream(text.split("")).mapToInt(Integer::parseInt).toArray();
            int sum = number[0] * (-1) + number[1] * 5 + number[2] * 7 + number[3] * 9 + number[4] * 4 + number[5] * 6 +
                    number[6] * 10 + number[7] * 5 + number[8] * 7;

            boolean isCorrect = ((sum % 11) % 10) == number[9] &&
                    (maxDate == null || maxDate.isAfter(age)) &&
                    (minDate == null || minDate.isBefore(age));

            if (!isCorrect && printIPNError) cell.setCellStyle(sheet.redColorStyle());

            if (sheet.getColumnNames().contains(dateOutput)) {
                int poxX = sheet.getColumnNames().get(dateOutput).getPosX();
                Cell dateCell = ExcelCell.getCell(cell.getRow(), poxX, CellType.STRING);
                dateCell.setCellValue(Date.from(age.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                dateCell.setCellStyle(sheet.dateStyle());
            }

            if (sheet.getColumnNames().contains(genderOutput)) {
                boolean isMale = number[8] % 2 != 0;
                int poxX = sheet.getColumnNames().get(genderOutput).getPosX();
                ExcelCell.writeCell(cell.getRow(), poxX,
                        isMale ? (male == null ? "" : male) : (female == null ? "" : female));
            }

            if (sheet.getColumnNames().contains(ageOutput)) {
                int poxX = sheet.getColumnNames().get(ageOutput).getPosX();
                Cell ageCell = cell.getRow().createCell(poxX, CellType.FORMULA);
                ageCell.setCellFormula("INT(YEARFRAC(DATE(" +
                        age.getYear() + "," + age.getMonthValue() + "," + age.getDayOfMonth() + "),TODAY()))");
            }
        }
    }

    @Override
    public String formatText(ColumnName column, String text) {
        if(!column.equals(input)) return text;
        return text.replaceAll("\\D", "");
    }
}
