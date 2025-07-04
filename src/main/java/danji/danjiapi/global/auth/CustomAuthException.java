package danji.danjiapi.global.auth;

import danji.danjiapi.global.exception.ErrorMessage;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class CustomAuthException extends AuthenticationException {
    private final ErrorMessage errorMessage;

    /**
     * Constructs a new CustomAuthException with the specified error message.
     *
     * @param errorMessage the ErrorMessage object containing details about the authentication error
     */
    public CustomAuthException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }

}
