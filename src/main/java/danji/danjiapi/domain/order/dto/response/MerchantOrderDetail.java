package danji.danjiapi.domain.order.dto.response;

import danji.danjiapi.domain.order.entity.Order;
import danji.danjiapi.domain.user.entity.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record MerchantOrderDetail(
        Long orderId,
        String orderStatus,
        LocalDateTime date,
        String deliveryAddress,
        CustomerSummary customer,
        List<OrderItemDetail> orderItems,
        BigDecimal totalPrices
) {
    public static MerchantOrderDetail from(Order order, User customer) {
        List<OrderItemDetail> orderItemDetails = order.getOrderItems().stream()
                .map(OrderItemDetail::from)
                .toList();

        BigDecimal totalPrice = orderItemDetails.stream()
                .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new MerchantOrderDetail(
                order.getId(),
                order.getStatus().name(),
                order.getDate(),
                order.getDeliveryAddress(),
                CustomerSummary.from(customer),
                orderItemDetails,
                totalPrice
        );
    }
}
