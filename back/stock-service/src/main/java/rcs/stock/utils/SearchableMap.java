package rcs.stock.utils;

import com.rcs.trie.FuzzyMatchingStrategy;
import com.rcs.trie.Trie;
import com.rcs.trie.TrieSearchResult;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SearchableMap<K, V> {

    private final Map<K, V> map = new HashMap<>();

    // maps search terms to a set of keys in the map above
    private final Trie<Set<K>> trie = new Trie<>();

    // extracts search terms to be indexed in the trie
    private final Function<V, Collection<String>> searchTermsExtractor;

    public SearchableMap(Function<V, Collection<String>> searchTermsExtractor) {
        this.searchTermsExtractor = searchTermsExtractor;
    }

    public void put(K key, V value) {
        V previousValue = map.put(key, value);
        removeIndex(previousValue);
        addIndex(value, key);
    }

    public V get(K key) {
        return map.get(key);
    }

    public List<V> searchBySubstringFuzzy(String search, int errorTolerance) {
        return trie.matchBySubstringFuzzy(search, errorTolerance, FuzzyMatchingStrategy.LIBERAL)
                .stream()
                .map(TrieSearchResult::getValue)
                .flatMap(Collection::stream)
                .distinct()
                .map(map::get)
                .collect(Collectors.toList());
    }

    private void removeIndex(V value) {
        Optional.ofNullable(value)
                .ifPresent(v -> searchTermsExtractor.apply(v)
                        .forEach(trie::remove));
    }

    private void addIndex(V value, K key) {
        searchTermsExtractor.apply(value)
                .forEach(searchTerm -> {
                    Set<K> newKeys = Optional.ofNullable(trie.getExactly(searchTerm))
                            .orElse(new HashSet<>());
                    newKeys.add(key);
                    trie.put(searchTerm, newKeys);
                });
    }
}
