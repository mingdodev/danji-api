package danji.danjiapi.global.util.validator;

import danji.danjiapi.domain.market.entity.Market;
import danji.danjiapi.domain.order.entity.Order;
import danji.danjiapi.domain.product.entity.Product;
import danji.danjiapi.global.exception.CustomException;
import danji.danjiapi.global.exception.ErrorMessage;

public class AccessValidator {

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

    public static void validateOrderAccess(Order order, Long userId) {
        // 주문 내역 수정의 경우 사장님만 가능하기 때문에, MERCHANT 역할로 확인함
        if (!order.getMerchant().getId().equals(userId)) {
            throw new CustomException(ErrorMessage.ORDER_FORBIDDEN);
        }
    }
}
