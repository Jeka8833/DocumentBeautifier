package com.Jeka8833.DocumentBeautifier.search;

import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import org.apache.poi.ss.usermodel.Cell;

public record Element(SheetDetailed sheet, Cell cell, String element) {
}
