package rcs.auth.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import rcs.auth.api.models.AuthenticatedUser;
import rcs.auth.api.models.LoginCredentials;
import rcs.auth.api.models.UpdateAuthorityRequest;
import rcs.auth.api.models.UpdatePasswordRequest;
import rcs.auth.services.UserCredentialsService;
import rcs.auth.utils.AuthUtils;

import java.util.stream.Collectors;

@RestController
public class AuthController {

    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private UserCredentialsService userCredentialsService;

    @PostMapping(
            path = "/register",
            consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
    public ResponseEntity<Void> createUser(LoginCredentials request) {
        userCredentialsService.save(request);
        return ResponseEntity.ok()
                .build();
    }

    @GetMapping("/authenticate")
    public ResponseEntity<AuthenticatedUser> getLoggedInUser() {
        return authUtils.tryGetLoggedInUser()
                .map(user -> new AuthenticatedUser(
                            user.getUsername(),
                            user.getAuthorities().stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .collect(Collectors.toSet())))
                .map(authenticatedUser -> ResponseEntity.ok().body(authenticatedUser))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PutMapping("/users/{username}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable String username,
            @RequestBody UpdatePasswordRequest request) {
        userCredentialsService.updatePassword(username, request.getPassword());
        return ResponseEntity.ok()
                .build();
    }

    @PutMapping("/users/{username}/authority")
    public ResponseEntity<Void> updateAuthority(
            @PathVariable String username,
            @RequestBody UpdateAuthorityRequest request) {
        userCredentialsService.updateAuthority(username, request.getAuthority());
        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/users/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        userCredentialsService.delete(username);
        return ResponseEntity.ok()
                .build();
    }
}
