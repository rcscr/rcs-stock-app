package rcs.stock.controllers;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import rcs.auth.api.AuthService;
import rcs.auth.api.RequestAuthenticationService;
import rcs.auth.api.models.LoginCredentials;
import rcs.stock.models.UserStocks;
import rcs.stock.services.FinnhubService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StocksRestControllerIT {

    private static LoginCredentials testUser = new LoginCredentials(
            "RCS_STOCKS_TEST_USERNAME", "RCS_STOCKS_TEST_PASSWORD");

    @Autowired
    private AuthService authService;

    @Autowired
    private RequestAuthenticationService requestAuthenticationService;

    @Autowired
    private StocksRestController target;

    @Before
    @After
    public void cleanup() {
        HttpServletRequest authenticatedRequest = getAuthenticatedRequest();
        UserStocks result = target.getMyStocks(authenticatedRequest);
        result.getStocks().forEach(stock -> target.unfollowStock(authenticatedRequest, stock));
    }

    @Test
    public void testGetMyStocksFirstTimeUser() {
        // Arrange
        HttpServletRequest authenticatedRequest = getAuthenticatedRequest();

        // Act
        UserStocks result = target.getMyStocks(authenticatedRequest);

        // Assert
        assertThat(result.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(result.getStocks()).isEmpty();
    }

    @Test
    public void testGetMyStocksAfterFollowing() {
        // Arrange
        HttpServletRequest authenticatedRequest = getAuthenticatedRequest();
        target.followStock(authenticatedRequest, "IBM");
        target.followStock(authenticatedRequest, "AAPL");

        // Act
        UserStocks result = target.getMyStocks(authenticatedRequest);

        // Assert
        assertThat(result.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(result.getStocks()).containsExactlyInAnyOrder("IBM", "AAPL");
    }

    @Test
    public void testFollowStock() {
        // Arrange
        HttpServletRequest authenticatedRequest = getAuthenticatedRequest();

        target.followStock(authenticatedRequest, "AAPL");

        // Act
        UserStocks result = target.followStock(authenticatedRequest, "IBM");

        // Assert
        assertThat(result.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(result.getStocks()).containsExactlyInAnyOrder("AAPL", "IBM");
    }

    @Test
    public void testUnfollowStock() {
        // Arrange
        HttpServletRequest authenticatedRequest = getAuthenticatedRequest();

        target.followStock(authenticatedRequest, "AAPL");
        target.followStock(authenticatedRequest, "IBM");

        // Act
        UserStocks result = target.unfollowStock(authenticatedRequest, "IBM");

        // Assert
        assertThat(result.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(result.getStocks()).containsExactlyInAnyOrder("AAPL");
    }

    @Test
    public void testSearch() {
        // Arrange

        // Act
        List<FinnhubService.StockResponse> results = target.searchStocks("apple", 1000);

        // Assert
        results.forEach(result -> {
            boolean somethingMatched = Stream.of(result.symbol(), result.description())
                    .map(String::toLowerCase)
                    .anyMatch(searchableTerm -> searchableTerm.contains("appl"));
            assertThat(somethingMatched).isTrue();
        });
    }

    private HttpServletRequest getAuthenticatedRequest() {
        try {
            authService.register(testUser);
        } catch (Exception e) {
            System.out.println("Test user already registered.");
        }
        String jsessionid = authService.login(testUser).orElseThrow(AssertionError::new);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[] { new Cookie("JSESSIONID", jsessionid)});
        return requestAuthenticationService.authenticate(request);
    }
}
