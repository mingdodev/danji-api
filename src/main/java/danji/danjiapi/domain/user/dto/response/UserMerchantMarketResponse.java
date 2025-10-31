package danji.danjiapi.domain.user.dto.response;

import danji.danjiapi.domain.market.entity.Market;
import lombok.Builder;

@Builder
public record UserMerchantMarketResponse(
        String marketName,
        Long marketId,
        String marketAddress
) {
    public static UserMerchantMarketResponse from(Market market) {
        return UserMerchantMarketResponse.builder()
                .marketName(market.getName())
                .marketId(market.getId())
                .marketAddress(market.getAddress())
                .build();
    }
}
