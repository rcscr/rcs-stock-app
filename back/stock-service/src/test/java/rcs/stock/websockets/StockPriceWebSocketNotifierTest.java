package rcs.stock.websockets;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import rcs.stock.models.StockPrice;
import rcs.stock.services.FinnhubService;

import java.util.Set;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class StockPriceWebSocketNotifierTest {

    private WebSocketSessionRegistry webSocketSessionRegistry;
    private FinnhubService finnhubService;
    private SimpMessagingTemplate template;

    private StockPriceWebSocketNotifier target;

    @Before
    public void setup() {
        webSocketSessionRegistry = mock(WebSocketSessionRegistry.class);
        finnhubService = mock(FinnhubService.class);
        template = mock(SimpMessagingTemplate.class);
        target = new StockPriceWebSocketNotifier(
                webSocketSessionRegistry,
                finnhubService,
                template);
    }

    @Test
    public void testNotifyStockPriceChanges() {
        // Arrange
        when(webSocketSessionRegistry.getStocksWithSubscribers())
                .thenReturn(Set.of("IBM", "AAPL"));

        StockPrice ibmStockPrice = new StockPrice("IBM", "USD", 1d, 0.5d);
        StockPrice aaplStockPrice = new StockPrice("AAPL", "USD", 2d, 0.75d);

        when(finnhubService.getPrice("IBM")).thenReturn(ibmStockPrice);
        when(finnhubService.getPrice("AAPL")).thenReturn(aaplStockPrice);

        // Act
        target.notifyStockPriceChanges();

        // Assert
        verify(template, times(1)).convertAndSend("/topic/stocks/IBM", ibmStockPrice);
        verify(template, times(1)).convertAndSend("/topic/stocks/AAPL", aaplStockPrice);
    }
}
