package danji.danjiapi.domain.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record OrderItemDetail(
        @NotNull(message = "상품 id가 누락되었습니다.")
        Long id,
        @NotBlank(message = "상품명이 누락되었습니다.")
        String name,
        @NotNull(message = "상품 가격이 누락되었습니다.")
        BigDecimal price,
        @NotNull(message = "상품 수량이 누락되었습니다.")
        Integer quantity
) {
}
