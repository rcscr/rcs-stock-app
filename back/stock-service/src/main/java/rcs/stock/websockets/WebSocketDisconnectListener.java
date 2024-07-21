package rcs.stock.websockets;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Optional;

@Component
public class WebSocketDisconnectListener {

    private final WebSocketSessionRegistry webSocketSessionRegistry;

    public WebSocketDisconnectListener(WebSocketSessionRegistry webSocketSessionRegistry) {
        this.webSocketSessionRegistry = webSocketSessionRegistry;
    }

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        Optional.ofNullable(event.getMessage().getHeaders().get("simpSessionId"))
                .map(Object::toString)
                .ifPresent(webSocketSessionRegistry::remove);
    }
}