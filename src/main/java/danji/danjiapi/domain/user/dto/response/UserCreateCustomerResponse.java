package danji.danjiapi.domain.user.dto.response;

import danji.danjiapi.domain.user.entity.User;
import lombok.Builder;

@Builder
public record UserCreateCustomerResponse(
        Long id,
        String name,
        String role
) {
    public static UserCreateCustomerResponse from(User user) {
        return UserCreateCustomerResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }
}
