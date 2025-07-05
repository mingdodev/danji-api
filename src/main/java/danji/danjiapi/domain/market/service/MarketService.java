package danji.danjiapi.domain.market.service;

import danji.danjiapi.domain.market.dto.request.MarketSearchCondition;
import danji.danjiapi.domain.market.dto.response.MarketSummary;
import danji.danjiapi.domain.market.entity.Market;
import danji.danjiapi.domain.market.repository.MarketRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketService {
    private final MarketRepository marketRepository;

    public List<MarketSummary> searchMarkets(MarketSearchCondition searchCondition) {
        List<Market> markets;

        if (searchCondition == null) {
            markets = marketRepository.findAll();
        } else {
            markets = marketRepository.findByNameOrAddressOrProductsContaining(searchCondition.keyword());
        }

        return markets.stream()
                .map(MarketSummary::from)
                .toList();
    }
}
