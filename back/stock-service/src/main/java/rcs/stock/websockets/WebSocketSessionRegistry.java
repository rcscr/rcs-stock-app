package rcs.stock.websockets;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WebSocketSessionRegistry {

    private final Map<String, String> sessionsWithStock = new HashMap<>();

    public void put(String sessionId, String stock) {
        sessionsWithStock.put(sessionId, stock);
    }

    public void remove(String sessionId) {
        sessionsWithStock.remove(sessionId);
    }

    public Collection<String> getStocksWithSubscribers() {
        return sessionsWithStock.values();
    }
}
