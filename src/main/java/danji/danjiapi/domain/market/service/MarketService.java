package danji.danjiapi.domain.market.service;

import danji.danjiapi.domain.market.dto.request.MarketSearchCondition;
import danji.danjiapi.domain.market.dto.response.MarketDetail;
import danji.danjiapi.domain.market.dto.response.ProductDetail;
import danji.danjiapi.domain.market.entity.Market;
import danji.danjiapi.domain.market.repository.MarketRepository;
import danji.danjiapi.domain.product.entity.Product;
import danji.danjiapi.domain.product.repository.ProductRepository;
import danji.danjiapi.global.exception.CustomException;
import danji.danjiapi.global.exception.ErrorMessage;
import danji.danjiapi.global.response.PaginationResponse;
import danji.danjiapi.global.util.resolver.CurrentUserResolver;
import danji.danjiapi.global.util.validator.AccessValidator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketService {
    private final MarketRepository marketRepository;
    private final ProductRepository productRepository;
    private final CurrentUserResolver currentUserResolver;

    public PaginationResponse<MarketDetail> searchMarkets(MarketSearchCondition searchCondition, Pageable pageable) {
        Slice<Market> markets;

        if (searchCondition == null || searchCondition.keyword() == null || searchCondition.keyword().trim().isEmpty()) {
            markets = marketRepository.findAll(pageable);
        } else {
            markets = marketRepository.findByNameOrAddressOrProductsContaining(searchCondition.keyword().trim(), pageable);
        }

        return PaginationResponse.from(markets.map(MarketDetail::from));
    }

    public List<ProductDetail> getProducts(Long marketId) {
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new CustomException(ErrorMessage.MARKET_NOT_FOUND));

        String role = currentUserResolver.getCurrentUserRole();
        Long userId = currentUserResolver.getCurrentUserId();

        if (role.equals("MERCHANT")) {
            AccessValidator.validateMarketAccess(market, userId);
        }

        List<Product> products = productRepository.findByMarketId(marketId);

        return products.stream()
                .map(ProductDetail::from)
                .toList();
    }
}
