package com.Jeka8833.DocumentBeautifier.search;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.nio.file.Path;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class Search {

    private final @Nullable Collection<DBElement> database;
    private @Nullable Set<DBElement> searched = null;

    public Search(@Nullable Collection<DBElement> database) {
        this.database = database;
    }

    /**
     * Add a list of files or folder to be searched. If the constructor collection is null or empty,
     * this method does nothing.
     *
     * @param files Accepts files or folders to be searched
     * @return This object
     * @throws NullPointerException If the 'files' parameter is null
     */
    @Contract("_ -> this")
    public Search addSearchedFiles(@NotNull Path... files) {
        if (database == null) return this;
        if (searched == null) searched = new HashSet<>();

        for (DBElement row : database) {
            for (Path path : files) {
                if (row.sheet().getReader().getInputFile().startsWith(path)) {
                    searched.add(row);
                    break;
                }
            }
        }
        return this;
    }

    /**
     * Search for and remove a path from the files list.
     *
     * @param path File to be deleted
     * @return This object
     * @throws NullPointerException If the 'path' parameter is null
     */
    @Contract("_ -> this")
    public Search removeSearchedFile(@NotNull Path path) {
        if (searched == null) return this;

        searched.removeIf(dbElement -> dbElement.sheet().getReader().getInputFile().startsWith(path));
        return this;
    }

    /**
     * Clear the list of searched files. After the list was cleared, all documents were searched.
     *
     * @return This object
     */
    @Contract("->this")
    public Search clearSearchedFiles() {
        searched = null;
        return this;
    }

    /**
     * Only search for a full element match. If the constructor collection is null or empty,
     * this method will return new empty {@link HashMap}.
     *
     * @return Elements found
     */
    @Contract("->new")
    public Map<String, List<DBElement>> search() {
        if (database == null) return new HashMap<>();

        Map<String, List<DBElement>> result = database.stream().collect(Collectors.groupingBy(DBElement::element));

        if (searched == null) {
            result.entrySet().removeIf(entry -> entry.getValue().size() < 2);
        } else {
            result.entrySet().removeIf(entry -> {
                if (entry.getValue().size() < 2) return true;

                for (DBElement row : entry.getValue()) {
                    if (searched.contains(row)) return false;
                }
                return true;
            });
        }
        return result;
    }

    /**
     * Search for elements that satisfy the comparison function.
     *
     * @param compareFunction The function that should compare two texts and return true or false
     * @return Found elements
     * @throws NullPointerException If the 'compareFunction' parameter is null
     */
    @Contract("_->new")
    public Map<String, List<DBElement>> search(@NotNull BiPredicate<String, String> compareFunction) {
        if (database == null) return new HashMap<>();

        Collection<DBElement> searchedDatabase = searched == null ? database : searched;

        Map<String, List<DBElement>> result = new HashMap<>();
        for (DBElement row : searchedDatabase) {
            if (result.containsKey(row.element())) continue;

            List<DBElement> foundRows = new ArrayList<>();
            foundRows.add(row);

            for (DBElement searchedRow : database) {
                if (compareFunction.test(row.element(), searchedRow.element())) foundRows.add(searchedRow);
            }

            if (foundRows.size() > 1) result.put(row.element(), foundRows);
        }
        return result;
    }

    /**
     * Search for elements that satisfy the comparison function. Elements with a full match will not be added.
     *
     * @param compareFunction The function that should compare two texts and return true or false
     * @return Found elements
     * @throws NullPointerException If the 'compareFunction' parameter is null
     */
    @Contract("_->new")
    public Map<String, List<DBElement>> searchIgnoreFullMatch(@NotNull BiPredicate<String, String> compareFunction) {
        return search(compareFunction.and((s, s2) -> !s.equals(s2)));
    }

    /**
     * Returns true if the constructor collection contains no elements or null.
     *
     * @return true if the constructor collection contains no elements or null
     */
    @Contract(pure = true)
    public boolean isEmpty() {
        return database == null || database.isEmpty();
    }

    /**
     * Returns the number of elements in the constructor collection.
     *
     * @return the number of elements in the constructor collection. If the constructor collection is null,
     * 0 will be returned
     */
    @Contract(pure = true)
    @Range(from = 0, to = Integer.MAX_VALUE)
    public int sizeDatabase() {
        if (database == null) return 0;

        return database.size();
    }
}
