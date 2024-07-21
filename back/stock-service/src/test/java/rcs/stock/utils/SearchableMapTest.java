package rcs.stock.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
public class SearchableMapTest {

    public record Dummy(String a, String b, String c) {
        public Collection<String> getSearch() {
            return List.of(a, c);
        }
    }

    @Test
    public void testMatchBySubstring() {
        // Arrange
        SearchableMap<Integer, Dummy> target = new SearchableMap<>(Dummy::getSearch);

        Dummy dummy0 = new Dummy("abc", "def", "ghi");
        Dummy dummy1 = new Dummy("abc", "jkl", "mno");
        Dummy dummy2 = new Dummy("abz", "pqr", "abc");

        target.put(1, dummy0); // should get overriden and unindexed below
        target.put(1, dummy1);
        target.put(2, dummy2);

        // Act
        List<Dummy> resultA = target.searchBySubstring("abc", 2);
        List<Dummy> resultB = target.searchBySubstring("mn0", 2);
        List<Dummy> resultC = target.searchBySubstring("pqr", 2);

        // Assert
        assertThat(resultA).containsExactly(dummy1, dummy2);
        assertThat(resultB).containsExactly(dummy1);
        assertThat(resultC).isEmpty();
    }
}
