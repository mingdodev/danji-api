package danji.danjiapi.domain.order.dto.response;

import danji.danjiapi.domain.order.entity.Order;

public record OrderCreateResponse(
        Long id
) {
    public static OrderCreateResponse from(Order order) {
        return new OrderCreateResponse(
                order.getId()
        );
    }
}
