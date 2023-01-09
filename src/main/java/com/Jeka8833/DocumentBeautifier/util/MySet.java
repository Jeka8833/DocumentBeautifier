package com.Jeka8833.DocumentBeautifier.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.IntFunction;

public class MySet<K> extends HashMap<K, K> {

    public MySet() {
    }

    public MySet(Collection<K> collection) {
        addAll(collection);
    }

    @SafeVarargs
    public MySet(K... collection) {
        addAll(collection);
    }

    public boolean contains(K k) {
        return this.containsKey(k);
    }

    public void addAll(K[] collection) {
        if (collection == null) return;

        for (K k : collection) add(k);
    }

    public void addAll(Collection<K> collection) {
        if (collection == null) return;

        for (K k : collection) add(k);
    }

    public void add(K k) {
        this.put(k, k);
    }

    public K[] toArray(IntFunction<K[]> value) {
        return this.keySet().toArray(value);
    }
}
