package danji.danjiapi.global.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Processes incoming HTTP requests to extract and validate a JWT token from the Authorization header.
     * <p>
     * If a valid JWT token is present, sets the corresponding Authentication in the Spring Security context
     * for the current request. Continues the filter chain regardless of token presence or validity.
     *
     * @param servletRequest  the incoming ServletRequest, expected to be an HttpServletRequest
     * @param servletResponse the outgoing ServletResponse
     * @param filterChain     the filter chain to continue processing the request
     * @throws IOException      if an I/O error occurs during filtering
     * @throws ServletException if a servlet error occurs during filtering
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        String token = resolveToken((HttpServletRequest) servletRequest);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * Extracts the JWT token from the Authorization header of the given HTTP request.
     *
     * @param request the HTTP request from which to extract the token
     * @return the JWT token string if present and prefixed with "Bearer ", or {@code null} if not found
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
