package com.Jeka8833.DocumentBeautifier.search;

import com.Jeka8833.DocumentBeautifier.header.ColumnHeader;
import com.Jeka8833.DocumentBeautifier.excel.ExcelCell;
import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import com.Jeka8833.DocumentBeautifier.mods.Mod;
import com.Jeka8833.DocumentBeautifier.util.TriFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleSearcher {
    private static final Logger logger = LogManager.getLogger(ConsoleSearcher.class);

    public static void start(SearchDB searchDB, ColumnHeader[] printingColumn, SearchFilter... filters) {
        Map<String, SearchedColumn> listMap = new HashMap<>();
        for (SearchFilter filter : filters) {
            List<DBElement> rowList = searchDB.getColumn(filter.column());
            if (rowList != null)
                listMap.put(filter.shortName,
                        new SearchedColumn(rowList, filter.formatted(), filter.column(), filter.filter()));
        }
        Scanner scanner = new Scanner(System.in);
        while (true) {
            logger.info("Search: ");
            String text = scanner.nextLine().strip();
            if (text.contains("exit")) return;

            String[] input = text.split(" ", 3);
            if (input.length < 3) {
                logger.info("Example: [" + String.join(",", listMap.keySet()) + "] <Mistake count 0-99> <Text>");
                continue;
            }

            SearchedColumn searchedColumn = listMap.get(input[0].toLowerCase());
            if (searchedColumn == null) {
                logger.info("Column not found: [" + String.join(",", listMap.keySet()) + "]");
                continue;
            }

            try {
                int mistake = Integer.parseInt(input[1]);

                String formattedText = searchedColumn.mod().formatText(searchedColumn.column(), input[2]);

                logger.info("Searching: " + formattedText + " Mistake: " + mistake);
                for (DBElement rowData : searchedColumn.rowList()) {
                    if (searchedColumn.filter().apply(rowData.element(), formattedText, mistake)) {
                        StringBuilder builder = new StringBuilder(rowData.sheet().getReader().getInputFile()
                                + " > " + rowData.sheet().getSheet().getSheetName() + ": ");

                        SheetDetailed sheet = rowData.sheet();
                        for (ColumnHeader name : printingColumn) {
                            if (sheet.getColumnNames().contains(name)) {
                                ColumnHeader used = sheet.getColumnNames().get(name);
                                if (used.getPosX() >= 0) {
                                    Cell cell = rowData.cell().getRow().getCell(used.getPosX());
                                    if (cell != null) {
                                        builder.append(used.getDisplayName()).append(": ")
                                                .append(ExcelCell.getText(cell)).append("; ");
                                    }
                                }
                            }
                        }
                        logger.info(builder);
                    }
                }
                logger.info("Search ended");
            } catch (Exception exception) {
                logger.error("Fail parse mistake number", exception);
            }
        }
    }

    public record SearchFilter(String shortName, Mod formatted, ColumnHeader column,
                               TriFunction<String, String, Integer, Boolean> filter) {
    }

    private record SearchedColumn(List<DBElement> rowList, Mod mod, ColumnHeader column,
                                  TriFunction<String, String, Integer, Boolean> filter) {
    }
}
