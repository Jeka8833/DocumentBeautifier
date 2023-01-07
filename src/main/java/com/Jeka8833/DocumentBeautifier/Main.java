package com.Jeka8833.DocumentBeautifier;

import com.Jeka8833.DocumentBeautifier.excel.ExcelDuplicatePage;
import com.Jeka8833.DocumentBeautifier.mods.*;
import com.Jeka8833.DocumentBeautifier.mods.search.SearchDB;
import com.Jeka8833.DocumentBeautifier.mods.search.SearchManager;
import com.Jeka8833.DocumentBeautifier.mods.search.SearchMod;
import com.Jeka8833.DocumentBeautifier.util.LevenshteinDistance;
import com.Jeka8833.DocumentBeautifier.util.MySet;

import java.io.IOException;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws IOException {
        SearchDB searchDB = new SearchDB();
        MySet<ColumnName> searchedColumns = new MySet<>(new ColumnName[]{
                new ColumnName("NAME_OUT", "Name"),
                new ColumnName("PHONE_OUT", "Phone"),
                new ColumnName("IPN_OUT", "IPN"),
                new ColumnName("ADDRESS_OUT", "Address")
        });

        Document document = new Document();
        document.addMod(new PhoneMod());
        document.addMod(new AddressMod());
        document.addMod(new IPNMod());
        document.addMod(new NameMod());

        document.addMod(new SearchMod(searchedColumns, searchDB));
        document.addMod(new IndexingColumnMod(new ColumnName[]{
                new ColumnName("INDEX", "Index")
        }));

        document.processBeautifier(Path.of("D:\\test\\New Microsoft Excel Worksheet.xlsx"),
                Path.of("D:\\test\\New Microsoft Excel Worksheet-out.xlsx"));

        ExcelDuplicatePage excelDuplicatePage = new ExcelDuplicatePage(new ColumnName[]{
                new ColumnName("INDEX", "Index"),
                new ColumnName("NAME_OUT", "Name"),
                new ColumnName("PHONE_OUT", "Phone"),
                new ColumnName("IPN_OUT", "IPN"),
                new ColumnName("ADDRESS_OUT", "Address")
        });

        excelDuplicatePage.addFilter("Need accept",
                new ExcelDuplicatePage.FilterPage("Name",
                        SearchManager.searchDuplicatesIgnoreFullEquals(
                                searchDB.getColumn(new ColumnName("NAME_OUT", "Name")), (s, s2) -> {
                                    int mistakes = LevenshteinDistance.limitedCompare(s, s2, 3);
                                    return mistakes != -1;
                                })));
        excelDuplicatePage.addFilter("Need accept",
                new ExcelDuplicatePage.FilterPage("Phone",
                        SearchManager.searchDuplicatesIgnoreFullEquals(
                                searchDB.getColumn(new ColumnName("PHONE_OUT", "Phone")), (s, s2) -> {
                                    int mistakes = LevenshteinDistance.limitedCompare(s, s2, 3);
                                    return mistakes != -1;
                                })));
        excelDuplicatePage.addFilter("Need accept",
                new ExcelDuplicatePage.FilterPage("IPN",
                        SearchManager.searchDuplicatesIgnoreFullEquals(
                                searchDB.getColumn(new ColumnName("IPN_OUT", "IPN")), (s, s2) -> {
                                    int mistakes = LevenshteinDistance.limitedCompare(s, s2, 3);
                                    return mistakes != -1;
                                })));
        excelDuplicatePage.addFilter("Need accept",
                new ExcelDuplicatePage.FilterPage("Address",
                        SearchManager.searchDuplicatesIgnoreFullEquals(
                                searchDB.getColumn(new ColumnName("ADDRESS_OUT", "Address")), (s, s2) -> {
                                    int mistakes = LevenshteinDistance.limitedCompare(s, s2, 3);
                                    return mistakes != -1;
                                })));
        excelDuplicatePage.addFilter("Find",
                new ExcelDuplicatePage.FilterPage("Name",
                        SearchManager.searchDuplicates(
                                searchDB.getColumn(new ColumnName("NAME_OUT", "Name")))));
        excelDuplicatePage.addFilter("Find",
                new ExcelDuplicatePage.FilterPage("Phone",
                        SearchManager.searchDuplicates(
                                searchDB.getColumn(new ColumnName("PHONE_OUT", "Phone")))));
        excelDuplicatePage.addFilter("Find",
                new ExcelDuplicatePage.FilterPage("IPN",
                        SearchManager.searchDuplicates(
                                searchDB.getColumn(new ColumnName("IPN_OUT", "IPN")))));
        excelDuplicatePage.addFilter("Find",
                new ExcelDuplicatePage.FilterPage("Address",
                        SearchManager.searchDuplicates(
                                searchDB.getColumn(new ColumnName("ADDRESS_OUT", "Address")))));

        excelDuplicatePage.create(Path.of("D:\\test\\New Microsoft Excel Worksheet-duplicate.xlsx"));
        System.out.println(searchDB);
    }
}
