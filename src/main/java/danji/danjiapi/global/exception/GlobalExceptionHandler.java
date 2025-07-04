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

    /**
     * Handles CustomException by returning an error response with the appropriate HTTP status and error details.
     *
     * @param e the CustomException to handle
     * @return a ResponseEntity containing the error response and corresponding HTTP status
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorMessage error = e.getErrorMessage();
        return ResponseEntity
                .status(error.getHttpStatus())
                .body(ErrorResponse.of(error));
    }

    /**
     * Handles Spring Security authentication exceptions and returns a standardized error response.
     *
     * If the cause of the exception is a {@code CustomAuthException}, the response uses its specific error message and HTTP status.
     * Otherwise, a generic unauthorized error response is returned.
     *
     * @param e the authentication exception to handle
     * @return a {@code ResponseEntity} containing the error response and appropriate HTTP status
     */
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

    /**
     * Handles all uncaught exceptions and returns a standardized internal server error response.
     *
     * @param e the unhandled exception
     * @return a ResponseEntity containing an error response with HTTP 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnhandled(Exception e) {
        log.error("예상치 못한 예외가 발생하였습니다. ", e);
        ErrorMessage error = ErrorMessage.INTERNAL_ERROR;
        return ResponseEntity
                .status(error.getHttpStatus())
                .body(ErrorResponse.of(error));
    }
}