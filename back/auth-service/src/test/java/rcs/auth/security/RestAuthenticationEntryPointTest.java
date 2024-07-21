package rcs.auth.security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class RestAuthenticationEntryPointTest {

    private RestAuthenticationEntryPoint target;

    @Before
    public void setup() {
        target = new RestAuthenticationEntryPoint();
    }

    @Test
    public void testCommence() throws IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException exception = mock(AuthenticationException.class);
        when(exception.getMessage())
                .thenReturn("fail");

        // Act
        target.commence(request, response, exception);

        // Assert
        verify(response).sendError(HttpStatus.UNAUTHORIZED.value(), "fail");
    }
}
