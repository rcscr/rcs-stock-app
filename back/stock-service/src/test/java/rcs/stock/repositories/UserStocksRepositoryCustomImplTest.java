package rcs.stock.repositories;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import rcs.stock.models.UserStocks;
import rcs.stock.testutils.InMemoryMongoRepositoryTestBase;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class UserStocksRepositoryCustomImplTest extends InMemoryMongoRepositoryTestBase {

    private MongoTemplate mongoTemplate;
    private UserStocksRepositoryCustomImpl target;

    @Before
    public void setup() {
        mongoTemplate = getMongoTemplate();
        target = new UserStocksRepositoryCustomImpl(mongoTemplate);
    }

    @Test
    public void testFollowStock() {
        // Arrange
        UserStocks existing = new UserStocks("raphael", Set.of("AAPL"));

        mongoTemplate.save(existing);

        String username = "raphael";
        String stock = "IBM";

        // Act
        UserStocks updated = target.followStock(username, stock);

        // Assert
        assertThat(updated.getStocks()).containsExactlyInAnyOrder("AAPL", "IBM");
    }

    @Test
    public void testFollowStockUpserts() {
        // Arrange
        String username = "raphael";
        String stock = "IBM";

        // Act
        UserStocks updated = target.followStock(username, stock);

        // Assert
        assertThat(updated.getStocks()).containsExactlyInAnyOrder("IBM");
    }

    @Test
    public void testUnfollowStock() {
        // Arrange
        UserStocks existing = new UserStocks("raphael", Set.of("AAPL", "IBM"));

        mongoTemplate.save(existing);

        String username = "raphael";
        String stock = "IBM";

        // Act
        UserStocks updated = target.unfollowStock(username, stock);

        // Assert
        assertThat(updated.getStocks()).containsExactlyInAnyOrder("AAPL");
    }

    @Test
    public void testGetStocksWithFollowers() {
        // Arrange
        Stream.of(
                new UserStocks("raphael", Set.of("AAPL", "MSFT")),
                new UserStocks("petri", Set.of("IBM")),
                new UserStocks("ramesh", Set.of("MSFT", "GE")),
                new UserStocks("ville", Set.of("GOOGL", "IBM", "MSFT")))
                .forEach(mongoTemplate::save);

        // Act
        Map<String, Long> result = target.getStocksWithFollowers();

        // Assert
        Map<String, Long> expected = Map.of(
                "AAPL", 1L,
                "IBM", 2L,
                "MSFT", 3L,
                "GE", 1L,
                "GOOGL", 1L);

        assertThat(result).isEqualTo(expected);
    }
}
