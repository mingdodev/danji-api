package danji.danjiapi.global.exception;


import danji.danjiapi.global.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnhandled(Exception e) {
        log.error("예상치 못한 예외가 발생하였습니다. ", e);
        ErrorMessage error = ErrorMessage.INTERNAL_ERROR;
        return ResponseEntity
                .status(error.getHttpStatus())
                .body(ErrorResponse.of(error));
    }
}