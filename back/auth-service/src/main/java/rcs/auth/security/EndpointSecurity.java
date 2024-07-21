package rcs.auth.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import rcs.auth.utils.AuthUtils;

@Component
public class EndpointSecurity {

    private AuthUtils authUtils;

    public EndpointSecurity(AuthUtils authUtils) {
        this.authUtils = authUtils;
    }

    public boolean canUpdatePassword(Authentication authentication, String username) {
        return authUtils.tryGetLoggedInUser(authentication)
                .map(user -> authUtils.isAdmin(user) || user.getUsername().equals(username))
                .orElse(false);
    }
}
