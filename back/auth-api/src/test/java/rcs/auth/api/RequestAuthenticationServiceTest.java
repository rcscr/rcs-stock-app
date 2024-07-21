package rcs.auth.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import rcs.auth.api.models.AuthenticatedUser;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequestAuthenticationServiceTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private RequestAuthenticationService target;

    @Test
    public void testAuthenticate() {
        // Arrange
        String authToken = "123456789";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies())
                .thenReturn(new Cookie[]{ new Cookie("JSESSIONID", authToken)});

        AuthenticatedUser user = mock(AuthenticatedUser.class);
        when(authService.authenticate(authToken))
                .thenReturn(ResponseEntity.ok().body(user));

        // Act
        HttpServletRequest actual = target.authenticate(request);

        // Assert
        assertThat(actual).isExactlyInstanceOf(AuthenticatedHttpServletRequest.class);
        assertThat(((AuthenticatedHttpServletRequest) actual).getLoggedInUser()).isEqualTo(user);
    }

    @Test
    public void testAuthenticateNoCookies() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies())
                .thenReturn(null);

        // Act
        HttpServletRequest actual = target.authenticate(request);

        // Assert
        assertThat(actual).isNotInstanceOf(AuthenticatedHttpServletRequest.class);
    }
}
