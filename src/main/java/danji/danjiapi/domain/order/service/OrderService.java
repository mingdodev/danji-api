package danji.danjiapi.domain.order.service;

import danji.danjiapi.domain.market.entity.Market;
import danji.danjiapi.domain.market.repository.MarketRepository;
import danji.danjiapi.domain.order.dto.response.CustomerOrderDetail;
import danji.danjiapi.domain.order.entity.Order;
import danji.danjiapi.domain.order.repository.OrderRepository;
import danji.danjiapi.domain.user.entity.Role;
import danji.danjiapi.global.exception.CustomException;
import danji.danjiapi.global.exception.ErrorMessage;
import danji.danjiapi.global.util.resolver.CurrentUserResolver;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CurrentUserResolver currentUserResolver;
    private final MarketRepository marketRepository;

    public List<CustomerOrderDetail> getCustomerOrders() {
        String currentUserRole = currentUserResolver.getCurrentUserRole();
        Long currentUserId = currentUserResolver.getCurrentUserId();

        List<Order> orders;

        if (currentUserRole.equals(Role.CUSTOMER.name())) {
            orders = orderRepository.findByCustomerIdWithOrderItems(currentUserId);
        } else {
            throw new CustomException(ErrorMessage.ORDER_FORBIDDEN);
        }

        Set<Long> merchantIds = orders.stream()
                .map(order -> order.getMerchant().getId())
                .collect(Collectors.toSet());

        Map<Long, Market> marketMap = marketRepository.findAllByUserIdIn(merchantIds).stream()
                .collect(Collectors.toMap(m -> m.getUser().getId(), Function.identity()));

        return orders.stream()
                .map(o -> {
                    Market market = marketMap.get(o.getMerchant().getId());
                    return CustomerOrderDetail.from(o, market);
                })
                .toList();
    }
}
