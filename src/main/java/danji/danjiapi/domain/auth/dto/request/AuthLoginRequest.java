package danji.danjiapi.domain.auth.dto.request;

public record AuthLoginRequest(
        String email,
        String password
) {
}
