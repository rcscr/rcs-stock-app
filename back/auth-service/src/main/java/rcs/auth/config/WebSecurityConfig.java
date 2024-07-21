package rcs.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import rcs.auth.security.EndpointSecurity;
import rcs.auth.security.RestAuthenticationEntryPoint;
import rcs.auth.services.UserCredentialsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private AuthenticationFailureHandler failureHandler;

    @Autowired
    private EndpointSecurity endpointSecurity;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserCredentialsService userCredentialsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .csrf()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                .authorizeRequests()

                .antMatchers(HttpMethod.GET, "/authenticate")
                .authenticated()

                .antMatchers(HttpMethod.POST, "/register")
                .permitAll()

                .antMatchers(HttpMethod.PUT, "/users/{username}/password")
                .access("@endpointSecurity.canUpdatePassword(authentication, #username)")

                .antMatchers(HttpMethod.PUT, "/users/{username}/authority")
                .hasAuthority("ADMIN")

                .antMatchers(HttpMethod.DELETE, "/users/{username}")
                .hasAuthority("ADMIN")

                .and()
                .formLogin()
                .successHandler((request, response, authentication) -> { }) // disables redirect
                .failureHandler(failureHandler)
                .and()
                .logout()
                .logoutSuccessHandler((request, response, authentication) -> { });
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userCredentialsService).passwordEncoder(encoder);
    }
}