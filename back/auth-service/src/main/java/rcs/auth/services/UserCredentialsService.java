package rcs.auth.services;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rcs.auth.api.models.LoginCredentials;
import rcs.auth.api.models.UserAuthority;
import rcs.auth.exceptions.FailedToUpdateUserException;
import rcs.auth.exceptions.UsernameAlreadyExistsException;
import rcs.auth.exceptions.UsernameNotFoundException;
import rcs.auth.models.UserCredentials;
import rcs.auth.repositories.UserCredentialsRepository;

import java.util.stream.Collectors;

@Service
@Transactional
public class UserCredentialsService implements UserDetailsService {

    private PasswordEncoder encoder;
    private UserCredentialsRepository repository;

    public UserCredentialsService(PasswordEncoder encoder, UserCredentialsRepository repository) {
        this.encoder = encoder;
        this.repository = repository;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findById(username)
                .map(credentials -> new User(
                        credentials.getUsername(),
                        credentials.getPassword(),
                        credentials.getAuthority().getRoles().stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList())))
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public void save(LoginCredentials request) {
        String username = request.getUsername();
        if (repository.existsById(username)) {
            throw new UsernameAlreadyExistsException(username);
        }

        UserCredentials credentials = new UserCredentials(
                username,
                encoder.encode(request.getPassword()),
                UserAuthority.USER);

        repository.save(credentials);
    }

    public void updatePassword(String username, String newPassword) {
        if (!repository.updatePassword(username, encoder.encode(newPassword))) {
            throw new FailedToUpdateUserException(username, UserCredentials.Fields.password);
        }
    }

    public void updateAuthority(String username, UserAuthority newAuthority) {
        if (!repository.updateAuthority(username, newAuthority)) {
            throw new FailedToUpdateUserException(username, UserCredentials.Fields.authority);
        }
    }

    public void delete(String username) {
        try {
            repository.deleteById(username);
        } catch (EmptyResultDataAccessException e){
            throw new UsernameNotFoundException(username);
        }
    }
}
