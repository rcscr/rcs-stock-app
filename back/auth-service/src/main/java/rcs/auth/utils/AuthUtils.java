package rcs.auth.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import rcs.auth.services.UserCredentialsService;

import java.util.Collection;
import java.util.Optional;

@Service
public class AuthUtils {

    private UserCredentialsService userCredentialsService;

    public AuthUtils(UserCredentialsService userCredentialsService) {
        this.userCredentialsService = userCredentialsService;
    }

    public Optional<User> tryGetLoggedInUser() {
        return tryGetLoggedInUser(SecurityContextHolder.getContext().getAuthentication());
    }

    public Optional<User> tryGetLoggedInUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return Optional.of((User) principal);
        } else {
            return Optional.empty();
        }
    }

    public boolean isAdmin(String username) {
        return isAdmin(userCredentialsService.loadUserByUsername(username).getAuthorities());
    }

    public boolean isAdmin(User user) {
        return isAdmin(user.getAuthorities());
    }

    private boolean isAdmin(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(Object::toString)
                .anyMatch(role -> role.equals("ADMIN"));
    }
}
