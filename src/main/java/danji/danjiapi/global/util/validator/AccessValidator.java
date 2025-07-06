package danji.danjiapi.global.util.validator;

import danji.danjiapi.domain.market.entity.Market;
import danji.danjiapi.domain.product.entity.Product;
import danji.danjiapi.global.exception.CustomException;
import danji.danjiapi.global.exception.ErrorMessage;

public class AccessValidator {

    // TO DO: 주문 내역에 대한 접근 권한 확인도 이곳에서

    public static void validateMarketAccess(Market market, Long userId) {
        if (!market.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorMessage.MARKET_FORBIDDEN);
        }
    }

    public static void validateProductAccess(Product product, Long userId) {
        if (!product.getMarket().getUser().getId().equals(userId)) {
            throw new CustomException(ErrorMessage.PRODUCT_FORBIDDEN);
        }
    }
}
