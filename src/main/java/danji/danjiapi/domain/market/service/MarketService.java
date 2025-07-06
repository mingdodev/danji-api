package danji.danjiapi.domain.market.service;

import danji.danjiapi.domain.market.dto.request.MarketSearchCondition;
import danji.danjiapi.domain.market.dto.response.MarketSummary;
import danji.danjiapi.domain.market.dto.response.ProductDetail;
import danji.danjiapi.domain.market.entity.Market;
import danji.danjiapi.domain.market.repository.MarketRepository;
import danji.danjiapi.domain.product.entity.Product;
import danji.danjiapi.domain.product.repository.ProductRepository;
import danji.danjiapi.global.exception.CustomException;
import danji.danjiapi.global.exception.ErrorMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketService {
    private final MarketRepository marketRepository;
    private final ProductRepository productRepository;

    public List<MarketSummary> searchMarkets(MarketSearchCondition searchCondition) {
        List<Market> markets;

        if (searchCondition == null || searchCondition.keyword() == null || searchCondition.keyword().trim().isEmpty()) {
            markets = marketRepository.findAll();
        } else {
            markets = marketRepository.findByNameOrAddressOrProductsContaining(searchCondition.keyword().trim());
        }

        return markets.stream()
                .map(MarketSummary::from)
                .toList();
    }

    public List<ProductDetail> getProducts(Long marketId) {
        if (!marketRepository.existsById(marketId)) {
            throw new CustomException(ErrorMessage.MARKET_NOT_FOUND);
        }
        List<Product> products = productRepository.findByMarketId(marketId);

        return products.stream()
                .map(ProductDetail::from)
                .toList();
    }
}
