package danji.danjiapi.domain.user.controller;

import danji.danjiapi.domain.user.dto.request.UserCreateRequest;
import danji.danjiapi.domain.user.dto.response.UserCreateResponse;
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

    @PostMapping("/signup")
    @Operation(summary = "회원 가입", description = "이메일 아이디로 회원 가입을 진행합니다.")
    public ApiResponse<UserCreateResponse> signup(@Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.success(userService.signup(request));
    }
}
