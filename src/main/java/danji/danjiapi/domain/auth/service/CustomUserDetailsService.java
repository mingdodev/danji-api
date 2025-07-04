package danji.danjiapi.domain.auth.service;

import danji.danjiapi.domain.user.entity.User;
import danji.danjiapi.domain.user.repository.UserRepository;
import danji.danjiapi.global.auth.CustomAuthException;
import danji.danjiapi.global.auth.CustomUserDetails;
import danji.danjiapi.global.exception.ErrorMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * Loads user details for authentication based on the provided email address.
     *
     * @param email the email address of the user to be loaded
     * @return a UserDetails object representing the authenticated user
     * @throws CustomAuthException if no user with the given email is found
     */
    @Override
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findByEmail(email)
                .map(this::createUserDetails)
                .orElseThrow(() -> new CustomAuthException(ErrorMessage.AUTH_USER_NOT_FOUND));
    };

    /**
     * Converts a User entity into a CustomUserDetails object containing authentication and authorization information.
     *
     * @param user the User entity to convert
     * @return a CustomUserDetails instance with the user's ID, email, password, and granted authorities
     */
    private UserDetails createUserDetails(User user) {
        List<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority(user.getRole().name()));

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
