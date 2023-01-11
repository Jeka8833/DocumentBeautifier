package com.Jeka8833.DocumentBeautifier.search;

import com.Jeka8833.DocumentBeautifier.ColumnName;
import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import org.apache.poi.ss.usermodel.Cell;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchDB {

    private final Map<ColumnName, List<DBRow>> searchDB = new HashMap<>();

    public synchronized void add(SheetDetailed sheet, ColumnName column, Cell cell, String text) {
        List<DBRow> rowList = searchDB.get(column);
        if (rowList == null) {
            List<DBRow> newList = new ArrayList<>();
            newList.add(new DBRow(sheet, cell, text));
            searchDB.put(column, newList);
        } else {
            rowList.add(new DBRow(sheet, cell, text));
        }
    }

    @Nullable
    public List<DBRow> getColumn(ColumnName column) {
        return searchDB.get(column);
    }

    public Map<ColumnName, List<DBRow>> getSearchDB() {
        return searchDB;
    }

    public record DBRow(SheetDetailed sheet, Cell cell, String element) {
    }
}
