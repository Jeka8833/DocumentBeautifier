package com.Jeka8833.DocumentBeautifier;

import org.jetbrains.annotations.NotNull;

public class ColumnName implements Cloneable {

    private final @NotNull String columnIndex;
    private final @NotNull String name;
    private int posX;

    public ColumnName(@NotNull String columnIndex, @NotNull String name) {
        this(columnIndex, name, Integer.MIN_VALUE);
    }

    public ColumnName(@NotNull String columnIndex, @NotNull String name, int posX) {
        this.columnIndex = columnIndex;
        this.name = name;
        this.posX = posX;
    }

    @NotNull
    public String getColumnIndex() {
        return columnIndex;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosX() {
        return posX;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColumnName that = (ColumnName) o;

        return columnIndex.equals(that.columnIndex);
    }

    @Override
    public int hashCode() {
        return columnIndex.hashCode();
    }

    @Override
    public ColumnName clone() {
        try {
            return (ColumnName) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
