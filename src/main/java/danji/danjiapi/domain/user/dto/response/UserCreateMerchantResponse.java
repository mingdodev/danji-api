package danji.danjiapi.domain.user.dto.response;

import lombok.Builder;

@Builder
public record UserCreateMerchantResponse(
        Long id,
        String name,
        String role,
        Long marketId
) {
    public static UserCreateMerchantResponse from(Long id, String name, String role, Long marketId) {
        return UserCreateMerchantResponse.builder()
                .id(id)
                .name(name)
                .role(role)
                .marketId(marketId)
                .build();
    }
}
