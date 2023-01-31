package com.Jeka8833.DocumentBeautifier.header;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ColumnParserTest {

    @Test
    void findNextChar() {
        Assertions.assertEquals(5, ColumnParser.findNextChar("t    a", 1));
    }
}