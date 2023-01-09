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

    public void add(SheetDetailed sheet, ColumnName column, Cell cell, String text) {
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

    public static class DBRow {
        private final SheetDetailed sheet;
        private final Cell cell;
        private final String element;

        public DBRow(SheetDetailed sheet, Cell cell, String element) {
            this.sheet = sheet;
            this.cell = cell;
            this.element = element;
        }

        public SheetDetailed getSheet() {
            return sheet;
        }

        public Cell getCell() {
            return cell;
        }

        public String getElement() {
            return element;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DBRow row = (DBRow) o;

            if (!sheet.equals(row.sheet)) return false;
            if (!cell.equals(row.cell)) return false;
            return element.equals(row.element);
        }

        @Override
        public int hashCode() {
            int result = sheet.hashCode();
            result = 31 * result + cell.hashCode();
            result = 31 * result + element.hashCode();
            return result;
        }
    }

}
