package danji.danjiapi.domain.product.dto.response;

import danji.danjiapi.domain.product.entity.Product;
import java.math.BigDecimal;

public record ProductCreateResponse(
        Long id,
        String name,
        BigDecimal price,
        Integer minQuantity,
        Integer maxQuantity
) {
    public static ProductCreateResponse from(Product product) {
        return new ProductCreateResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getMinQuantity(),
                product.getMaxQuantity()
        );
    }
}
