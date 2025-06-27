package danji.danjiapi.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorMessage {
    INVALID_INPUT("INVALID_INPUT", "잘못된 입력입니다.", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED("UNAUTHORIZED", "인증되지 않은 요청입니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("FORBIDDEN", "요청 권한이 없습니다.", HttpStatus.FORBIDDEN),
    CONFLICT("CONFLICT", "리소스 충돌이 발생하였습니다.", HttpStatus.CONFLICT),
    INTERNAL_ERROR("INTERNAL_ERROR", "알 수 없는 서버 내부 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorMessage(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
