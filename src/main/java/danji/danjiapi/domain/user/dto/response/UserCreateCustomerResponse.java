package danji.danjiapi.domain.user.dto.response;

import lombok.Builder;

@Builder
public record UserCreateCustomerResponse(
        Long id,
        String name,
        String role
) {
    public static UserCreateCustomerResponse from(Long id, String name, String role) {
        return UserCreateCustomerResponse.builder()
                .id(id)
                .name(name)
                .role(role)
                .build();
    }
}
