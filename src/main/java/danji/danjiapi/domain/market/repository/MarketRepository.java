package danji.danjiapi.domain.market.repository;

import danji.danjiapi.domain.market.entity.Market;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MarketRepository extends JpaRepository<Market, Long> {

    Optional<Market> findByUserId(Long id);

    @Query("""
        SELECT DISTINCT m FROM Market m
        LEFT JOIN m.products p
        WHERE
            (m.name LIKE %:keyword%
            OR m.address LIKE %:keyword%
            OR p.name LIKE %:keyword%)
    """)
    List<Market> findByNameOrAddressOrProductsContaining(@Param("keyword") String keyword);
}
