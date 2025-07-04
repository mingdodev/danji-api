package danji.danjiapi.domain.auth.service;

import danji.danjiapi.domain.user.entity.User;
import danji.danjiapi.domain.user.repository.UserRepository;
import danji.danjiapi.global.security.exception.CustomAuthException;
import danji.danjiapi.global.security.CustomUserDetails;
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

    @Override
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findByEmail(email)
                .map(this::createUserDetails)
                .orElseThrow(() -> new CustomAuthException(ErrorMessage.AUTH_USER_NOT_FOUND));
    };

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
