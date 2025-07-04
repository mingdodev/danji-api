package danji.danjiapi.global.exception;


import danji.danjiapi.global.auth.CustomAuthException;
import danji.danjiapi.global.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorMessage error = e.getErrorMessage();
        return ResponseEntity
                .status(error.getHttpStatus())
                .body(ErrorResponse.of(error));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
        Throwable cause = e.getCause();
        if (cause instanceof CustomAuthException customAuthException) {
            return ResponseEntity
                    .status(customAuthException.getErrorMessage().getHttpStatus())
                    .body(ErrorResponse.of(customAuthException.getErrorMessage()));
        }
        return ResponseEntity
                .status(ErrorMessage.UNAUTHORIZED.getHttpStatus())
                .body(ErrorResponse.of(ErrorMessage.UNAUTHORIZED));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnhandled(Exception e) {
        log.error("예상치 못한 예외가 발생하였습니다. ", e);
        ErrorMessage error = ErrorMessage.INTERNAL_ERROR;
        return ResponseEntity
                .status(error.getHttpStatus())
                .body(ErrorResponse.of(error));
    }
}