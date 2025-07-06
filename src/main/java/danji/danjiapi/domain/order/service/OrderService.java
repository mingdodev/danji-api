package danji.danjiapi.domain.order.service;

import danji.danjiapi.domain.order.dto.request.OrderItemDetail;
import danji.danjiapi.domain.order.dto.request.OrderItemInfo;
import danji.danjiapi.domain.market.entity.Market;
import danji.danjiapi.domain.market.repository.MarketRepository;
import danji.danjiapi.domain.order.dto.request.OrderCreateRequest;
import danji.danjiapi.domain.order.dto.request.OrderUpdateRequest;
import danji.danjiapi.domain.order.dto.response.CustomerOrderDetail;
import danji.danjiapi.domain.order.dto.response.MerchantOrderDetail;
import danji.danjiapi.domain.order.dto.response.OrderCreateResponse;
import danji.danjiapi.domain.order.entity.Order;
import danji.danjiapi.domain.order.entity.OrderItem;
import danji.danjiapi.domain.order.entity.OrderStatus;
import danji.danjiapi.domain.order.repository.OrderRepository;
import danji.danjiapi.domain.user.entity.Role;
import danji.danjiapi.domain.user.entity.User;
import danji.danjiapi.domain.user.repository.UserRepository;
import danji.danjiapi.global.exception.CustomException;
import danji.danjiapi.global.exception.ErrorMessage;
import danji.danjiapi.global.util.resolver.CurrentUserResolver;
import danji.danjiapi.global.util.validator.AccessValidator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CurrentUserResolver currentUserResolver;
    private final MarketRepository marketRepository;
    private final UserRepository userRepository;

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

    public List<MerchantOrderDetail> getMerchantOrders() {
        String currentUserRole = currentUserResolver.getCurrentUserRole();
        Long currentUserId = currentUserResolver.getCurrentUserId();

        List<Order> orders;

        if (currentUserRole.equals(Role.MERCHANT.name())) {
            orders = orderRepository.findByMerchantIdWithOrderItems(currentUserId);
        } else {
            throw new CustomException(ErrorMessage.ORDER_FORBIDDEN);
        }

        Set<Long> customerIds = orders.stream()
                .map(order -> order.getCustomer().getId())
                .collect(Collectors.toSet());

        Map<Long, User> customerMap = userRepository.findAllById(customerIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return orders.stream()
                .map(o -> {
                    User customer = customerMap.get(o.getCustomer().getId());
                    return MerchantOrderDetail.from(o, customer);
                })
                .toList();
    }

    @Transactional
    public OrderCreateResponse create(OrderCreateRequest request) {
        Long currentUserId = currentUserResolver.getCurrentUserId();
        User customer = userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(ErrorMessage.USER_NOT_FOUND));

        Market market = marketRepository.findById(request.marketId())
                .orElseThrow(() -> new CustomException(ErrorMessage.MARKET_NOT_FOUND));
        User merchant = market.getUser();

        Order order = Order.create(OrderStatus.PENDING, request.date(), request.deliveryAddress(), customer, merchant);

        for (OrderItemInfo itemInfo : request.orderItems()) {
            OrderItem item = OrderItem.create(itemInfo.name(), itemInfo.price(), itemInfo.quantity());

            order.addOrderItem(item);
        }

        return OrderCreateResponse.from(orderRepository.save(order));
    }

    @Transactional
    public void update(OrderUpdateRequest request) {
        Long currentUserId = currentUserResolver.getCurrentUserId();
        String currentUserRole = currentUserResolver.getCurrentUserRole();

        if (!Role.MERCHANT.name().equals(currentUserRole)) {
            throw new CustomException(ErrorMessage.ORDER_FORBIDDEN);
        }

        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new CustomException(ErrorMessage.ORDER_NOT_FOUND));

        AccessValidator.validateOrderAccess(order, currentUserId);

        Map<Long, OrderItem> itemMap = order.getOrderItems().stream()
                .collect(Collectors.toMap(OrderItem::getId, Function.identity()));

        for (OrderItemDetail detail : request.orderItems()) {
            OrderItem item = itemMap.get(detail.id());
            if (item == null) {
                throw new CustomException(ErrorMessage.ORDER_ITEM_NOT_FOUND);
            }

            item.update(detail.price(), detail.quantity());
        }
    }
}
