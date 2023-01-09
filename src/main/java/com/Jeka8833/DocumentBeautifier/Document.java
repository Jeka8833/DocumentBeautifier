package com.Jeka8833.DocumentBeautifier;

import com.Jeka8833.DocumentBeautifier.excel.ExcelCell;
import com.Jeka8833.DocumentBeautifier.excel.ExcelReader;
import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import com.Jeka8833.DocumentBeautifier.mods.Mod;
import com.Jeka8833.DocumentBeautifier.search.SearchDB;
import com.Jeka8833.DocumentBeautifier.util.MySet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Document {
    private static final Logger logger = LogManager.getLogger(Document.class);
    private final List<Mod> mods = new ArrayList<>();


    public ExcelReader processBeautifier(Path inputFile, Path outputFile) throws IOException {
        ExcelReader reader = new ExcelReader(inputFile);
        SheetDetailed[] sheets = reader.getSheetsWithNames(mods);
        for (SheetDetailed sheet : sheets) {
            if (!sheet.haveColumn()) continue;

            MySet<ColumnName> columnNames = sheet.getColumnNames();
            for (Row row : sheet.getSheet()) {
                if (sheet.getStartPosY() > row.getRowNum()) continue;
                for (Mod mod : mods) {
                    for (ColumnName column : columnNames.keySet()) {
                        mod.process(sheet, column, row.getCell(column.getPosX()));
                    }
                }
            }
        }
        reader.save(outputFile);
        return reader;
    }

    public void processSearchFiles(List<Path> inputFiles, MySet<ColumnName> searching, SearchDB searchDB)
            throws IOException {
        for (Path path : inputFiles) {
            if (Files.isDirectory(path)) {
                try (Stream<Path> fileStream = Files.walk(path)) {
                    fileStream.filter(Files::isRegularFile)
                            .filter(Document::checkSupportFormat)
                            .forEach(path1 -> {
                                try {
                                    processSearch(path1, searching, searchDB);
                                } catch (Exception e) {
                                    logger.warn("Fail process file" + e);
                                }
                            });
                }
            } else if (Files.isRegularFile(path)) {
                if (checkSupportFormat(path)) {
                    try {
                        processSearch(path, searching, searchDB);
                    } catch (Exception e) {
                        logger.warn("Fail process file" + e);
                    }
                }
            } else {
                logger.warn("File or folder not exists: " + path);
            }
        }
    }

    public static boolean checkSupportFormat(@NotNull Path path) {
        String name = path.getFileName().toString().toLowerCase();
        return !name.startsWith("~$") && (name.endsWith(".xls") || name.endsWith(".xlsx"));
    }

    public ExcelReader processSearch(Path inputFile, MySet<ColumnName> searching, SearchDB searchDB) throws IOException {
        logger.info("Search in file: " + inputFile);
        ExcelReader reader = new ExcelReader(inputFile);
        SheetDetailed[] sheets = reader.getSheetsWithNames(mods);
        for (SheetDetailed sheet : sheets) {
            if (!sheet.haveColumn()) continue;

            MySet<ColumnName> columnNames = sheet.getColumnNames();
            for (Row row : sheet.getSheet()) {
                if (sheet.getStartPosY() > row.getRowNum()) continue;

                for (ColumnName column : columnNames.keySet()) {
                    if (!searching.contains(column)) continue;

                    Cell cell = row.getCell(column.getPosX());
                    String formattedText = ExcelCell.getText(cell);
                    for (Mod mod : mods) {
                        formattedText = mod.formatText(column, formattedText);
                    }
                    if (!formattedText.isBlank()) searchDB.add(sheet, column, cell, formattedText);
                }
            }
        }
        return reader;
    }

    public void addMod(Mod mod) {
        mods.add(mod);
    }

    public List<Mod> getMods() {
        return mods;
    }
}
