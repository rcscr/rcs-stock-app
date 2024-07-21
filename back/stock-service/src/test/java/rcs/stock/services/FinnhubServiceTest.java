package rcs.stock.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import rcs.stock.models.StockPrice;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class FinnhubServiceTest {

    private RestTemplate restTemplate;

    private FinnhubService target;

    @Before
    public void setup() {
        restTemplate = mock(RestTemplate.class);

        ResponseEntity<FinnhubService.StockResponse[]> responseEntity =
                mock(ResponseEntity.class);

        when(responseEntity.getBody())
                .thenReturn(new FinnhubService.StockResponse[]{
                        new FinnhubService.StockResponse(
                                "USD",
                                "searchable description for stock ABC",
                                "displaySymbol",
                                "figi",
                                "isin",
                                "mic",
                                "ABC",
                                "symbol2",
                                "type")
                });

        when(restTemplate.getForEntity(
                "fh.com/stock/symbol?exchange=US&token=token123",
                FinnhubService.StockResponse[].class))
                .thenReturn(responseEntity);

        target = new FinnhubService("fh.com", "token123", Set.of("US"), restTemplate);
    }

    @Test
    public void testStockExists() {
        // Arrange

        // Act
        boolean shouldExist = target.stockExists("abc");
        boolean shouldNotExist = target.stockExists("def");

        // Assert
        assertThat(shouldExist).isTrue();
        assertThat(shouldNotExist).isFalse();
    }

    @Test
    public void testGetPrice() {
        // Arrange
        ResponseEntity<FinnhubService.QuoteResponse> responseEntity =
                mock(ResponseEntity.class);

        when(responseEntity.getBody())
                .thenReturn(new FinnhubService.QuoteResponse(1d, 2d, 3d, 4d, 5d, 6d, 7d, 8L));

        when(restTemplate.getForEntity(
                "fh.com/quote?symbol=abc&token=token123",
                FinnhubService.QuoteResponse.class))
                .thenReturn(responseEntity);

        // Act
        StockPrice result = target.getPrice("abc");

        // Assert
        assertThat(result).isEqualTo(new StockPrice("ABC", "USD", 1d, 3d));
    }

    @Test
    public void testSearchStocks() {
        // Arrange
        String search = "descriptions"; // not the s which is not present in the object

        // Act
        List<FinnhubService.StockResponse> result = target.searchStocks(search, 10);

        // Assert
        assertThat(result.stream().map(FinnhubService.StockResponse::symbol))
                .containsExactly("ABC");
    }
}
