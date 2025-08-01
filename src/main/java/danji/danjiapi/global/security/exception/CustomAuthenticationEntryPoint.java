package danji.danjiapi.global.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import danji.danjiapi.global.exception.ErrorMessage;
import danji.danjiapi.global.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.warn("Authentication Failed! 🥲");

        ErrorMessage errorMessage = ErrorMessage.UNAUTHORIZED;

        Throwable cause = authException.getCause();
        if (cause instanceof CustomAuthException customAuthException) {
            errorMessage = customAuthException.getErrorMessage();
        }
        response.setStatus(errorMessage.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = ErrorResponse.of(errorMessage);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
