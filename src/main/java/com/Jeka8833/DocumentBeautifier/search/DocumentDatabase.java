package com.Jeka8833.DocumentBeautifier.search;

import com.Jeka8833.DocumentBeautifier.header.ColumnHeader;
import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import org.apache.poi.ss.usermodel.Cell;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DocumentDatabase {

    private final Map<ColumnHeader, List<Element>> database = new ConcurrentHashMap<>();

    /**
     * Adds information to the database. This method allows for multithreading.
     */
    public void add(SheetDetailed sheet, ColumnHeader column, Cell cell, String text) {
        List<Element> rowList = database.computeIfAbsent(column,
                columnName -> Collections.synchronizedList(new ArrayList<>()));
        rowList.add(new Element(sheet, cell, text));
    }

    /**
     * Returns the found column in the database.
     *
     * @param column The link to be found in the database
     * @return Returns the found column in the database. If the column is not found, returns null
     * @throws NullPointerException If the 'column' parameter is null
     */
    @Nullable
    public List<Element> getColumn(@NotNull ColumnHeader column) {
        return database.get(column);
    }

    @NotNull
    public Map<ColumnHeader, List<Element>> getDatabase() {
        return database;
    }

    /**
     * Creating a new {@link SearchDuplicates} stream for the current column.
     *
     * @param column The link to be found in the database
     * @return Returns a new {@link SearchDuplicates} stream
     * @throws NullPointerException If the 'column' parameter is null
     */
    @Contract("_->new")
    public SearchDuplicates search(@NotNull ColumnHeader column) {
        return new SearchDuplicates(database.get(column));
    }
}
