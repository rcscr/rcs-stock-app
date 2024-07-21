package rcs.auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UsernameNotFoundException extends org.springframework.security.core.userdetails.UsernameNotFoundException {

    public UsernameNotFoundException(String username) {
        super("Username " + username + " not found.");
    }
}
