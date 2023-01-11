package com.Jeka8833.DocumentBeautifier.excel;

import com.Jeka8833.DocumentBeautifier.mods.Mod;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

public class ExcelReader implements AutoCloseable {

    private final Workbook workbook;
    private final Path inputFile;

    public ExcelReader(Path file) throws IOException {
        inputFile = file;
        String fileName = file.getFileName().toString().toLowerCase();
        if (fileName.startsWith("~$"))
            throw new IllegalArgumentException("The file '" + file.getFileName() + "' is a backup file.");

        if (fileName.endsWith(".xls"))
            workbook = new HSSFWorkbook(Files.newInputStream(file));
        else if (fileName.endsWith(".xlsx"))
            workbook = new XSSFWorkbook(Files.newInputStream(file));
        else throw new IllegalArgumentException("Invalid file format");
    }

    public Sheet[] getSheets() {
        int sheetCount = workbook.getNumberOfSheets();
        var sheets = new Sheet[sheetCount];
        for (int i = 0; i < sheetCount; i++) {
            sheets[i] = workbook.getSheetAt(i);
        }
        return sheets;
    }

    public SheetDetailed[] getSheetsWithNames(Collection<Mod> mods) {
        int sheetCount = workbook.getNumberOfSheets();
        var sheets = new SheetDetailed[sheetCount];
        for (int i = 0; i < sheetCount; i++) {
            var sheetDetailed = new SheetDetailed(workbook.getSheetAt(i), this);
            sheetDetailed.readColumnNames(mods);

            sheets[i] = sheetDetailed;
        }
        return sheets;
    }

    public void save(Path file) throws IOException {
        try (OutputStream stream = Files.newOutputStream(file)) {
            workbook.write(stream);
        }
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public Path getInputFile() {
        return inputFile;
    }

    @Override
    public void close() {
        try {
            workbook.close();
        } catch (IOException ignored) {
        }
    }
}
