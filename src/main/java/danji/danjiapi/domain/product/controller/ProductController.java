package danji.danjiapi.domain.product.controller;

import danji.danjiapi.domain.product.dto.request.ProductCreateRequest;
import danji.danjiapi.domain.product.dto.response.ProductCreateResponse;
import danji.danjiapi.domain.product.service.ProductService;
import danji.danjiapi.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("")
    @Operation(summary = "상품 추가", description = "사장님이 가게에 새로운 상품을 추가합니다.")
    public ApiResponse<ProductCreateResponse> add(@Valid ProductCreateRequest request) {
        return ApiResponse.success(productService.add(request));
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "상품 삭제", description = "사장님이 가게의 특정 상품을 삭제합니다.")
    public ApiResponse<Void> delete(@PathVariable Long productId) {
        return ApiResponse.success(productService.delete(productId));
    }
}
