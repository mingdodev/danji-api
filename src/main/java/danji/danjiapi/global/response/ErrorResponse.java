package danji.danjiapi.global.response;

import danji.danjiapi.global.exception.ErrorMessage;
import jakarta.annotation.Nullable;

public record ErrorResponse(
        boolean success,
        ErrorInfo error
) {
    public static ErrorResponse of(ErrorMessage errorMessage) {
        return new ErrorResponse(false, new ErrorInfo(
                errorMessage.getCode(),
                errorMessage.getMessage(),
                null
        ));
    }

    public static ErrorResponse of(ErrorMessage errorMessage, @Nullable Object details) {
        return new ErrorResponse(false, new ErrorInfo(
                errorMessage.getCode(),
                errorMessage.getMessage(),
                details
        ));
    }

    public record ErrorInfo(String code, String message, @Nullable Object details) {}

}