package danji.danjiapi.global.security.exception;

import danji.danjiapi.global.exception.ErrorMessage;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class CustomAuthException extends AuthenticationException {
    private final ErrorMessage errorMessage;

    public CustomAuthException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }

}
