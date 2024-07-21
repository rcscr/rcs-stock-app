package rcs.auth.repositories;

import rcs.auth.api.models.UserAuthority;

public interface UserCredentialsRepositoryCustom {

    boolean updatePassword(String username, String encodedPassword);
    boolean updateAuthority(String username, UserAuthority authority);
}
