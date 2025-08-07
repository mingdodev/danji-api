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
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class MarketService {
    private final MarketRepository marketRepository;
    private final ProductRepository productRepository;
    private final CurrentUserResolver currentUserResolver;

    public MarketService(
            MarketRepository marketRepository,
            ProductRepository productRepository,
            CurrentUserResolver currentUserResolver,
            @Qualifier("cacheRedisTemplate") RedisTemplate<String, Object> redisTemplate
    ) {
        this.marketRepository = marketRepository;
        this.productRepository = productRepository;
        this.currentUserResolver = currentUserResolver;
        this.redisTemplate = redisTemplate;
    }

    @Qualifier("cacheRedisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;

    public PaginationResponse<MarketDetail> searchMarkets(MarketSearchCondition searchCondition, Pageable pageable) {
        Slice<Market> markets;

        if (searchCondition == null || searchCondition.keyword() == null || searchCondition.keyword().trim().isEmpty()) {
            markets = marketRepository.findAll(pageable);
        } else {
            markets = marketRepository.findByNameOrAddressOrProductsContaining(searchCondition.keyword().trim(), pageable);
        }

        return PaginationResponse.from(markets.map(MarketDetail::from));
    }

    /*  캐싱을 적용한 가게 목록 조회 메서드
        Valkey(Redis) index 1에 키워드를 key로 검색 결과를 캐시한다.
        - Cache Hit: 캐시 데이터를 슬라이싱해 페이지네이션 응답으로 반환한다.
        - Cache Miss: DB로부터 결과 조회 후 캐시에 저장(TTL 10분)하고, 데이터를 슬라이싱해 페이지네이션 응답으로 반환한다.
                      만약 검색 결과가 없다면 null을 저장(TTL 5분)하고, 빈 리스트를 페이지네이션 응답에 넣어 반환한다.
    */
    public PaginationResponse<MarketDetail> searchMarketsWithCache(MarketSearchCondition searchCondition, Pageable pageable) {
        String keyword = (searchCondition == null || searchCondition.keyword() == null) ? "" : searchCondition.keyword().trim();
        String cacheKey = keyword.isEmpty()
                ? "market:search:all"
                : "market:search:" + keyword;
        int start = (int) pageable.getOffset();

        @SuppressWarnings("unchecked") // 내부적으로 타입 정보 포함하는 Serializer 사용하므로 unchecked warning 제거
        List<MarketDetail> cachedMarkets = (List<MarketDetail>) redisTemplate.opsForValue().get(cacheKey);

        if (cachedMarkets != null) {
            if (cachedMarkets.isEmpty()) return PaginationResponse.from(List.of(), false);

            int end = Math.min(start + pageable.getPageSize(), cachedMarkets.size());
            List<MarketDetail> pagedList = cachedMarkets.subList(start, end);

            return PaginationResponse.from(pagedList, end < cachedMarkets.size());
        }

        List<Market> queriedMarkets = marketRepository.findByNameOrAddressOrProductsContaining(keyword);

        if (queriedMarkets.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, List.of(), Duration.ofMinutes(5));

            return PaginationResponse.from(List.of(), false);
        }

        List<MarketDetail> cacheableMarkets = queriedMarkets.stream()
                .map(MarketDetail::from).toList();
        redisTemplate.opsForValue().set(cacheKey, cacheableMarkets, Duration.ofMinutes(10));

        int end = Math.min(start + pageable.getPageSize(), queriedMarkets.size());
        List<MarketDetail> pagedList = cacheableMarkets.subList(start, end);

        return PaginationResponse.from(pagedList, end < cacheableMarkets.size());
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
