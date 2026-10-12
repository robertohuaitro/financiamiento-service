package cl.partnercars.financiamiento.config;

import cl.partnercars.financiamiento.security.JwtAuthorizationFilter;
import cl.partnercars.financiamiento.security.JwtService;
import cl.partnercars.financiamiento.security.ManejadorErroresSeguridad;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtService jwtService;
    private final ManejadorErroresSeguridad manejadorErroresSeguridad;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(rutas -> rutas
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(HttpMethod.GET, "/entidades-financieras/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/solicitudes").permitAll()
                        .anyRequest().hasRole("ADMIN"))
                .exceptionHandling(errores -> errores
                        .authenticationEntryPoint(manejadorErroresSeguridad)
                        .accessDeniedHandler(manejadorErroresSeguridad))
                .addFilterBefore(new JwtAuthorizationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
