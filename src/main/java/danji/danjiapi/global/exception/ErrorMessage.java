package danji.danjiapi.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorMessage {

    /**
     * code: 사용자 정의 에러 코드
     * httpStatus: 예외 상황에 알맞은 http 에러 코드
     * message: 에러 메시지
     */

    // global
    INVALID_INPUT("INVALID_INPUT", "잘못된 입력입니다.", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED("UNAUTHORIZED", "인증되지 않은 요청입니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("FORBIDDEN", "요청 권한이 없습니다.", HttpStatus.FORBIDDEN),
    CONFLICT("CONFLICT", "리소스 충돌이 발생하였습니다.", HttpStatus.CONFLICT),
    INTERNAL_ERROR("INTERNAL_ERROR", "알 수 없는 서버 내부 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    //user
    USER_DUPLICATED_EMAIL("USER_DUPLICATED_EMAIL", "이미 존재하는 이메일입니다.", HttpStatus.CONFLICT),

    // auth
    AUTH_INVALID("AUTH_INVALID", "잘못된 토큰입니다.", HttpStatus.BAD_REQUEST),
    AUTH_FORBIDDEN("AUTH_FORBIDDEN", "권한 정보가 없는 토큰입니다.", HttpStatus.FORBIDDEN),
    AUTH_USER_NOT_FOUND("AUTH_USER_NOT_FOUND", "해당 인증 정보의 회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    AUTH_INVALID_ROLE("AUTH_INVALID_ROLE", "일치하는 역할 정보가 없습니다.", HttpStatus.BAD_REQUEST),

    // market
    MARKET_NOT_FOUND("MARKET_NOT_FOUND", "존재하지 않는 가게 ID 입니다.", HttpStatus.NOT_FOUND),

    // product
    PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", "존재하지 않는 상품 ID 입니다.", HttpStatus.NOT_FOUND),
    PRODUCT_FORBIDDEN("PRODUCT_FORBIDDEN", "해당 상품에 대한 접근 권한이 없습니다.", HttpStatus.FORBIDDEN)
;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
