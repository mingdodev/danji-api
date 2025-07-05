package danji.danjiapi.domain.market.controller;

import danji.danjiapi.domain.market.dto.request.MarketSearchCondition;
import danji.danjiapi.domain.market.dto.response.MarketSummary;
import danji.danjiapi.domain.market.service.MarketService;
import danji.danjiapi.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/markets")
@RequiredArgsConstructor
public class MarketController {
    private final MarketService marketService;

    @GetMapping("")
    @Operation(summary = "가게 둘러보기", description = "회원이 모든 가게들의 목록을 조회하고, 키워드로 원하는 가게를 검색합니다.")
    public ApiResponse<List<MarketSummary>> getMarkets(@ModelAttribute MarketSearchCondition searchCondition) {
        return ApiResponse.success(marketService.searchMarkets(searchCondition));
    }

}
