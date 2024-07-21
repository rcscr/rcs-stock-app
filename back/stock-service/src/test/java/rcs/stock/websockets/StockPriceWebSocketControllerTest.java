package rcs.stock.websockets;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import rcs.stock.models.StockPrice;
import rcs.stock.services.FinnhubService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class StockPriceWebSocketControllerTest {

    private WebSocketSessionRegistry webSocketSessionRegistry;
    private FinnhubService finnhubService;
    private StockPriceWebSocketController target;

    @Before
    public void setup() {
        webSocketSessionRegistry = mock(WebSocketSessionRegistry.class);
        finnhubService = mock(FinnhubService.class);
        target = new StockPriceWebSocketController(webSocketSessionRegistry, finnhubService);
    }

    @Test
    public void testInitialReply() {
        // Arrange
        StockPrice stockPrice = new StockPrice("IBM", "USD", 123d, 0.3d);
        when(finnhubService.getPrice("IBM")).thenReturn(stockPrice);

        // Act
        StockPrice result = target.initialReply("IBM", "sessionid");

        // Assert
        assertThat(result).isEqualTo(stockPrice);
        verify(webSocketSessionRegistry, times(1)).put("sessionid", "IBM");
    }
}
