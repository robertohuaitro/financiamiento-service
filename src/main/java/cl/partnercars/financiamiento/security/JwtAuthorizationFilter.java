package cl.partnercars.financiamiento.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    public static final String ATRIBUTO_ERROR_TOKEN = "jwt.error";
    private static final String PREFIJO_BEARER = "Bearer ";

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String cabecera = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (cabecera != null && cabecera.startsWith(PREFIJO_BEARER)) {
            autenticar(cabecera.substring(PREFIJO_BEARER.length()), request);
        }
        filterChain.doFilter(request, response);
    }

    private void autenticar(String token, HttpServletRequest request) {
        try {
            Claims claims = jwtService.obtenerClaims(token);
            UsernamePasswordAuthenticationToken autenticacion =
                    new UsernamePasswordAuthenticationToken(claims.getSubject(), null, extraerAutoridades(claims));
            SecurityContextHolder.getContext().setAuthentication(autenticacion);
            log.debug("Token válido de {} en {}", claims.getSubject(), request.getRequestURI());
        } catch (ExpiredJwtException e) {
            log.warn("Token expirado en {}", request.getRequestURI());
            request.setAttribute(ATRIBUTO_ERROR_TOKEN, "El token expiró, inicia sesión nuevamente");
            SecurityContextHolder.clearContext();
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token inválido en {}: {}", request.getRequestURI(), e.getMessage());
            request.setAttribute(ATRIBUTO_ERROR_TOKEN, "El token es inválido");
            SecurityContextHolder.clearContext();
        }
    }

    private List<SimpleGrantedAuthority> extraerAutoridades(Claims claims) {
        Object valor = claims.get(JwtService.CLAIM_AUTHORITIES);
        if (!(valor instanceof List<?> autoridades)) {
            return List.of();
        }
        return autoridades.stream()
                .map(String::valueOf)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}
