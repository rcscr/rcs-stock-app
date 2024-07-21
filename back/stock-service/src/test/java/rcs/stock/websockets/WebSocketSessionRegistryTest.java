package rcs.stock.websockets;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
public class WebSocketSessionRegistryTest {

    @Test
    public void testPutAndRemove() {
        // Arrange
        WebSocketSessionRegistry target = new WebSocketSessionRegistry();
        target.put("123", "abc");
        target.put("456", "def");

        // Act
        Collection<String> resultA = target.getStocksWithSubscribers();

        // Assert
        assertThat(resultA).containsExactlyInAnyOrder("abc", "def");

        // Arrange
        target.remove("123");

        // Act
        Collection<String> resultB = target.getStocksWithSubscribers();

        // Assert
        assertThat(resultB).containsExactly("def");
    }
}
