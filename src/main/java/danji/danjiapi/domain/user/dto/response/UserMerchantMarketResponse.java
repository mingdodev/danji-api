package danji.danjiapi.domain.user.dto.response;

import lombok.Builder;

@Builder
public record UserMerchantMarketResponse(
        String marketName,
        Long marketId,
        String marketAddress
) {
    public static UserMerchantMarketResponse from(String name, Long marketId, String marketAddress) {
        return UserMerchantMarketResponse.builder()
                .marketName(name)
                .marketId(marketId)
                .marketAddress(marketAddress)
                .build();
    }
}
