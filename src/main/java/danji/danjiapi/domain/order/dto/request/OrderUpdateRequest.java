package danji.danjiapi.domain.order.dto.request;

import java.util.List;

public record OrderUpdateRequest(
        List<OrderItemDetail> orderItems
) {
}
