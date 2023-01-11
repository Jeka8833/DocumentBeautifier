package com.Jeka8833.DocumentBeautifier;

import com.Jeka8833.DocumentBeautifier.util.LevenshteinDistance;

import java.util.HashMap;
import java.util.Map;

public class CacheSystem {

    private static final Map<String, String[]> CACHE_SPLIT_NAMES = new HashMap<>();
    private static final Map<DistanceCache, Integer> CACHE_DISTANCE = new HashMap<>();
    private static final Map<DistanceLimitedCache, Integer> CACHE_DISTANCE_LIMITED = new HashMap<>();

    public static boolean compareName(String name1, String name2, int mistakes) {
        String[] name1ArrayCache = CACHE_SPLIT_NAMES.get(name1);
        String[] name2ArrayCache = CACHE_SPLIT_NAMES.get(name2);

        final String[] name1Array;
        final String[] name2Array;
        if (name1ArrayCache == null) {
            name1Array = name1.split("[.,\\s]+");
            CACHE_SPLIT_NAMES.put(name1, name1Array);
        } else {
            name1Array = name1ArrayCache;
        }
        if (name2ArrayCache == null) {
            name2Array = name2.split("[.,\\s]+");
            CACHE_SPLIT_NAMES.put(name2, name2Array);
        } else {
            name2Array = name2ArrayCache;
        }

        if (name1Array.length != name2Array.length) return false;

        for (int i = 0; i < name1Array.length; i++) {
            if (name1Array[i].length() == 1 || name2Array[i].length() == 1) {
                if (Character.toLowerCase(name1Array[i].charAt(0)) != Character.toLowerCase(name2Array[i].charAt(0)))
                    return false;
            } else {
                int value = limitedCompare(name1Array[i], name2Array[i], mistakes);
                if (value == -1) return false;
                mistakes -= value;
            }
        }
        return true;
    }

    public static int limitedCompare(CharSequence left, CharSequence right, final int threshold) {
        var key = new DistanceLimitedCache(left, right, threshold);
        Integer cacheValue = CACHE_DISTANCE_LIMITED.get(key);
        if (cacheValue != null) return cacheValue;

        int value = LevenshteinDistance.limitedCompare(left, right, threshold);
        CACHE_DISTANCE_LIMITED.put(key, value);
        return value;
    }

    public static int unlimitedCompare(CharSequence left, CharSequence right) {
        var key = new DistanceCache(left, right);
        Integer cacheValue = CACHE_DISTANCE.get(key);
        if (cacheValue != null) return cacheValue;

        int value = LevenshteinDistance.unlimitedCompare(left, right);
        CACHE_DISTANCE.put(key, value);
        return value;
    }

    public static void close() {
        CACHE_SPLIT_NAMES.clear();
        CACHE_DISTANCE.clear();
        CACHE_DISTANCE_LIMITED.clear();
    }

    private record DistanceCache(CharSequence a, CharSequence b) {
        @Override
        public boolean equals(Object o) {
            DistanceCache that = (DistanceCache) o;
            // A == A && B == B or A == B && B == A

            if (a.equals(that.a) && b.equals(that.b)) return true;
            return b.equals(that.a) && a.equals(that.b);
        }

        @Override
        public int hashCode() {
            // Controlled collision and buffer overflow
            return a.hashCode() * b.hashCode();
        }
    }

    private record DistanceLimitedCache(CharSequence a, CharSequence b, int limit) {
        @Override
        public boolean equals(Object o) {
            DistanceLimitedCache that = (DistanceLimitedCache) o;

            if (limit != that.limit) return false;

            // A == A && B == B or A == B && B == A

            if (a.equals(that.a) && b.equals(that.b)) return true;
            return b.equals(that.a) && a.equals(that.b);
        }

        @Override
        public int hashCode() {
            // Controlled collision and buffer overflow
            int result = a.hashCode() * b.hashCode();
            result = 31 * result + limit;
            return result;
        }
    }
}
