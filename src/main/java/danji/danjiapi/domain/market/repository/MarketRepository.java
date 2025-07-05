package danji.danjiapi.domain.market.repository;

import danji.danjiapi.domain.market.entity.Market;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketRepository extends JpaRepository<Market, Long> {
}
