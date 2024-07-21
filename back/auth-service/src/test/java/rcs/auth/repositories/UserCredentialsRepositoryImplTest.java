package rcs.auth.repositories;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import rcs.auth.api.models.UserAuthority;
import rcs.auth.models.UserCredentials;
import rcs.auth.testutils.InMemoryDataSourceTestBase;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
public class UserCredentialsRepositoryImplTest extends InMemoryDataSourceTestBase {

    private UserCredentialsRepositoryImpl target;

    @Before
    public void setup() {
        target = new UserCredentialsRepositoryImpl(template);
    }

    @Before
    public void createTable() {
        template.execute("create table " + UserCredentials.tableName +
                " (" + UserCredentials.Fields.username + " varchar, " +
                UserCredentials.Fields.password + " varchar, " +
                UserCredentials.Fields.authority + " int)");
    }

    @After
    public void dropTable() {
        template.execute("drop table " + UserCredentials.tableName);
    }

    @Test
    public void testUpdatePassword() {
        // Arrange
        String username = "username";
        String password = "p455w0rd";
        template.update(
                "INSERT INTO " + UserCredentials.tableName +
                        " (" + UserCredentials.Fields.username + ", " + UserCredentials.Fields.password + ")" +
                        " VALUES (?, ?)",
                username,
                password);

        String newPassword = "n3wP455w0rd";

        // Act
        boolean actual = target.updatePassword(username, newPassword);

        // Assert
        assertThat(actual).isTrue();

        String savedPassword = template.queryForObject(
                "select " + UserCredentials.Fields.password + " from " + UserCredentials.tableName +
                        " where " + UserCredentials.Fields.username + " = '" + username + "'",
                String.class);

        assertThat(savedPassword).isEqualTo(newPassword);
    }

    @Test
    public void testUpdatePasswordUserDoesNotExist() {
        // Arrange
        String username = "username";
        String newPassword = "n3wP455w0rd";

        // Act
        boolean actual = target.updatePassword(username, newPassword);

        // Assert
        assertThat(actual).isFalse();
    }

    public void testUpdateAuthority() {
        // Arrange
        String username = "username";
        UserAuthority authority = UserAuthority.ADMIN;
        template.update(
                "INSERT INTO " + UserCredentials.tableName +
                        " (" + UserCredentials.Fields.username + ", " + UserCredentials.Fields.authority + ")" +
                        " VALUES (?, ?)",
                username,
                authority);

        UserAuthority newAuthority = UserAuthority.USER;

        // Act
        boolean actual = target.updateAuthority(username, newAuthority);

        // Assert
        assertThat(actual).isTrue();

        UserAuthority savedAuthority = template.queryForObject(
                "select " + UserCredentials.Fields.authority + " from " + UserCredentials.tableName +
                        " where " + UserCredentials.Fields.username + " = '" + username + "'",
                UserAuthority.class);

        assertThat(savedAuthority).isEqualTo(newAuthority);
    }

    @Test
    public void testUpdateAuthorityUserDoesNotExist() {
        // Arrange
        String username = "username";
        UserAuthority newAuthority = UserAuthority.ADMIN;

        // Act
        boolean actual = target.updateAuthority(username, newAuthority);

        // Assert
        assertThat(actual).isFalse();
    }
}
