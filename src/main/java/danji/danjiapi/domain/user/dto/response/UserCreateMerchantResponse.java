package danji.danjiapi.domain.user.dto.response;

import danji.danjiapi.domain.user.entity.User;
import lombok.Builder;

@Builder
public record UserCreateMerchantResponse(
        Long id,
        String name,
        String role,
        Long marketId
) {
    public static UserCreateMerchantResponse from(User user) {

        return UserCreateMerchantResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .role(user.getRole().name())
                .marketId(user.getMarket().getId())
                .build();
    }
}
