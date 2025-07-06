package danji.danjiapi.domain.market.dto.response;

import danji.danjiapi.domain.product.entity.Product;
import java.math.BigDecimal;

public record ProductDetail(
        Long id,
        String name,
        BigDecimal price,
        Integer minQuantity,
        Integer maxQuantity
) {
    public static ProductDetail from(Product product) {
        return new ProductDetail(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getMinQuantity(),
                product.getMaxQuantity()
        );
    }
}
