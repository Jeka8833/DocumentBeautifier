package com.Jeka8833.DocumentBeautifier.search;

import com.Jeka8833.DocumentBeautifier.excel.SheetDetailed;
import org.apache.poi.ss.usermodel.Cell;

public record DBRow(SheetDetailed sheet, Cell cell, String element) {
}
