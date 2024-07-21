package rcs.auth.api;

import rcs.auth.api.models.AuthenticatedUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class AuthenticatedHttpServletRequest extends HttpServletRequestWrapper {

    private final AuthenticatedUser user;

    public AuthenticatedHttpServletRequest(HttpServletRequest request, AuthenticatedUser user) {
        super(request);
        this.user = user;
    }

    public AuthenticatedUser getLoggedInUser() {
        return user;
    }
}
