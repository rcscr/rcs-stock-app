package rcs.auth.api.models;

import java.util.Arrays;
import java.util.Set;

public enum UserAuthority {
    USER("USER"),
    ADMIN("USER", "ADMIN");

    private Set<String> roles;

    UserAuthority(String... roles) {
        this.roles = Set.copyOf(Arrays.asList(roles));
    }

    public Set<String> getRoles() {
        return roles;
    }
}
