package rcs.stock.utils;

import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
public class TrieTest {

    @Test
    public void testAdd() {
        // Arrange
        Trie<Integer> trie = new Trie<>();

        // Act
        trie.put("Hello, Nomads!", 123);

        // Assert
        assertThat(trie.containsExactly("Hello")).isFalse();
        assertThat(trie.containsExactly("Hello, World!")).isFalse();
        assertThat(trie.containsExactly("Hello, Nomads!")).isTrue();
    }

    @Test
    public void testAddShorterAfter() {
        // Arrange
        Trie<Integer> trie = new Trie<>();

        // Act
        trie.put("123456", 1);
        trie.put("12345", 2);
        trie.put("1234", 3);

        // Assert
        assertThat(trie.containsExactly("123456")).isTrue();
        assertThat(trie.containsExactly("12345")).isTrue();
        assertThat(trie.containsExactly("1234")).isTrue();
    }

    @Test
    public void testRemove() {
        // Arrange
        Trie<Integer> trie = new Trie<>();
        trie.put("Hello, Nomads!", 1);
        trie.put("Hello, World!", 1);

        // Act
        trie.remove("Hello, Nomads!");

        // Assert
        assertThat(trie.matchByPrefix("Hello, Talk")).isEmpty();
        assertThat(trie.containsExactly("Hello, Nomads!")).isFalse();
        assertThat(trie.containsExactly("Hello, World!")).isTrue();
    }

    @Test
    public void testMatchByPrefix() {
        // Arrange
        Trie<Integer> trie = new Trie<>();
        trie.put("Hello, Nomads!", 1);
        trie.put("Hello, World!", 2);
        trie.put("Hi there!", 3);

        // Act
        Map<String, Integer> matchedHello = trie.matchByPrefix("Hello");
        Map<String, Integer> matchedHi = trie.matchByPrefix("Hi");
        Map<String, Integer> matchedH = trie.matchByPrefix("H");
        Map<String, Integer> matchedBlank = trie.matchByPrefix("");

        // Assert
        assertThat(matchedHello).isEqualTo(Map.of(
                "Hello, Nomads!", 1,
                "Hello, World!", 2));

        assertThat(matchedHi).isEqualTo(Map.of(
                "Hi there!", 3));

        assertThat(matchedH).isEqualTo(Map.of(
                "Hello, Nomads!", 1,
                "Hello, World!", 2,
                "Hi there!", 3));

        assertThat(matchedBlank).isEqualTo(Map.of(
                "Hello, Nomads!", 1,
                "Hello, World!", 2,
                "Hi there!", 3));
    }

    @Test
    public void testmatchByExactSubstring() {
        // Arrange
        Trie<Integer> trie = new Trie<>();

        trie.put("abcdef", 1);
        trie.put("defghi", 2);
        trie.put("deghij", 3);
        trie.put("jklmno", 4);
        trie.put("pqrpqs", 5);
        trie.put("tu vw, xyz", 6);
        trie.put("123", 7);

        // Act
        Collection<Trie.SearchResult<Integer>> resultA = trie.matchByExactSubstring("a");       // match a prefix of length 1
        Collection<Trie.SearchResult<Integer>> resultB = trie.matchByExactSubstring("def");     // match a prefix of length > 1
        Collection<Trie.SearchResult<Integer>> resultC = trie.matchByExactSubstring("ghi");     // match a postfix & substring
        Collection<Trie.SearchResult<Integer>> resultD = trie.matchByExactSubstring("jklmno");  // match an entire string
        Collection<Trie.SearchResult<Integer>> resultE = trie.matchByExactSubstring("pqs");     // match after an initial failed attempt
        Collection<Trie.SearchResult<Integer>> resultF = trie.matchByExactSubstring("vw");      // matched whole word
        Collection<Trie.SearchResult<Integer>> resultG = trie.matchByExactSubstring("234");     // only partial match

        // Assert
        assertThat(resultA).containsExactlyInAnyOrder(
                new Trie.SearchResult<>("abcdef", 1, 1, false));

        assertThat(resultB).containsExactlyInAnyOrder(
                new Trie.SearchResult<>("abcdef", 1, 3, false),
                new Trie.SearchResult<>("defghi", 2, 3, false));

        assertThat(resultC).containsExactlyInAnyOrder(
                new Trie.SearchResult<>("defghi", 2, 3, false),
                new Trie.SearchResult<>("deghij", 3, 3, false));

        assertThat(resultD).containsExactlyInAnyOrder(
                new Trie.SearchResult<>("jklmno", 4, 6, true));

        assertThat(resultE).containsExactlyInAnyOrder(
                new Trie.SearchResult<>("pqrpqs", 5, 3, false));

        assertThat(resultF).containsExactlyInAnyOrder(
                new Trie.SearchResult<>("tu vw, xyz", 6, 2, true));

        assertThat(resultG).isEmpty();
    }


    @Test
    public void testmatchByExactSubstringWithLength() {
        // Arrange
        Trie<Integer> trie = new Trie<>();

        trie.put("googl", 1);

        // Act
        List<Trie.SearchResult<Integer>> result = trie.matchBySubstring("google", 5);

        // Assert
        assertThat(result).containsExactly(
                new Trie.SearchResult<>("googl", 1, 5, true));
    }

    @Test
    public void testConcurrency() {
        // Arrange
        Trie<Integer> trie = new Trie<>();
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        String[] randomStrings = Stream.generate(() -> getRandomString(20))
                .distinct()
                .limit(1_000)
                .toArray(String[]::new);

        // Act
        Arrays.stream(randomStrings)
                .map(string -> executorService.submit(() -> trie.put(string, 123)))
                .forEach(this::waitForFuture);

        // Assert
        assertThat(trie.matchByPrefix("").size()).isEqualTo(randomStrings.length);

        // parallel match and remove
        Arrays.stream(randomStrings)
                .map(string -> executorService.submit(() -> {
                    String substring = string.substring(0, 10);
                    Map<String, Integer> matched = trie.matchByPrefix(substring);
                    matched.keySet().forEach(trie::remove);
                }))
                .forEach(this::waitForFuture);

        assertThat(trie.matchByPrefix("").size()).isEqualTo(0);
    }

    private String getRandomString(int length) {
        byte[] array = new byte[length];
        new Random().nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);
    }

    @SneakyThrows
    private void waitForFuture(Future<?> future) {
        future.get();
    }
}
