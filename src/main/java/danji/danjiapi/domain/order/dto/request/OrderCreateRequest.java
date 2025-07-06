package danji.danjiapi.domain.order.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record OrderCreateRequest(
        @NotNull(message = "가게 id가 누락되었습니다.")
        Long marketId,
        @NotNull(message = "배송 일자가 누락되었습니다.")
        LocalDateTime date,
        String deliveryAddress,
        @NotNull(message = "주문 항목이 누락되었습니다.")
        @NotEmpty(message = "주문 상품 목록은 비어있을 수 없습니다.")
        List<OrderItemInfo> orderItems
) {
}