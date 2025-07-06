package danji.danjiapi.domain.order.dto.response;

import danji.danjiapi.domain.order.entity.OrderItem;
import java.math.BigDecimal;

public record OrderItemDetail(
        Long id,
        String name,
        Integer quantity,
        BigDecimal price
) {
    public static OrderItemDetail from(OrderItem orderItem) {
        return new OrderItemDetail(
                orderItem.getId(),
                orderItem.getName(),
                orderItem.getQuantity(),
                orderItem.getPrice()
        );
    }
}
