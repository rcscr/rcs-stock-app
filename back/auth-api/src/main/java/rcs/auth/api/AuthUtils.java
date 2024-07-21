package rcs.auth.api;

import rcs.auth.api.models.AuthenticatedUser;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Optional;

public class AuthUtils {

    public Optional<AuthenticatedUser> tryGetLoggedInUser(ServletRequest request) {
        if (request instanceof AuthenticatedHttpServletRequest) {
            return Optional.of(((AuthenticatedHttpServletRequest) request).getLoggedInUser());
        }
        if (request instanceof HttpServletRequestWrapper) {
            return tryGetLoggedInUser(((HttpServletRequestWrapper) request).getRequest());
        }
        return Optional.empty();
    }

    public boolean isAdmin(AuthenticatedUser user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.equals("ADMIN"));
    }
}
