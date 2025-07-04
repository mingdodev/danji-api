package danji.danjiapi.domain.user.service;

import danji.danjiapi.domain.user.dto.request.UserCreateRequest;
import danji.danjiapi.domain.user.dto.response.UserCreateResponse;
import danji.danjiapi.domain.user.entity.User;
import danji.danjiapi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user with the provided information and returns the created user's details.
     *
     * The password is securely encoded before storing. Returns a response containing the user's ID, email, name, and role.
     *
     * @param request the user creation request containing email, plaintext password, name, and role
     * @return a response with the newly created user's ID, email, name, and role
     */
    public UserCreateResponse signup(UserCreateRequest request) {
        // TO DO: 이메일 중복 체크
        String encodedPassword = passwordEncoder.encode(request.password());

        User user = userRepository.save(User.create(request.email(), encodedPassword, request.name(), request.role()));

        return UserCreateResponse.from(user.getId(), user.getEmail(),user.getName(), user.getRole().name());
    }
}
