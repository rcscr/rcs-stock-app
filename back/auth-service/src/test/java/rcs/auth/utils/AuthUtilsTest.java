package rcs.auth.utils;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import rcs.auth.services.UserCredentialsService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class AuthUtilsTest {

    private UserCredentialsService userCredentialsService;
    private AuthUtils target;

    @Before
    public void setup() {
        userCredentialsService = mock(UserCredentialsService.class);
        target = new AuthUtils(userCredentialsService);
    }

    @Test
    public void testTryGetLoggedInUser() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        User user = mock(User.class);
        when(authentication.getPrincipal())
                .thenReturn(user);

        // Act
        Optional<User> actual = target.tryGetLoggedInUser(authentication);

        // Assert
        assertThat(actual.get()).isSameAs(user);
    }

    @Test
    public void testTryGetLoggedInUserFails() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        String user = "anonymousUser";
        when(authentication.getPrincipal())
                .thenReturn(user);

        // Act
        Optional<User> actual = target.tryGetLoggedInUser(authentication);

        // Assert
        assertThat(actual).isEmpty();
    }

    @Test
    @Parameters({
            "ADMIN | true",
            "USER | false",
            "OTHER | false",
    })
    public void testUserIdIsAdmin(String role, boolean expected) {
        // Arrange
        User user = mock(User.class);
        when(user.getAuthorities())
                .thenReturn(List.of(new SimpleGrantedAuthority(role)));

        String username = "username";
        when(userCredentialsService.loadUserByUsername(username))
                .thenReturn(user);

        // Act
        boolean actual = target.isAdmin(user);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Parameters({
            "ADMIN | true",
            "USER | false",
            "OTHER | false",
    })
    public void testUserIsAdmin(String role, boolean expected) {
        // Arrange
        User user = mock(User.class);
        when(user.getAuthorities())
                .thenReturn(List.of(new SimpleGrantedAuthority(role)));

        // Act
        boolean actual = target.isAdmin(user);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
