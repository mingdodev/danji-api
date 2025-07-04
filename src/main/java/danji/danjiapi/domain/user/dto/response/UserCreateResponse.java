package danji.danjiapi.domain.user.dto.response;

import lombok.Builder;

@Builder
public record UserCreateResponse(
        Long id,
        String email,
        String name,
        String role
) {
    /**
     * Creates a new {@code UserCreateResponse} instance with the specified user details.
     *
     * @param id    the unique identifier of the user
     * @param email the user's email address
     * @param name  the user's name
     * @param role  the user's role
     * @return a {@code UserCreateResponse} containing the provided user information
     */
    public static UserCreateResponse from(Long id, String email, String name, String role) {
        return UserCreateResponse.builder()
                .id(id)
                .email(email)
                .name(name)
                .role(role)
                .build();
    }
}
