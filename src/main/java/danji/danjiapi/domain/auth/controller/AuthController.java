package danji.danjiapi.domain.auth.controller;

import danji.danjiapi.domain.auth.dto.request.AuthLoginRequest;
import danji.danjiapi.domain.auth.dto.response.AuthLoginResponse;
import danji.danjiapi.domain.auth.service.AuthService;
import danji.danjiapi.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일 아이디와 비밀번호로 로그인을 진행합니다. 로그인의 결과를 통해 회원의 역할을 구분합니다.",
            security = @SecurityRequirement(name = ""))
    public ApiResponse<AuthLoginResponse> login(@Valid @RequestBody AuthLoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }
}
