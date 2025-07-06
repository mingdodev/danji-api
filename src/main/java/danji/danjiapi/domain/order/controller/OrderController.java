package danji.danjiapi.domain.order.controller;

import danji.danjiapi.domain.order.dto.request.OrderCreateRequest;
import danji.danjiapi.domain.order.dto.request.OrderStatusUpdateRequest;
import danji.danjiapi.domain.order.dto.request.OrderUpdateRequest;
import danji.danjiapi.domain.order.dto.response.CustomerOrderDetail;
import danji.danjiapi.domain.order.dto.response.MerchantOrderDetail;
import danji.danjiapi.domain.order.dto.response.OrderCreateResponse;
import danji.danjiapi.domain.order.dto.response.OrderStatusUpdateResponse;
import danji.danjiapi.domain.order.service.OrderService;
import danji.danjiapi.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/orders")
    @Operation(summary = "고객의 주문 요청", description = "고객이 주문을 요청(생성)합니다. 요청은 PENDING 상태로 생성됩니다.")
    public ApiResponse<OrderCreateResponse> create(@Valid @RequestBody OrderCreateRequest request) {
        return ApiResponse.success(orderService.create(request));
    }

    @PutMapping("/orders")
    @Operation(summary = "사장님의 주문 수정", description = "사장님이 주문(주문 항목의 가격, 수량)을 수정합니다.")
    public ApiResponse<Void> update(@Valid @RequestBody OrderUpdateRequest request) {
        orderService.update(request);
        return ApiResponse.success(null, "주문 수정이 완료되었습니다.");
    }

    @PatchMapping("/orders/{orderId}/status")
    @Operation(summary = "사장님의 주문 수락 및 거절", description = "사장님이 주문 상태를 수락 또는 거절로 변경합니다.")
    public ApiResponse<OrderStatusUpdateResponse> updateStatus(@PathVariable Long orderId,
                                                               @Valid @RequestBody OrderStatusUpdateRequest request) {
        return ApiResponse.success(orderService.updateStatus(orderId, request));
    }
}
