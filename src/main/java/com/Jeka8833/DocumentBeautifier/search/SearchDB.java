package com.Jeka8833.DocumentBeautifier.search;

import com.Jeka8833.DocumentBeautifier.ColumnName;
import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import org.apache.poi.ss.usermodel.Cell;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SearchDB {

    private final Map<ColumnName, List<DBRow>> searchDB = new ConcurrentHashMap<>();

    public void add(SheetDetailed sheet, ColumnName column, Cell cell, String text) {
        List<DBRow> rowList = searchDB.computeIfAbsent(column,
                columnName -> Collections.synchronizedList(new ArrayList<>()));
        rowList.add(new DBRow(sheet, cell, text));
    }

    @Nullable
    public List<DBRow> getColumn(ColumnName column) {
        return searchDB.get(column);
    }

    public Map<ColumnName, List<DBRow>> getSearchDB() {
        return searchDB;
    }
}
