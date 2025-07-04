package danji.danjiapi.domain.auth.service;

import danji.danjiapi.domain.auth.dto.request.AuthLoginRequest;
import danji.danjiapi.domain.auth.dto.response.AuthLoginResponse;
import danji.danjiapi.domain.auth.dto.JwtToken;
import danji.danjiapi.global.auth.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Authenticates a user with the provided credentials and returns a response containing user information and JWT tokens.
     *
     * @param request the login request containing the user's email and password
     * @return an {@link AuthLoginResponse} with the authenticated username, primary authority, access token, and refresh token
     */
    public AuthLoginResponse login(AuthLoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.email(), request.password());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        return AuthLoginResponse.from(
                authentication.getName(),
                authentication.getAuthorities().stream().findFirst().map(GrantedAuthority::getAuthority).orElse(""),
                jwtToken.accessToken(),
                jwtToken.refreshToken()
        );
    }
}
