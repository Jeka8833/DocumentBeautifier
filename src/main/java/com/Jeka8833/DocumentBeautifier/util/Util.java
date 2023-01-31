package com.Jeka8833.DocumentBeautifier.util;

import org.jetbrains.annotations.NotNull;

public class Util {

    public static @NotNull String replaceEnglish(@NotNull String text) {
        return text.replace('A', 'А')
                .replace('B', 'В')
                .replace('C', 'С')
                .replace('E', 'Е')
                .replace('H', 'Н')
                .replace('I', 'І')
                .replace('K', 'К')
                .replace('M', 'М')
                .replace('O', 'О')
                .replace('P', 'Р')
                .replace('T', 'Т')
                .replace('X', 'Х')
                .replace('a', 'а')
                .replace('c', 'с')
                .replace('e', 'е')
                .replace('i', 'і')
                .replace('o', 'о')
                .replace('p', 'р')
                .replace('x', 'х')
                .replace('y', 'у')
                .replaceAll("[`'’ʼ]", "’");
    }
}
