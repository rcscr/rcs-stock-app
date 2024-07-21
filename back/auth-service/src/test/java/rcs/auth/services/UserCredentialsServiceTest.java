package rcs.auth.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import rcs.auth.api.models.LoginCredentials;
import rcs.auth.api.models.UserAuthority;
import rcs.auth.exceptions.UsernameAlreadyExistsException;
import rcs.auth.exceptions.UsernameNotFoundException;
import rcs.auth.repositories.UserCredentialsRepository;
import rcs.auth.models.UserCredentials;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserCredentialsServiceTest {

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private UserCredentialsRepository repository;

    @InjectMocks
    private UserCredentialsService target;

    @Test
    public void testLoadUserByUsername() {
        // Arrange
        UserCredentials userCredentials = new UserCredentials(
                "username",
                "password",
                UserAuthority.USER);

        when(repository.findById(userCredentials.getUsername()))
                .thenReturn(Optional.of(userCredentials));

        // Act
        User actual = target.loadUserByUsername(userCredentials.getUsername());

        // Assert
        assertThat(actual.getUsername()).isEqualTo(userCredentials.getUsername());
        assertThat(actual.getPassword()).isEqualTo(userCredentials.getPassword());
        assertThat(actual.getAuthorities()).containsOnly(
                new SimpleGrantedAuthority(userCredentials.getAuthority().name()));
    }

    @Test
    public void testLoadUserByUsernameNotFound() {
        // Arrange
        String username = "username";
        when(repository.findById(username))
                .thenReturn(Optional.empty());

        // Act & assert
        assertThrows(
                UsernameNotFoundException.class,
                () -> target.loadUserByUsername(username));
    }

    @Test
    public void testSave() {
        // Arrange
        LoginCredentials request = new LoginCredentials("username", "password");
        when(encoder.encode(request.getPassword()))
                .thenReturn("p455w0rd");

        // Act
        target.save(request);

        // Assert
        verify(repository).save(new UserCredentials(request.getUsername(), "p455w0rd", UserAuthority.USER));
    }

    @Test
    public void testSaveAlreadyExists() {
        // Arrange
        LoginCredentials request = new LoginCredentials("username", "password");
        when(repository.existsById(request.getUsername()))
                .thenReturn(true);

        // Act & assert
        assertThrows(
                UsernameAlreadyExistsException.class,
                () -> target.save(request));
    }

    @Test
    public void testUpdatePassword() {
        // Arrange
        String username = "username";
        String newPassword = "newPassword";
        String encodedPassword = "p455w0rd";

        when(encoder.encode(newPassword))
                .thenReturn(encodedPassword);

        when(repository.updatePassword(username, encodedPassword))
                .thenReturn(true);

        // Act
        target.updatePassword(username, newPassword);

        // Assert
        verify(repository).updatePassword(username, encodedPassword);
    }

    @Test
    public void testDelete() {
        // Arrange
        String username = "username";

        // Act
        target.delete(username);

        // Assert
        verify(repository).deleteById(username);
    }
}
