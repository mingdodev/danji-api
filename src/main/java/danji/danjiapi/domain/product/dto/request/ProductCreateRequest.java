package danji.danjiapi.domain.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record ProductCreateRequest(
        @NotBlank(message = "상품 이름은 필수입니다.")
        String name,
        @NotBlank(message = "상품 가격은 필수입니다.")
        BigDecimal price,
        Integer minQuantity,
        Integer maxQuantity
) {
}
