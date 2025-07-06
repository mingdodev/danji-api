package danji.danjiapi.domain.order.dto.response;

import danji.danjiapi.domain.market.entity.Market;

public record MarketSummary(
        Long id,
        String name,
        String address
) {
    public static MarketSummary from(Market market) {
        return new MarketSummary(
                market.getId(),
                market.getName(),
                market.getAddress()
        );
    }
}
