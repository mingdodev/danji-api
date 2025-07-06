package danji.danjiapi.domain.order.dto.request;

import danji.danjiapi.domain.market.dto.request.OrderItemInfo;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record OrderCreateRequest(
        @NotNull(message = "가게 id가 누락되었습니다.")
        Long marketId,
        @NotNull(message = "배송 일자가 누락되었습니다.")
        LocalDateTime date,
        String deliveryAddress,
        List<OrderItemInfo> orderItems
) {
}