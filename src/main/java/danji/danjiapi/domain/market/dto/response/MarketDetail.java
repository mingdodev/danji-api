package danji.danjiapi.domain.market.dto.response;

import danji.danjiapi.domain.market.entity.Market;

public record MarketDetail(
        Long id,
        String name,
        String address,
        String imageUrl
) {
    public static MarketDetail from(Market market) {
        return new MarketDetail(
                market.getId(),
                market.getName(),
                market.getAddress(),
                market.getImageUrl()
        );
    }
}
