package com.Jeka8833.DocumentBeautifier.mods;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressModTest {

    @Test
    void formatText() {
        var mod = new AddressMod();
        assertEquals("вул.В. Івасюка, 3", mod.formatText(mod.input, "вyл. B. Івасюка , 3"));
    }
}