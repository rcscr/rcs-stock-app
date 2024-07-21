package rcs.stock.websockets;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import rcs.stock.models.StockPrice;
import rcs.stock.services.FinnhubService;

@Controller
public class StockPriceWebSocketController {

    private final WebSocketSessionRegistry webSocketSessionRegistry;
    private final FinnhubService finnhubService;

    public StockPriceWebSocketController(
            WebSocketSessionRegistry webSocketSessionRegistry,
            FinnhubService finnhubService) {
        this.webSocketSessionRegistry = webSocketSessionRegistry;
        this.finnhubService = finnhubService;
    }

    @SubscribeMapping("/topic/stocks/{symbol}")
    public StockPrice initialReply(@DestinationVariable String symbol, @Header("simpSessionId") String sessionId) {
        webSocketSessionRegistry.put(sessionId, symbol);
        return finnhubService.getPrice(symbol);
    }
}
