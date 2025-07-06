package danji.danjiapi.domain.order.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record OrderUpdateRequest(
        @NotNull(message = "주문 상품 목록이 누락되었습니다.")
        @NotEmpty(message = "주문 상품 목록은 비어있을 수 없습니다.")
        List<OrderItemDetail> orderItems
) {
}
