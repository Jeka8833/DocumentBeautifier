package com.Jeka8833.DocumentBeautifier;

import com.Jeka8833.DocumentBeautifier.mods.IPNMod;
import com.Jeka8833.DocumentBeautifier.mods.NameMod;
import com.Jeka8833.DocumentBeautifier.mods.PhoneMod;

import java.io.IOException;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws IOException {
        Document document = new Document();
        document.addMod(new PhoneMod());
        document.addMod(new IPNMod());
        document.addMod(new NameMod());
        document.processBeautifier(Path.of("D:\\test\\New Microsoft Excel Worksheet.xlsx"),
                Path.of("D:\\test\\New Microsoft Excel Worksheet-out.xlsx"));
    }

}
