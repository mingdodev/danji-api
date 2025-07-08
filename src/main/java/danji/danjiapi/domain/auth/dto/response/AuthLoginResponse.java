package danji.danjiapi.domain.auth.dto.response;

import lombok.Builder;

@Builder
public record AuthLoginResponse(
        Long userId,
        String role,
        String accessToken,
        String refreshToken
) {
    public static AuthLoginResponse from(Long id, String role, String accessToken, String refreshToken) {
        return AuthLoginResponse.builder()
                .userId(id)
                .role(role)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
