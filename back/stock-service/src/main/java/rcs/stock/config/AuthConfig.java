package rcs.stock.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import rcs.auth.api.AuthService;
import rcs.auth.api.AuthUtils;
import rcs.auth.api.AuthenticationFilter;
import rcs.auth.api.RequestAuthenticationService;

@Configuration
public class AuthConfig {

    @Autowired
    private ApplicationContext context;

    @Value("${services.auth.baseUrl}")
    private String authServiceBaseUrl;

    @Bean
    public AuthService getAuthService() {
        return new AuthService(authServiceBaseUrl, context.getBean(RestTemplate.class));
    }

    @Bean
    public AuthUtils getAuthUtils() {
        return new AuthUtils();
    }

    @Bean
    public AuthenticationFilter getAuthenticationFilter() {
        return new AuthenticationFilter(context.getBean(RequestAuthenticationService.class));
    }

    @Bean
    public RequestAuthenticationService getRequestAuthenticationService() {
        return new RequestAuthenticationService(context.getBean(AuthService.class));
    }
}
