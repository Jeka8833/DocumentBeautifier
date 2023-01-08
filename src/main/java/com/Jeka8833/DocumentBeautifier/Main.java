package com.Jeka8833.DocumentBeautifier;

import com.Jeka8833.DocumentBeautifier.excel.ExcelDuplicatePage;
import com.Jeka8833.DocumentBeautifier.mods.*;
import com.Jeka8833.DocumentBeautifier.mods.search.SearchDB;
import com.Jeka8833.DocumentBeautifier.mods.search.SearchManager;
import com.Jeka8833.DocumentBeautifier.util.LevenshteinDistance;
import com.Jeka8833.DocumentBeautifier.util.MySet;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        var document = new Document();

        var phoneMod = new PhoneMod();
        phoneMod.input = new ColumnName("PHONE_INPUT", "Phone");
        document.addMod(phoneMod);

        var addressMod = new AddressMod();
        addressMod.input = new ColumnName("COUNTRY_INPUT", "Address");
        document.addMod(addressMod);

        var ipnMod = new IPNMod();
        ipnMod.input = new ColumnName("IPN_INPUT", "Address");
        ipnMod.male = "Чоловік";
        ipnMod.female = "Жінка";
        document.addMod(ipnMod);

        var nameMod = new NameMod();
        nameMod.input = new ColumnName("NAME_INPUT", "Address");
        document.addMod(nameMod);

        document.processBeautifier(Path.of("..."),
                Path.of("..."));


        SearchDB searchDB = new SearchDB();
        MySet<ColumnName> searchedColumns = new MySet<>(new ColumnName[]{
                new ColumnName("NAME_INPUT", "Name"),
                new ColumnName("PHONE_INPUT", "Phone"),
                new ColumnName("IPN_INPUT", "IPN")
        });
        document.addMod(new IndexingColumnMod(new ColumnName[]{
                new ColumnName("INDEX", "Index")
        }));

        document.processSearch(Path.of("D:\\test\\Центр - допомога.xlsx"), searchedColumns, searchDB);

        ExcelDuplicatePage excelDuplicatePage = new ExcelDuplicatePage(new ColumnName[]{
                new ColumnName("INDEX", "Index"),
                new ColumnName("NAME_INPUT", "Name"),
                new ColumnName("PHONE_INPUT", "Phone"),
                new ColumnName("IPN_INPUT", "IPN"),
                new ColumnName("COUNTRY_INPUT", "Address")
        });

        List<SearchDB.DBRow> nameRows = searchDB.getColumn(new ColumnName("NAME_INPUT"));
        if (nameRows != null) {
            excelDuplicatePage.addFilter("Find",
                    new ExcelDuplicatePage.FilterPage("Name", SearchManager.searchDuplicates(nameRows)));
            excelDuplicatePage.addFilter("Need accept",
                    new ExcelDuplicatePage.FilterPage("Name",
                            SearchManager.searchDuplicatesIgnoreFullEquals(
                                    nameRows, (s, s2) -> NameMod.compareName(s, s2, 3))));
        }
        List<SearchDB.DBRow> phoneRows = searchDB.getColumn(new ColumnName("PHONE_INPUT"));
        if (phoneRows != null) {
            excelDuplicatePage.addFilter("Find",
                    new ExcelDuplicatePage.FilterPage("Phone", SearchManager.searchDuplicates(phoneRows)));
            excelDuplicatePage.addFilter("Need accept",
                    new ExcelDuplicatePage.FilterPage("Phone",
                            SearchManager.searchDuplicatesIgnoreFullEquals(phoneRows, (s, s2) -> {
                                int mistakes = LevenshteinDistance.limitedCompare(s, s2, 2);
                                return mistakes != -1;
                            })));
        }
        List<SearchDB.DBRow> ipnRows = searchDB.getColumn(new ColumnName("IPN_INPUT"));
        if (ipnRows != null) {
            excelDuplicatePage.addFilter("Find",
                    new ExcelDuplicatePage.FilterPage("IPN", SearchManager.searchDuplicates(ipnRows)));
            excelDuplicatePage.addFilter("Need accept",
                    new ExcelDuplicatePage.FilterPage("IPN",
                            SearchManager.searchDuplicatesIgnoreFullEquals(ipnRows, (s, s2) -> {
                                int mistakes = LevenshteinDistance.limitedCompare(s, s2, 2);
                                return mistakes != -1;
                            })));
        }

        excelDuplicatePage.create(Path.of("..."));
    }
}
