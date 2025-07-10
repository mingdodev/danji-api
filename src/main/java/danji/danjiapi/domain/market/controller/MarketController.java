package danji.danjiapi.domain.market.controller;

import danji.danjiapi.domain.market.dto.request.MarketSearchCondition;
import danji.danjiapi.domain.market.dto.response.MarketDetail;
import danji.danjiapi.domain.market.service.MarketService;
import danji.danjiapi.domain.market.dto.response.ProductDetail;
import danji.danjiapi.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/markets")
@RequiredArgsConstructor
public class MarketController {
    private final MarketService marketService;

    @GetMapping("")
    @Operation(summary = "가게 목록 조회 및 검색", description = "고객이 모든 가게들의 목록을 조회하고, 키워드로 원하는 가게를 검색합니다.")
    public ApiResponse<List<MarketDetail>> getMarkets(@ModelAttribute MarketSearchCondition searchCondition) {
        log.info("GET /api/markets");
        return ApiResponse.success(marketService.searchMarkets(searchCondition));
    }

    @GetMapping("/{marketId}/products")
    @Operation(summary = "특정 가게의 상품 목록 조회", description = "사장님은 자기 가게의 모든 상품을, 고객은 선택한 특정 가게의 모든 상품을 조회할 수 있습니다.")
    public ApiResponse<List<ProductDetail>> getProducts(@PathVariable Long marketId) {
        log.info("GET /api/markets/{marketId}/products");
        return ApiResponse.success(marketService.getProducts(marketId));
    }

}
