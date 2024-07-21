package rcs.auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FailedToUpdateUserException extends RuntimeException {

    public FailedToUpdateUserException(String username, String field) {
        super("Failed to update field '" + field + "' for user " + username + ".");
    }
}
