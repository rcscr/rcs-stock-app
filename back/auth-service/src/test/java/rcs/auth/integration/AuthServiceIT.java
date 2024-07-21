package rcs.auth.integration;

import net.bytebuddy.utility.RandomString;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import rcs.auth.api.AuthService;
import rcs.auth.api.models.AuthenticatedUser;
import rcs.auth.api.models.LoginCredentials;
import rcs.auth.api.models.UserAuthority;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AuthServiceIT {

    private static final LoginCredentials admin =
            new LoginCredentials("testAdmin", "password");

    private static final LoginCredentials userA =
            new LoginCredentials(RandomString.make(), RandomString.make());

    private static final LoginCredentials userB =
            new LoginCredentials(RandomString.make(), RandomString.make());

    @Value("${server.port}")
    private int port;

    private AuthService target;

    @Before
    public void setup() {
        var jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON));

        var restTemplate = new RestTemplateBuilder()
                .additionalMessageConverters(new FormHttpMessageConverter())
                .additionalMessageConverters(jsonConverter)
                .build();

        target = new AuthService("http://localhost:" + port, restTemplate);
    }

    // unlike @After, this also runs when exceptions are thrown inside test methods
    @Rule
    public TestRule watchman = new TestWatcher() {
        @Override
        protected void finished(Description ignored) {
            Stream.of(userA, userB).forEach(user ->
                    assertThat(target.delete(admin, user.getUsername()).getStatusCodeValue())
                            .satisfiesAnyOf(
                                    status -> assertThat(status).isEqualTo(200),
                                    status -> assertThat(status).isEqualTo(404)));
        }
    };

    @Test
    public void testGetLoggedInUser() {
        // Arrange
        target.register(userA);

        // Act
        ResponseEntity<AuthenticatedUser> actual = target.authenticate(userA);

        // Assert
        assertThat(actual.getBody().getUsername()).isEqualTo(userA.getUsername());
    }

    @Test
    public void testCreateUser() {
        // Arrange

        // Act
        ResponseEntity<Void> response = target.register(userA);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(target.login(userA)).isNotNull();
    }

    @Test
    public void testUpdateOwnPassword() {
        // Arrange
        target.register(userA);
        String newPassword = "newPassword";

        // Act
        ResponseEntity<Void> response = target.updatePassword(userA, userA.getUsername(), newPassword);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        // old password no longer works
        assertThat(target.login(userA)).isEmpty();

        // new password works
        assertThat(target.login(new LoginCredentials(userA.getUsername(), newPassword))).isNotNull();
    }

    @Test
    public void testUpdateOthersPassword() {
        // Arrange
        target.register(userA);
        target.register(userB);
        String newPassword = "newPassword";

        // Act
        ResponseEntity<Void> response = target.updatePassword(userA, userB.getUsername(), newPassword);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(403);

        // potential hacking victim's password hasn't changed
        assertThat(target.login(userB)).isNotNull();
    }

    @Test
    public void testUpdatePasswordRequesterIsAdmin() {
        // Arrange
        target.register(userA);
        String newPassword = "newPassword";

        // Act
        ResponseEntity<Void> response = target.updatePassword(admin, userA.getUsername(), newPassword);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        // old password no longer works
        assertThat(target.login(userA)).isEmpty();

        // new password works
        assertThat(target.login(new LoginCredentials(userA.getUsername(), newPassword))).isNotNull();
    }

    @Test
    public void testUpdateAuthorityRequestIsUser() {
        // Arrange
        target.register(userA);
        UserAuthority newAuthority = UserAuthority.ADMIN;

        // Act
        ResponseEntity<Void> response = target.updateAuthority(userA, userA.getUsername(), newAuthority);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(403);

        // authority hasn't changed
        assertThat(target.authenticate(userA).getBody().getRoles())
                .hasSameElementsAs(UserAuthority.USER.getRoles());
    }

    @Test
    public void testUpdateAuthorityRequesterIsAdmin() {
        // Arrange
        target.register(userA);
        UserAuthority newAuthority = UserAuthority.ADMIN;

        // Act
        ResponseEntity<Void> response = target.updateAuthority(admin, userA.getUsername(), newAuthority);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(target.authenticate(userA).getBody().getRoles())
                .isEqualTo(UserAuthority.ADMIN.getRoles());
    }

    @Test
    public void testDeleteUserRequesterIsAdmin() {
        // Arrange
        target.register(userA);

        // Act
        ResponseEntity<Void> response = target.delete(admin, userA.getUsername());

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(target.authenticate(userA).getStatusCodeValue()).isEqualTo(401);
    }

    @Test
    public void testDeleteUserRequesterIsNotAdmin() {
        // Arrange
        target.register(userA);

        // Act
        ResponseEntity<Void> response = target.delete(userA, userA.getUsername());

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(403);

        // user has not been deleted
        assertThat(target.authenticate(userA)).isNotNull();
    }
}
