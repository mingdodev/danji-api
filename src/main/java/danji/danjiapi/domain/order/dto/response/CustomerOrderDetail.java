package danji.danjiapi.domain.order.dto.response;

import danji.danjiapi.domain.market.entity.Market;
import danji.danjiapi.domain.order.entity.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CustomerOrderDetail(
        Long orderId,
        String orderStatus,
        LocalDateTime date,
        String deliveryAddress,
        MarketSummary market,
        List<OrderItemDetail> orderItems,
        BigDecimal totalPrice
) {
    public static CustomerOrderDetail from(Order order, Market market) {
        List<OrderItemDetail> orderItemDetails = order.getOrderItems().stream()
                .map(OrderItemDetail::from)
                .toList();

        BigDecimal totalPrice = orderItemDetails.stream()
                .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CustomerOrderDetail(
                order.getId(),
                order.getStatus().name(),
                order.getDate(),
                order.getDeliveryAddress(),
                MarketSummary.from(market),
                orderItemDetails,
                totalPrice
        );
    }
}
