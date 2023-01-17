package com.Jeka8833.DocumentBeautifier.search;

import com.Jeka8833.DocumentBeautifier.ColumnName;
import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import org.apache.poi.ss.usermodel.Cell;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SearchDB {

    private final Map<ColumnName, List<DBElement>> searchDB = new ConcurrentHashMap<>();

    public void add(SheetDetailed sheet, ColumnName column, Cell cell, String text) {
        List<DBElement> rowList = searchDB.computeIfAbsent(column,
                columnName -> Collections.synchronizedList(new ArrayList<>()));
        rowList.add(new DBElement(sheet, cell, text));
    }

    @Nullable
    public List<DBElement> getColumn(ColumnName column) {
        return searchDB.get(column);
    }

    public Map<ColumnName, List<DBElement>> getSearchDB() {
        return searchDB;
    }

    public Search search(ColumnName column) {
        return new Search(searchDB.get(column));
    }
}
