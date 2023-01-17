package com.Jeka8833.DocumentBeautifier;

import com.Jeka8833.DocumentBeautifier.util.LevenshteinDistance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheSystem {

    private static final Map<String, String[]> CACHE_SPLIT_NAMES = new ConcurrentHashMap<>();
    private static final Map<DistanceCache, Integer> CACHE_DISTANCE = new ConcurrentHashMap<>();
    private static final Map<DistanceLimitedCache, Integer> CACHE_DISTANCE_LIMITED = new ConcurrentHashMap<>();

    public static boolean compareName(String name1, String name2, int mistakes) {
        String[] name1Array = CACHE_SPLIT_NAMES.computeIfAbsent(name1, s -> s.split("[.,\\s]+"));
        String[] name2Array = CACHE_SPLIT_NAMES.computeIfAbsent(name2, s -> s.split("[.,\\s]+"));

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
        return CACHE_DISTANCE_LIMITED.computeIfAbsent(new DistanceLimitedCache(left, right, threshold),
                distanceLimitedCache -> LevenshteinDistance.limitedCompare(
                        distanceLimitedCache.a, distanceLimitedCache.b, distanceLimitedCache.limit));
    }

    public static int unlimitedCompare(CharSequence left, CharSequence right) {
        return CACHE_DISTANCE.computeIfAbsent(new DistanceCache(left, right),
                distanceLimitedCache -> LevenshteinDistance.unlimitedCompare(
                        distanceLimitedCache.a, distanceLimitedCache.b));
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
