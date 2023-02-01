package com.Jeka8833.DocumentBeautifier;

import com.Jeka8833.DocumentBeautifier.excel.ExcelCell;
import com.Jeka8833.DocumentBeautifier.excel.ExcelReader;
import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import com.Jeka8833.DocumentBeautifier.header.ColumnHeader;
import com.Jeka8833.DocumentBeautifier.mods.CombineIPNPassportMod;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class Document {
    private static final Logger logger = LogManager.getLogger(Document.class);

    private final Map<String, String> alternativeColumnNames = new HashMap<>();
    private final List<Mod> mods = new ArrayList<>();

    public void processBeautifier(Path inputFile, Path outputFile) throws IOException {
        CombineIPNPassportMod combineIPNPassportMod = mods.stream()
                .filter(mod -> mod instanceof CombineIPNPassportMod)
                .map(mod -> (CombineIPNPassportMod) mod)
                .findAny().orElse(null);

        try (ExcelReader reader = new ExcelReader(inputFile)) {
            SheetDetailed[] sheets = reader.getSheetsWithNames(this);
            for (SheetDetailed sheet : sheets) {
                if (!sheet.haveColumn()) continue;

                MySet<ColumnHeader> columnNames = sheet.getColumnNames();
                boolean isCombined = combineIPNPassportMod != null && columnNames.contains(combineIPNPassportMod.input);

                for (Row row : sheet.getSheet()) {
                    if (sheet.getStartPosY() > row.getRowNum()) continue;
                    for (Mod mod : mods) {
                        if (isCombined && combineIPNPassportMod.inSkipMod(mod)) continue;

                        for (ColumnHeader column : mod.getNeededColumn()) {
                            ColumnHeader originalColumn = columnNames.get(column);
                            if (originalColumn == null) continue;

                            Cell cell = row.getCell(originalColumn.getPosX());
                            if (cell == null) continue;

                            mod.setParameters(originalColumn.getModProperties())
                                    .process(sheet, originalColumn, cell);
                        }
                    }
                }
            }
            reader.save(outputFile);
        }
    }

    public void processSearchFiles(List<Path> inputFiles, MySet<ColumnHeader> searching, SearchDB searchDB)
            throws InterruptedException {
        ExecutorService threadPool = Executors.newWorkStealingPool();
        try {
            processSearchFiles(inputFiles, searching, searchDB, threadPool);
        } finally {
            threadPool.shutdown();
        }
    }

    public void processSearchFiles(List<Path> inputFiles, MySet<ColumnHeader> searching, SearchDB searchDB,
                                   ExecutorService threadPool) throws InterruptedException {
        List<Callable<Void>> taskPool = new ArrayList<>();
        for (Path path : inputFiles) {
            if (Files.isDirectory(path)) {
                try (Stream<Path> fileStream = Files.walk(path)) {
                    fileStream.filter(Document::checkSupportFormat)
                            .forEach(path1 -> taskPool.add(() -> {
                                try {
                                    processSearch(path1, searching, searchDB);
                                } catch (Exception e) {
                                    logger.warn("Fail process file" + e);
                                }
                                return null;
                            }));
                } catch (IOException e) {
                    logger.warn("Fail walk" + e);
                }
            } else if (checkSupportFormat(path)) {
                taskPool.add(() -> {
                    try {
                        processSearch(path, searching, searchDB);
                    } catch (Exception e) {
                        logger.warn("Fail process file" + e);
                    }
                    return null;
                });
            }
        }
        threadPool.invokeAll(taskPool);
    }

    public static boolean checkSupportFormat(@NotNull Path path) {
        if (!Files.isRegularFile(path)) return false;

        String name = path.getFileName().toString().toLowerCase();
        return !name.startsWith("~$") && (name.endsWith(".xls") || name.endsWith(".xlsx"));
    }

    public void processSearch(Path inputFile, MySet<ColumnHeader> searching, SearchDB searchDB) throws IOException {
        logger.info("Search in file: " + inputFile);

        CombineIPNPassportMod combineIPNPassportMod = mods.stream()
                .filter(mod -> mod instanceof CombineIPNPassportMod)
                .map(mod -> (CombineIPNPassportMod) mod)
                .findAny().orElse(null);

        try (ExcelReader reader = new ExcelReader(inputFile)) {
            SheetDetailed[] sheets = reader.getSheetsWithNames(this);
            for (SheetDetailed sheet : sheets) {
                if (!sheet.haveColumn()) continue;

                MySet<ColumnHeader> columnNames = sheet.getColumnNames();
                boolean isCombined = combineIPNPassportMod != null && columnNames.contains(combineIPNPassportMod.input);

                for (Row row : sheet.getSheet()) {
                    if (sheet.getStartPosY() > row.getRowNum()) continue;

                    for (ColumnHeader column : columnNames.keySet()) {
                        if (!searching.contains(column)) continue;

                        Cell cell = row.getCell(column.getPosX());
                        if (cell == null) continue;

                        String formattedText = ExcelCell.getText(cell);
                        for (Mod mod : mods) {
                            if (isCombined && combineIPNPassportMod.inSkipMod(mod)) {
                                formattedText = combineIPNPassportMod.formatText(column, formattedText);
                            } else {
                                formattedText = mod.formatText(column, formattedText);
                            }
                        }
                        if (!formattedText.isBlank()) searchDB.add(sheet, column, cell, formattedText);
                    }
                }
            }
        }
    }

    public void addMod(Mod mod) {
        mods.add(mod);
    }

    public List<Mod> getMods() {
        return mods;
    }

    public void addAlternativeColumnName(String from, String to) {
        alternativeColumnNames.put(from.toUpperCase(), to.toUpperCase());
    }

    public Map<String, String> getAlternativeColumnNames() {
        return alternativeColumnNames;
    }
}
