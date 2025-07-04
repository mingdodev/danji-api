package danji.danjiapi.domain.user.entity;

import danji.danjiapi.global.exception.CustomException;
import danji.danjiapi.global.exception.ErrorMessage;

public enum Role {
    CUSTOMER,
    MERCHANT;



    /**
     * Converts a string to its corresponding {@code Role} enum constant.
     *
     * @param value the string representation of the role
     * @return the matching {@code Role} enum constant
     * @throws CustomException if the input does not match any {@code Role}
     */
    public static Role from(String value) {
        try {
            return Role.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorMessage.AUTH_INVALID_ROLE);
        }
    }
}
