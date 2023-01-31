package com.Jeka8833.DocumentBeautifier.header;

record ColumnLabel(String name, String properties) {

    public ColumnLabel(String name) {
        this(name, "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColumnLabel that = (ColumnLabel) o;

        return name.equalsIgnoreCase(that.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }
}
