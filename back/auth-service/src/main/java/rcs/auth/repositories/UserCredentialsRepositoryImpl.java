package rcs.auth.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import rcs.auth.api.models.UserAuthority;
import rcs.auth.models.UserCredentials;

import java.util.Map;

@Repository
public class UserCredentialsRepositoryImpl implements UserCredentialsRepositoryCustom {

    private static final Map<UserAuthority, Integer> authorityToDbValue = Map.of(
            UserAuthority.USER, 0,
            UserAuthority.ADMIN, 1);

    private JdbcTemplate template;

    public UserCredentialsRepositoryImpl(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public boolean updatePassword(String username, String encodedPassword) {
        return updateSuccessful(
                template.update(
                        updateQuery(
                                username,
                                UserCredentials.Fields.password,
                                encodedPassword)));
    }

    @Override
    public boolean updateAuthority(String username, UserAuthority authority) {
        return updateSuccessful(
                template.update(
                        updateQuery(
                                username,
                                UserCredentials.Fields.authority,
                                authorityToDbValue.get(authority))));
    }

    private String updateQuery(String username, String field, Object newValue) {
        return "update ${table} c set c.${fieldToUpdate} = '${newValue}' where c.${fieldToMatch} = '${valueToMatch}'"
                .replace("${table}", UserCredentials.tableName)
                .replace("${fieldToUpdate}", field)
                .replace("${newValue}", newValue.toString())
                .replace("${fieldToMatch}", UserCredentials.Fields.username)
                .replace("${valueToMatch}", username);
    }

    private boolean updateSuccessful(int updateCount) {
        return updateCount == 1;
    }
}
