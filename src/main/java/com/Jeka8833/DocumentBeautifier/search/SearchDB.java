package com.Jeka8833.DocumentBeautifier.search;

import com.Jeka8833.DocumentBeautifier.header.ColumnHeader;
import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import org.apache.poi.ss.usermodel.Cell;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SearchDB {

    private final Map<ColumnHeader, List<DBElement>> searchDB = new ConcurrentHashMap<>();

    /**
     * Adds information to the database. This method allows for multithreading.
     */
    public void add(SheetDetailed sheet, ColumnHeader column, Cell cell, String text) {
        List<DBElement> rowList = searchDB.computeIfAbsent(column,
                columnName -> Collections.synchronizedList(new ArrayList<>()));
        rowList.add(new DBElement(sheet, cell, text));
    }

    @Nullable
    public List<DBElement> getColumn(ColumnHeader column) {
        return searchDB.get(column);
    }

    public Map<ColumnHeader, List<DBElement>> getSearchDB() {
        return searchDB;
    }

    /**
     * Creating a new {@link Search} stream for the current column.
     *
     * @param column The link to be found in the database
     * @throws NullPointerException If the 'column' parameter is null
     */
    @Contract("_->new")
    public Search search(@NotNull ColumnHeader column) {
        return new Search(searchDB.get(column));
    }
}
