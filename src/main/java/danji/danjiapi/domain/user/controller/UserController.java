package danji.danjiapi.domain.user.controller;

import danji.danjiapi.domain.user.dto.request.UserCreateCustomerRequest;
import danji.danjiapi.domain.user.dto.request.UserCreateMerchantRequest;
import danji.danjiapi.domain.user.dto.response.UserCreateMerchantResponse;
import danji.danjiapi.domain.user.dto.response.UserCreateCustomerResponse;
import danji.danjiapi.domain.user.dto.response.UserMerchantMarketResponse;
import danji.danjiapi.domain.user.service.UserService;
import danji.danjiapi.global.annotation.MultipartJsonRequest;
import danji.danjiapi.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup/customer")
    @Operation(summary = "일반 회원 가입", description = "일반 회원의 회원 가입을 진행합니다.",
            security = @SecurityRequirement(name = ""))
    public ApiResponse<UserCreateCustomerResponse> signupCustomer(@Valid @RequestBody UserCreateCustomerRequest request) {
        log.info("POST /api/users/signup/customer");
        return ApiResponse.success(userService.signupCustomer(request));
    }

    @PostMapping(value = "/signup/merchant", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @MultipartJsonRequest
    @Operation(summary = "사장님 회원 가입", description = "사장님의 회원 가입을 진행합니다. 회원 가입과 가게 생성이 하나의 프로세스로 진행됩니다.",
            security = @SecurityRequirement(name = ""))
    public ApiResponse<UserCreateMerchantResponse> signupMerchant(@Valid @RequestPart("request") UserCreateMerchantRequest request,
                                                                  @RequestPart(value = "image", required = false) MultipartFile image) {
        log.info("POST /api/users/signup/merchant");
        return ApiResponse.success(userService.signupMerchant(request, image));
    }

    @GetMapping("/merchant/{userId}/market")
    @Operation(summary = "사장님의 가게 정보 조회")
    public ApiResponse<UserMerchantMarketResponse> getMarket(@PathVariable Long userId) {
        log.info("GET /api/merchant/{userId}/market");
        return ApiResponse.success(userService.getMarket(userId));
    }
}
