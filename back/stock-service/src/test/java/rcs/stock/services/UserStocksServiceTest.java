package rcs.stock.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import rcs.stock.models.StockWithFollowers;
import rcs.stock.models.UserStocks;
import rcs.stock.repositories.UserStocksRepository;
import rcs.stock.services.exceptions.StockNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class UserStocksServiceTest {

    private UserStocksRepository userStocksRepository;
    private FinnhubService finnhubService;
    private UserStocksService target;

    @Before
    public void setup() {
        userStocksRepository = mock(UserStocksRepository.class);
        finnhubService = mock(FinnhubService.class);
        target = new UserStocksService(userStocksRepository, finnhubService);
    }

    @Test
    public void testGetUserStocks() {
        // Arrange
        UserStocks userStocks = new UserStocks("username", Set.of("IBM", "AAPL"));
        when(userStocksRepository.findById("username"))
                .thenReturn(Optional.of(userStocks));

        // Act
        UserStocks result = target.getUserStocks("username");

        // Assert
        assertThat(result).isEqualTo(userStocks);
    }

    @Test
    public void testGetUserStocksNonExistant() {
        // Arrange
        when(userStocksRepository.findById("username"))
                .thenReturn(Optional.empty());

        // Act
        UserStocks result = target.getUserStocks("username");

        // Assert
        assertThat(result).isEqualTo(new UserStocks("username", Set.of()));
    }

    @Test
    public void testFollowStock() {
        // Arrange
        when(finnhubService.stockExists("abc")).thenReturn(true);

        // Act
        target.followStock("username", "abc");

        // Assert
        verify(userStocksRepository, times(1)).followStock("username", "abc");
    }

    @Test
    public void testFollowStockNonExistant() {
        // Arrange
        when(finnhubService.stockExists("abc")).thenReturn(false);

        // Act & Assert
        assertThrows(
                StockNotFoundException.class,
                () -> target.followStock("username", "abc"));
    }

    @Test
    public void testUnfollowStock() {
        // Arrange

        // Act
        target.unfollowStock("username", "abc");

        // Assert
        verify(userStocksRepository, times(1)).unfollowStock("username", "abc");
    }

    @Test
    public void testGetMostPopularStocks() {
        // Arrange
        when(userStocksRepository.getStocksWithFollowers())
                .thenReturn(Map.of("a", 1L, "b", 3L, "c", 2L));

        // Act
        List<StockWithFollowers> result = target.getMostPopularStocks();

        // Assert
        assertThat(result).containsExactly(
                new StockWithFollowers("b", 3L),
                new StockWithFollowers("c", 2L),
                new StockWithFollowers("a", 1L));
    }
}
