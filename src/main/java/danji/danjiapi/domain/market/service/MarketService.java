package danji.danjiapi.domain.market.service;

import danji.danjiapi.domain.market.dto.request.MarketSearchCondition;
import danji.danjiapi.domain.market.dto.response.MarketDetail;
import danji.danjiapi.domain.market.entity.Market;
import danji.danjiapi.domain.market.repository.MarketRepository;
import danji.danjiapi.domain.product.entity.Product;
import danji.danjiapi.domain.product.repository.ProductRepository;
import danji.danjiapi.global.exception.CustomException;
import danji.danjiapi.global.exception.ErrorMessage;
import danji.danjiapi.global.response.PaginationResponse;
import danji.danjiapi.global.util.resolver.CurrentUserResolver;
import danji.danjiapi.global.util.validator.AccessValidator;
import java.time.Duration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MarketService {
    private final MarketRepository marketRepository;
    private final ProductRepository productRepository;
    private final CurrentUserResolver currentUserResolver;
    private final RedisTemplate<String, List<MarketDetail>> redisTemplate;

    public MarketService(
            MarketRepository marketRepository,
            ProductRepository productRepository,
            CurrentUserResolver currentUserResolver,
            @Qualifier("cacheRedisTemplate") RedisTemplate<String, List<MarketDetail>> redisTemplate
    ) {
        this.marketRepository = marketRepository;
        this.productRepository = productRepository;
        this.currentUserResolver = currentUserResolver;
        this.redisTemplate = redisTemplate;
    }

    public Slice<Market> searchMarkets(MarketSearchCondition searchCondition, Pageable pageable) {

        Slice<Market> markets;

        if (searchCondition == null || searchCondition.keyword() == null || searchCondition.keyword().trim().isEmpty()) {
            markets = marketRepository.findAll(pageable);
        } else {
            markets = marketRepository.findByNameOrAddressOrProductsContaining(searchCondition.keyword().trim(), pageable);
        }

        return markets;
    }

    public List<Product> getProducts(Long marketId) {
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new CustomException(ErrorMessage.MARKET_NOT_FOUND));

        String role = currentUserResolver.getCurrentUserRole();
        Long userId = currentUserResolver.getCurrentUserId();

        if (role.equals("MERCHANT")) {
            AccessValidator.validateMarketAccess(market, userId);
        }

        return productRepository.findByMarketId(marketId);
    }
}
