package danji.danjiapi.domain.auth.dto.response;

import lombok.Builder;

@Builder
public record AuthLoginResponse(
        String email,
        String role,
        String accessToken,
        String refreshToken
) {
    public static AuthLoginResponse from(String email, String role, String accessToken, String refreshToken) {
        return AuthLoginResponse.builder()
                .email(email)
                .role(role)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
