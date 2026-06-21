package danji.danjiapi.domain.market.repository;

import danji.danjiapi.domain.market.entity.Market;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MarketRepository extends JpaRepository<Market, Long> {

    Optional<Market> findByUserId(Long id);

    @Query("SELECT m FROM Market m JOIN FETCH m.user WHERE m.user.id IN :userIds")
    List<Market> findAllByUserIdIn(@Param("userIds") Set<Long> userIds);

    @Query("""
        SELECT m FROM Market m
        WHERE m.name LIKE %:keyword%
           OR m.address LIKE %:keyword%
           OR EXISTS (
               SELECT 1 FROM Product p
               WHERE p.market = m
               AND p.name LIKE %:keyword%
           )
    """)
    Slice<Market> findByNameOrAddressOrProductsContaining(@Param("keyword") String keyword, Pageable pageable);

}
