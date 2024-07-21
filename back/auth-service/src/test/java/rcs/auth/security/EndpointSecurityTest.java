package rcs.auth.security;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import rcs.auth.utils.AuthUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class EndpointSecurityTest {

    private AuthUtils authUtils;
    private EndpointSecurity target;

    @Before
    public void setup() {
        authUtils = mock(AuthUtils.class);
        target = new EndpointSecurity(authUtils);
    }

    @Test
    @Parameters({
            "true | other | true",
            "false | username | true",
            "false | other | false"
    })
    public void testCanUpdatePassword(
            boolean requesterIsAdmin,
            String requesterUsername,
            boolean expected) {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        String username = "username";

        User requester = mock(User.class);
        when(authUtils.tryGetLoggedInUser(authentication))
                .thenReturn(Optional.of(requester));

        when(authUtils.isAdmin(requester))
                .thenReturn(requesterIsAdmin);

        when(requester.getUsername())
                .thenReturn(requesterUsername);

        // Act
        boolean actual = target.canUpdatePassword(authentication, username);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
