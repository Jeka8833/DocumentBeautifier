package com.Jeka8833.DocumentBeautifier;

import java.util.Objects;

public class ColumnName implements Cloneable {

    private final String columnIndex;
    private final String name;
    private int posX;

    public ColumnName(String columnIndex, String name) {
        this(columnIndex, name, Integer.MIN_VALUE);
    }

    public ColumnName(String columnIndex, String name, int posX) {
        this.columnIndex = columnIndex;
        this.name = name;
        this.posX = posX;
    }

    public String getColumnIndex() {
        return columnIndex;
    }

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

        return Objects.equals(columnIndex, that.columnIndex);
    }

    @Override
    public int hashCode() {
        return columnIndex != null ? columnIndex.hashCode() : 0;
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
