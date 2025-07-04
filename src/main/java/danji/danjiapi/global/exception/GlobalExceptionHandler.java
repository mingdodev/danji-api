package danji.danjiapi.global.exception;


import danji.danjiapi.global.security.exception.CustomAuthException;
import danji.danjiapi.global.response.ErrorResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Custom Error
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorMessage error = e.getErrorMessage();
        return ResponseEntity
                .status(error.getHttpStatus())
                .body(ErrorResponse.of(error));
    }

    /**
     * Authentication Error
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
     * Argument Validation Error
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
        List<ValidationError> fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationError(error.getObjectName(), error.getField(), error.getDefaultMessage()))
                .toList();
        return ResponseEntity
                .status(ErrorMessage.INVALID_INPUT.getHttpStatus())
                .body(ErrorResponse.of(ErrorMessage.INVALID_INPUT, fieldErrors));
    }

    /**
     * Unhandled Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnhandled(Exception e) {
        log.error("예상치 못한 예외가 발생하였습니다. ", e);
        ErrorMessage error = ErrorMessage.INTERNAL_ERROR;
        return ResponseEntity
                .status(error.getHttpStatus())
                .body(ErrorResponse.of(error));
    }

    private record ValidationError(String object, String field, String message) {}
}