package rcs.auth.api;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import rcs.auth.api.models.AuthenticatedUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class AuthUtilsTest {

    private AuthUtils target;

    @Before
    public void setup() {
        target = new AuthUtils();
    }

    @Test
    public void testTryGetLoggedInUserRequestIsAuthenticated() {
        // Arrange
        AuthenticatedHttpServletRequest authenticatedRequest = mock(AuthenticatedHttpServletRequest.class);
        AuthenticatedUser loggedInUser = mock(AuthenticatedUser.class);

        when(authenticatedRequest.getLoggedInUser())
                .thenReturn(loggedInUser);

        // Act
        Optional<AuthenticatedUser> actual = target.tryGetLoggedInUser(authenticatedRequest);

        // Assert
        assertThat(actual.get()).isSameAs(loggedInUser);
    }

    @Test
    public void testTryGetLoggedInUserRequestIsWrapperAndDelegateIsAuthenticated() {
        // Arrange
        HttpServletRequestWrapper request = mock(HttpServletRequestWrapper.class);
        AuthenticatedHttpServletRequest authenticatedRequest = mock(AuthenticatedHttpServletRequest.class);
        AuthenticatedUser loggedInUser = mock(AuthenticatedUser.class);

        when(request.getRequest())
                .thenReturn(authenticatedRequest);

        when(authenticatedRequest.getLoggedInUser())
                .thenReturn(loggedInUser);

        // Act
        Optional<AuthenticatedUser> actual = target.tryGetLoggedInUser(request);

        // Assert
        assertThat(actual.get()).isSameAs(loggedInUser);
    }

    @Test
    public void testTryGetLoggedInUserRequestIsWrapperAndDelegateIsNotAuthenticated() {
        // Arrange
        HttpServletRequestWrapper request = mock(HttpServletRequestWrapper.class);
        HttpServletRequest delegateRequest = mock(HttpServletRequest.class);

        when(request.getRequest())
                .thenReturn(delegateRequest);

        // Act
        Optional<AuthenticatedUser> actual = target.tryGetLoggedInUser(request);

        // Assert
        assertThat(actual).isEmpty();
    }

    @Test
    public void testTryGetLoggedInUserRequestIsNotAuthenticated() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);

        // Act
        Optional<AuthenticatedUser> actual = target.tryGetLoggedInUser(request);

        // Assert
        assertThat(actual).isEmpty();
    }

    @Test
    @Parameters({
            "ADMIN | true",
            "USER | false"
    })
    public void testIsAdmin(String role, boolean expected) {
        // Arrange
        AuthenticatedUser user = new AuthenticatedUser(
                "username",
                Set.of(role, "OTHER"));

        // Act
        boolean actual = target.isAdmin(user);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}