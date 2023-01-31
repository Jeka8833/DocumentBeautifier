package com.Jeka8833.DocumentBeautifier.header;

import org.jetbrains.annotations.NotNull;

public class ColumnHeader implements Cloneable {

    private final @NotNull String labelName;
    private final @NotNull String displayName;
    private int posX;
    private @NotNull String modProperties = "";

    public ColumnHeader(@NotNull String labelName) {
        this(labelName, "");
    }

    public ColumnHeader(@NotNull String labelName, @NotNull String displayName) {
        this.labelName = labelName.toLowerCase();
        this.displayName = displayName;
    }

    @NotNull
    public String getLabelName() {
        return labelName;
    }

    @NotNull
    public String getDisplayName() {
        return displayName;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosX() {
        return posX;
    }

    @NotNull
    public String getModProperties() {
        return modProperties;
    }

    public void setModProperties(@NotNull String modProperties) {
        this.modProperties = modProperties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColumnHeader that = (ColumnHeader) o;

        return labelName.equalsIgnoreCase(that.labelName);
    }

    @Override
    public int hashCode() {
        return labelName.toLowerCase().hashCode();
    }

    @Override
    public ColumnHeader clone() {
        try {
            return (ColumnHeader) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return "ColumnHeader{" +
                "columnIndex='" + labelName + '\'' +
                ", name='" + displayName + '\'' +
                ", posX=" + posX +
                ", sheetProperties='" + modProperties + '\'' +
                '}';
    }
}
