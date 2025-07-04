package danji.danjiapi.domain.auth.dto;

import lombok.Builder;

@Builder
public record JwtToken(
        String accessToken,
        String refreshToken
) {
}
