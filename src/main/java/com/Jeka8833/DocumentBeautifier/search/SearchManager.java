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

    public static Map<String, List<SearchDB.DBRow>> filterOfPath(Map<String, List<SearchDB.DBRow>> listMap,
                                                                 Path... filerFiles) {
        if (filerFiles == null) return listMap;

        Map<String, List<SearchDB.DBRow>> out = new HashMap<>();
        for (Map.Entry<String, List<SearchDB.DBRow>> entry : listMap.entrySet()) {
            SearchDB.DBRow dbRow = findRowByPath(entry.getValue(), filerFiles);
            if (dbRow != null && dbRow.getElement().equalsIgnoreCase(entry.getKey())) {
                out.put(entry.getKey(), entry.getValue());
            }
        }
        return out;
    }

    public static Map<String, List<SearchDB.DBRow>> searchDuplicates(List<SearchDB.DBRow> rowList) {
        return deleteSingleItem(rowList.stream().collect(Collectors.groupingBy(SearchDB.DBRow::getElement)));
    }

    public static Map<String, List<SearchDB.DBRow>> searchDuplicatesIgnoreFullEquals(List<SearchDB.DBRow> rowList,
                                                                                     BiFunction<String, String, Boolean> compareFunction) {
        Map<String, List<SearchDB.DBRow>> out = new HashMap<>();
        for (SearchDB.DBRow rowFirst : rowList) {
            if (out.containsKey(rowFirst.getElement())) continue;

            List<SearchDB.DBRow> temp = new ArrayList<>();
            temp.add(rowFirst);

            for (SearchDB.DBRow rowSecond : rowList) {
                if (rowFirst.getElement().equals(rowSecond.getElement())) continue;

                if (compareFunction.apply(rowFirst.getElement(), rowSecond.getElement())) temp.add(rowSecond);
            }
            if (temp.size() > 1) out.put(rowFirst.getElement(), temp);
        }
        return out;
    }

    public static Map<String, List<SearchDB.DBRow>> searchDuplicatesIgnoreFullEqualsFilterPath(List<SearchDB.DBRow> rowList, List<Path> filter,
                                                                                               BiFunction<String, String, Boolean> compareFunction) {
        Map<String, List<SearchDB.DBRow>> out = new HashMap<>();
        for (SearchDB.DBRow rowFirst : rowList) {
            if (out.containsKey(rowFirst.getElement())) continue;
            if (!containsPath(rowFirst, filter)) continue;

            List<SearchDB.DBRow> temp = new ArrayList<>();
            temp.add(rowFirst);

            for (SearchDB.DBRow rowSecond : rowList) {
                if (rowFirst.getElement().equals(rowSecond.getElement())) continue;

                if (compareFunction.apply(rowFirst.getElement(), rowSecond.getElement())) temp.add(rowSecond);
            }
            if (temp.size() > 1) out.put(rowFirst.getElement(), temp);
        }
        return out;
    }

    private static SearchDB.DBRow findRowByPath(@NotNull List<SearchDB.DBRow> rowList, Path... filerFiles) {
        if (filerFiles == null) return null;

        for (SearchDB.DBRow row : rowList) {
            for (Path path : filerFiles) {
                if (row.getSheet().getReader().getInputFile().equals(path)) return row;
            }
        }
        return null;
    }

    private static boolean containsPath(SearchDB.DBRow row, List<Path> filter) {
        for (Path path : filter) {
            if (row.getSheet().getReader().getInputFile().equals(path))
                return true;
        }
        return false;
    }

    public static Map<String, List<SearchDB.DBRow>> deleteSingleItem(Map<String, List<SearchDB.DBRow>> listMap) {
        Map<String, List<SearchDB.DBRow>> out = new HashMap<>();
        for (Map.Entry<String, List<SearchDB.DBRow>> entry : listMap.entrySet()) {
            if (entry.getValue().size() >= 2) out.put(entry.getKey(), entry.getValue());
        }
        return out;
    }
}
