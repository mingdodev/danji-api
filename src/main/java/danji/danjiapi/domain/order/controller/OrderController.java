package danji.danjiapi.domain.order.controller;

import danji.danjiapi.domain.order.dto.response.CustomerOrderDetail;
import danji.danjiapi.domain.order.dto.response.MerchantOrderDetail;
import danji.danjiapi.domain.order.service.OrderService;
import danji.danjiapi.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/customers/me/orders")
    @Operation(summary = "고객의 주문 목록 조회", description = "고객이 모든 주문 목록을 조회합니다. 주문은 주문 항목들과 주문 상태를 포함합니다.")
    public ApiResponse<List<CustomerOrderDetail>> getCustomerOrders() {
        return ApiResponse.success(orderService.getCustomerOrders());
    }

    @GetMapping("/merchants/me/orders")
    @Operation(summary = "사장님의 주문 목록 조회", description = "사장님이 모든 주문 목록을 조회합니다. 주문은 주문 항목들과 주문 상태를 포함합니다.")
    public ApiResponse<List<MerchantOrderDetail>> getMerchantOrders() {
        return ApiResponse.success(orderService.getMerchantOrders());
    }
}
