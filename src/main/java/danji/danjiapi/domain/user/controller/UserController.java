package danji.danjiapi.domain.user.controller;

import danji.danjiapi.domain.user.dto.request.UserCreateCustomerRequest;
import danji.danjiapi.domain.user.dto.request.UserCreateMerchantRequest;
import danji.danjiapi.domain.user.dto.response.UserCreateMerchantResponse;
import danji.danjiapi.domain.user.dto.response.UserCreateCustomerResponse;
import danji.danjiapi.domain.user.service.UserService;
import danji.danjiapi.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup/customer")
    @Operation(summary = "일반 회원 가입", description = "일반 회원의 회원 가입을 진행합니다.")
    public ApiResponse<UserCreateCustomerResponse> signupCustomer(@Valid @RequestBody UserCreateCustomerRequest request) {
        return ApiResponse.success(userService.signupCustomer(request));
    }

    @PostMapping("/signup/merchant")
    @Operation(summary = "사장님 회원 가입", description = "사장님의 회원 가입을 진행합니다. 회원 가입과 가게 생성이 하나의 프로세스로 진행됩니다.")
    public ApiResponse<UserCreateMerchantResponse> signupMerchant(@Valid @RequestBody UserCreateMerchantRequest request) {
        return ApiResponse.success(userService.signupMerchant(request));
    }
}
