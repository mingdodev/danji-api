package danji.danjiapi.domain.product.repository;

import danji.danjiapi.domain.product.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByMarketId(Long marketId);
}
