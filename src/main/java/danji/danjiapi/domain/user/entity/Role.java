package danji.danjiapi.domain.user.entity;

import danji.danjiapi.global.exception.CustomException;
import danji.danjiapi.global.exception.ErrorMessage;

public enum Role {
    CUSTOMER,
    MERCHANT;



    public static Role from(String value) {
        try {
            return Role.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorMessage.AUTH_INVALID_ROLE);
        }
    }
}
