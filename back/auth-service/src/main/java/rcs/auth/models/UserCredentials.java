package rcs.auth.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import rcs.auth.api.models.UserAuthority;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
public class UserCredentials {

    public static final String tableName = "user_credentials";

    public static final class Fields {
        public static final String username = "username";
        public static final String password = "password";
        public static final String authority = "authority";
    }

    @Id
    @Length(max = 32)
    private String username;
    private String password;
    private UserAuthority authority;
}
