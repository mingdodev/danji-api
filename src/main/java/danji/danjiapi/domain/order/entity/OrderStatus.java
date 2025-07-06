package danji.danjiapi.domain.order.entity;

import danji.danjiapi.global.exception.CustomException;
import danji.danjiapi.global.exception.ErrorMessage;

public enum OrderStatus {
    PENDING,
    REJECTED,
    ACCEPTED;

    public static OrderStatus from(String value) {
        try {
            return OrderStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorMessage.ORDER_INVALID_STATUS);
        }
    }
}
