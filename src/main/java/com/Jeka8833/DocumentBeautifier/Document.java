package com.Jeka8833.DocumentBeautifier;

import com.Jeka8833.DocumentBeautifier.excel.ExcelReader;
import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import com.Jeka8833.DocumentBeautifier.mods.Mod;
import com.Jeka8833.DocumentBeautifier.util.MySet;
import org.apache.poi.ss.usermodel.Row;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Document {

    private final List<Mod> mods = new ArrayList<>();


    public void processBeautifier(Path inputFile, Path outputFile) throws IOException {
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
    }

    public void addMod(Mod mod) {
        mods.add(mod);
    }

    public List<Mod> getMods() {
        return mods;
    }

}
