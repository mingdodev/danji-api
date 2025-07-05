package danji.danjiapi.domain.market.dto.response;

import danji.danjiapi.domain.market.entity.Market;

public record MarketSummary(
        Long id,
        String name,
        String address,
        String imageUrl
) {
    public static MarketSummary from(Market market) {
        return new MarketSummary(
                market.getId(),
                market.getName(),
                market.getAddress(),
                market.getImageUrl()
        );
    }
}
