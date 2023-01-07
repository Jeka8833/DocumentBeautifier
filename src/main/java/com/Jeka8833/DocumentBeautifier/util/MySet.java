package com.Jeka8833.DocumentBeautifier.util;

import java.util.Collection;
import java.util.HashMap;

public class MySet<K> extends HashMap<K, K> {

    public boolean contains(K k) {
        return this.containsKey(k);
    }

    public void addAll(Collection<K> collection) {
        if (collection == null) return;

        for (K k : collection) add(k);
    }

    public void add(K k) {
        this.put(k, k);
    }
}
