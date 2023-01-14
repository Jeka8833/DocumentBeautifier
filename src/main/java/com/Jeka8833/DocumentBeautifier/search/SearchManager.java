package com.Jeka8833.DocumentBeautifier.search;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class SearchManager {

    public static Map<String, List<DBRow>> filterOfPath(Map<String, List<DBRow>> listMap,
                                                        Path... filerFiles) {
        if (filerFiles == null) return listMap;

        Map<String, List<DBRow>> out = new HashMap<>();
        for (Map.Entry<String, List<DBRow>> entry : listMap.entrySet()) {
            DBRow dbRow = findRowByPath(entry.getValue(), filerFiles);
            if (dbRow != null && dbRow.element().equalsIgnoreCase(entry.getKey())) {
                out.put(entry.getKey(), entry.getValue());
            }
        }
        return out;
    }

    public static Map<String, List<DBRow>> searchDuplicates(List<DBRow> rowList) {
        return deleteSingleItem(rowList.stream().collect(Collectors.groupingBy(DBRow::element)));
    }

    public static Map<String, List<DBRow>> searchDuplicatesIgnoreFullEquals(List<DBRow> rowList,
                                                                            BiFunction<String, String, Boolean> compareFunction) {
        Map<String, List<DBRow>> out = new HashMap<>();
        for (DBRow rowFirst : rowList) {
            if (out.containsKey(rowFirst.element())) continue;

            List<DBRow> temp = new ArrayList<>();
            temp.add(rowFirst);

            for (DBRow rowSecond : rowList) {
                if (rowFirst.element().equals(rowSecond.element())) continue;

                if (compareFunction.apply(rowFirst.element(), rowSecond.element())) temp.add(rowSecond);
            }
            if (temp.size() > 1) out.put(rowFirst.element(), temp);
        }
        return out;
    }

    public static Map<String, List<DBRow>> searchDuplicatesIgnoreFullEqualsFilterPath(List<DBRow> rowList, List<Path> filter,
                                                                                      BiFunction<String, String, Boolean> compareFunction) {
        Map<String, List<DBRow>> out = new HashMap<>();
        for (DBRow rowFirst : rowList) {
            if (out.containsKey(rowFirst.element())) continue;
            if (!containsPath(rowFirst, filter)) continue;

            List<DBRow> temp = new ArrayList<>();
            temp.add(rowFirst);

            for (DBRow rowSecond : rowList) {
                if (rowFirst.element().equals(rowSecond.element())) continue;

                if (compareFunction.apply(rowFirst.element(), rowSecond.element())) temp.add(rowSecond);
            }
            if (temp.size() > 1) out.put(rowFirst.element(), temp);
        }
        return out;
    }

    private static DBRow findRowByPath(@NotNull List<DBRow> rowList, Path... filerFiles) {
        if (filerFiles == null) return null;

        for (DBRow row : rowList) {
            for (Path path : filerFiles) {
                if (row.sheet().getReader().getInputFile().equals(path)) return row;
            }
        }
        return null;
    }

    private static boolean containsPath(DBRow row, List<Path> filter) {
        for (Path path : filter) {
            if (row.sheet().getReader().getInputFile().equals(path))
                return true;
        }
        return false;
    }

    public static Map<String, List<DBRow>> deleteSingleItem(Map<String, List<DBRow>> listMap) {
        Map<String, List<DBRow>> out = new HashMap<>();
        for (Map.Entry<String, List<DBRow>> entry : listMap.entrySet()) {
            if (entry.getValue().size() >= 2) out.put(entry.getKey(), entry.getValue());
        }
        return out;
    }
}
