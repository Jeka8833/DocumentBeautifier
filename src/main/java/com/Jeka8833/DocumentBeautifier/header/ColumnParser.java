package com.Jeka8833.DocumentBeautifier.header;

import com.Jeka8833.DocumentBeautifier.util.MySet;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ColumnParser {
    private static final Logger LOGGER = LogManager.getLogger(ColumnParser.class);
    private static final Moshi MOSHI = new Moshi.Builder().build();

    @Contract(pure = true, value = "_, _, _ -> new")
    public static MySet<ColumnHeader> text2Columns(@NotNull String text, @NotNull MySet<ColumnHeader> allowingColumns,
                                                   @NotNull Map<String, String> alternativeColumnNames) {
        MySet<ColumnHeader> output = new MySet<>();

        Set<ColumnLabel> columnLabels = ColumnParser.splitText(text);
        for (ColumnLabel label : columnLabels) {
            String name = alternativeColumnNames.getOrDefault(label.name(), label.name());
            ColumnHeader header = allowingColumns.get(new ColumnHeader(name));
            if (header != null) {
                ColumnHeader clonedHeader = header.clone();
                clonedHeader.setModProperties(label.properties());
                output.add(clonedHeader);
            }
        }
        return output;
    }

    @Contract(pure = true, value = "_ -> new")
    public static Set<ColumnLabel> splitText(@NotNull String text) {
        final Set<ColumnLabel> labels = new HashSet<>();

        text = text.strip();
        if (text.isEmpty()) return labels;

        boolean stateInvertedComma = false;
        int startPositionProperties = -1;
        int caretPosition = 0;

        for (int i = 0; i < text.length(); i++) {
            final char currentChar = text.charAt(i);

            if (startPositionProperties < 0) {
                if (currentChar == '{') {
                    if (text.substring(caretPosition, i).isBlank()) {
                        caretPosition = i;
                    } else if (i - 1 >= 0 && text.charAt(i - 1) != '\\') {
                        startPositionProperties = i;
                    }
                } else if (currentChar == ';') {
                    String labelName = text.substring(caretPosition, i)
                            .strip()
                            .replace("\\{", "{");

                    if (!labelName.isEmpty()) labels.add(new ColumnLabel(labelName));

                    caretPosition = i + 1;
                }
            } else {
                if (currentChar == '"') {
                    if (i - 1 >= 0 && text.charAt(i - 1) != '\\') {
                        stateInvertedComma = !stateInvertedComma;
                    }
                } else if (!stateInvertedComma && currentChar == '}') {
                    final int nextChar = findNextChar(text, i + 1);

                    if (nextChar >= text.length() || text.charAt(nextChar) == ';') {
                        final String labelName = text.substring(caretPosition, startPositionProperties)
                                .strip()
                                .replace("\\{", "{");

                        if (!labelName.isEmpty()) {
                            String parameters = text.substring(startPositionProperties, i + 1);
                            // Min length of text is 7 characters ('{"a":a}')
                            if (parameters.length() >= 7) {
                                labels.add(new ColumnLabel(labelName, parameters));
                            } else {
                                labels.add(new ColumnLabel(labelName));
                            }
                        }

                        i = nextChar;
                        caretPosition = nextChar + 1;
                        startPositionProperties = -1;
                    }
                }
            }
        }
        if (caretPosition < text.length() - 1) {
            String labelName = text.substring(caretPosition,
                            startPositionProperties > -1 ? startPositionProperties : text.length())
                    .strip()
                    .replace("\\{", "{");

            if (!labelName.isEmpty()) {
                labels.add(new ColumnLabel(labelName));
            }
        }

        return labels;
    }

    @Contract(pure = true)
    @Range(from = 0, to = Integer.MAX_VALUE)
    public static int findNextChar(@NotNull String text, int startIndex) {
        if (startIndex < 0) return 0;

        for (int i = startIndex; i < text.length(); i++) {
            if (!Character.isWhitespace(text.charAt(i))) return i;
        }
        return text.length();
    }

    @SuppressWarnings("unchecked")
    @Contract(value = "_, _ -> param1", mutates = "param1")
    public static <V> V updateModParameter(@NotNull V input, @NotNull String parameters) {
        final JsonAdapter<V> adapter;
        try {
            adapter = (JsonAdapter<V>) MOSHI.adapter(input.getClass());
        } catch (Exception exception) {
            LOGGER.warn("Fail create adapter for " + input + ", ignoring '" + parameters +
                    "' parameters", exception);
            return input;
        }

        V newModConstructor = null;
        try {
            newModConstructor = adapter.fromJson(parameters);
        } catch (Exception exception1) {
            LOGGER.warn("Fail parse parameters '" + parameters +
                    "', please solve this problem. Trying again...", exception1);
            try {
                newModConstructor = adapter.lenient().fromJson(parameters);
            } catch (Exception ignore) {
            }
        }

        if (newModConstructor == null) {
            LOGGER.warn("The parameters are incorrect, they are skipped");
            return input;
        }

        try {
            set2DefaultValues(newModConstructor, input);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            LOGGER.warn("Fail a field editing", e);
        }
        return input;
    }

    @Contract(mutates = "param2")
    public static <V> void set2DefaultValues(@NotNull V input, @NotNull V defaultValues) throws NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        Object virtualObject = input.getClass().getDeclaredConstructor().newInstance();

        for (Field field : virtualObject.getClass().getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
                field.setAccessible(true);
                Object inputValue = field.get(input);
                if (!inputValue.equals(field.get(virtualObject))) {
                    field.set(defaultValues, inputValue);
                }
            }
        }
    }
}
