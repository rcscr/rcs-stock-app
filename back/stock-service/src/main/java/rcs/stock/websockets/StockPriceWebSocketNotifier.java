package rcs.stock.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import rcs.stock.models.StockPrice;
import rcs.stock.services.FinnhubService;

import java.util.HashMap;
import java.util.Map;

@EnableScheduling
@Component
public class StockPriceWebSocketNotifier {

    private final Logger logger = LoggerFactory.getLogger(StockPriceWebSocketNotifier.class);

    private final Map<String, StockPrice> lastStockPrice = new HashMap<>();

    private final WebSocketSessionRegistry webSocketSessionRegistry;
    private final FinnhubService finnhubService;
    private final SimpMessagingTemplate template;

    public StockPriceWebSocketNotifier(
            WebSocketSessionRegistry webSocketSessionRegistry,
            FinnhubService finnhubService,
            SimpMessagingTemplate template) {
        this.webSocketSessionRegistry = webSocketSessionRegistry;
        this.finnhubService = finnhubService;
        this.template = template;
    }

    /**
     * TODO: consider optimizing this to not check for price updates outside trading hours
     */
    @Scheduled(fixedRateString = "${web-socket.stock-price-notify-rate}")
    public void notifyStockPriceChanges() {
        logger.info("Notifying websocket subscribers");

        webSocketSessionRegistry.getStocksWithSubscribers()
                .stream()
                .map(finnhubService::getPrice)
                .filter(stockPrice -> {
                    boolean isEqualToLastPrice = stockPrice.equals(lastStockPrice.get(stockPrice.symbol()));
                    if (isEqualToLastPrice) {
                        logger.info("Price has not changed; not notifying subscribers of " + stockPrice.symbol());
                        return false;
                    } else {
                        logger.info("Price has changed; notifying subscribers of" + stockPrice.symbol());
                        return true;
                    }
                })
                .peek(stockPrice -> lastStockPrice.put(stockPrice.symbol(), stockPrice))
                .forEach(stockPrice -> template.convertAndSend("/topic/stocks/" + stockPrice.symbol(), stockPrice));
    }
}
