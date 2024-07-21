package rcs.auth.api;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class AuthenticationFilter implements Filter {

    private final RequestAuthenticationService requestAuthenticationService;

    public AuthenticationFilter(RequestAuthenticationService requestAuthenticationService) {
        this.requestAuthenticationService = requestAuthenticationService;
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest authenticated =
                requestAuthenticationService.authenticate((HttpServletRequest) request);
        chain.doFilter(authenticated, response);
    }
}
