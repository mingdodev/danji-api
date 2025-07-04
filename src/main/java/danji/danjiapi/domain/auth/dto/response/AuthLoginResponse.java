package danji.danjiapi.domain.auth.dto.response;

import lombok.Builder;

@Builder
public record AuthLoginResponse(
        String email,
        String role,
        String accessToken,
        String refreshToken
) {
    /**
     * Creates a new {@code AuthLoginResponse} instance with the specified email, role, access token, and refresh token.
     *
     * @param email the user's email address
     * @param role the user's role
     * @param accessToken the access token for authentication
     * @param refreshToken the refresh token for session renewal
     * @return a new {@code AuthLoginResponse} containing the provided authentication details
     */
    public static AuthLoginResponse from(String email, String role, String accessToken, String refreshToken) {
        return AuthLoginResponse.builder()
                .email(email)
                .role(role)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
