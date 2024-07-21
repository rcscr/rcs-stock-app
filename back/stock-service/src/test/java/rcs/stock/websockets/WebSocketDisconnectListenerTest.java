package rcs.stock.websockets;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class WebSocketDisconnectListenerTest {

    private WebSocketSessionRegistry webSocketSessionRegistry;
    private WebSocketDisconnectListener target;

    @Before
    public void setup() {
        webSocketSessionRegistry = mock(WebSocketSessionRegistry.class);
        target = new WebSocketDisconnectListener(webSocketSessionRegistry);
    }

    @Test
    public void testHandleWebSocketDisconnect() {
        // Arrange
        SessionDisconnectEvent event = mock(SessionDisconnectEvent.class);
        Message<byte[]> message = mock(Message.class);
        MessageHeaders headers = mock(MessageHeaders.class);
        when(headers.get("simpSessionId")).thenReturn("sessionid123");
        when(message.getHeaders()).thenReturn(headers);
        when(event.getMessage()).thenReturn(message);

        // Act
        target.handleWebSocketDisconnect(event);

        // Assert
        verify(webSocketSessionRegistry, times(1)).remove("sessionid123");
    }
}
