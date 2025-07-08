package danji.danjiapi.domain.auth.service;

import danji.danjiapi.domain.auth.dto.request.AuthLoginRequest;
import danji.danjiapi.domain.auth.dto.response.AuthLoginResponse;
import danji.danjiapi.domain.auth.dto.JwtToken;
import danji.danjiapi.global.exception.CustomException;
import danji.danjiapi.global.exception.ErrorMessage;
import danji.danjiapi.global.security.CustomUserDetails;
import danji.danjiapi.global.security.JwtTokenProvider;
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

    public AuthLoginResponse login(AuthLoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.email(), request.password());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        if (!(authentication.getPrincipal() instanceof CustomUserDetails principal)) {
            throw new CustomException(ErrorMessage.AUTH_INVALID);
        }

        return AuthLoginResponse.from(
                principal.getUserId(),
                authentication.getAuthorities().stream().findFirst().map(GrantedAuthority::getAuthority).orElse(""),
                jwtToken.accessToken(),
                jwtToken.refreshToken()
        );
    }
}
