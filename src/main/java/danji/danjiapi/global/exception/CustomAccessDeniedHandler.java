package danji.danjiapi.global.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import danji.danjiapi.global.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * Handles access denied exceptions by sending a standardized JSON error response with a forbidden status code.
     *
     * Sets the HTTP response status to forbidden, content type to JSON, and writes a serialized error message to the response body when authorization fails.
     *
     * @throws IOException if an input or output exception occurs while writing the response
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        log.warn("Authorization Failed! 🥲");

        ErrorMessage errorMessage = ErrorMessage.AUTH_FORBIDDEN;

        response.setStatus(errorMessage.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = ErrorResponse.of(errorMessage);
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}
