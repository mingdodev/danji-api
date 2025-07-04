package danji.danjiapi.global.auth;

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

    /**
     * Handles authentication failures by sending a structured JSON error response with an appropriate HTTP status code.
     *
     * If the authentication exception is caused by a custom authentication exception, a specific error message is returned; otherwise, a generic unauthorized error message is used.
     *
     * @param request the HTTP request that resulted in an authentication failure
     * @param response the HTTP response to be sent to the client
     * @param authException the exception that triggered the authentication failure
     * @throws IOException if an input or output error occurs while writing the response
     */
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
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}
