package danji.danjiapi.domain.order.dto.response;

import danji.danjiapi.domain.order.entity.Order;

public record OrderStatusUpdateResponse(
        Long orderId,
        String updatedStatus
) {
    public static OrderStatusUpdateResponse from(Order order) {
        return new OrderStatusUpdateResponse(order.getId(), order.getStatus().name());
    }
}
