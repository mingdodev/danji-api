package danji.danjiapi.domain.user.dto.response;

import lombok.Builder;

@Builder
public record UserCreateResponse(
        Long id,
        String email,
        String name,
        String role
) {
    public static UserCreateResponse from(Long id, String email, String name, String role) {
        return UserCreateResponse.builder()
                .id(id)
                .email(email)
                .name(name)
                .role(role)
                .build();
    }
}
