package danji.danjiapi.domain.order.dto.request;

import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateRequest(
        @NotNull(message = "주문 상태가 누락되었습니다.")
        String status
) {}