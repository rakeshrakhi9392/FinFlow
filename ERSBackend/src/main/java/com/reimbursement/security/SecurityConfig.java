package com.reimbursement.security;

import com.reimbursement.config.CorsProperties;
import com.reimbursement.constant.AppConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring Security RBAC for the enterprise reimbursement workflow.
 *
 * <p>Roles: EMPLOYEE, MANAGER, SENIOR_MANAGER, FINANCE, ADMIN</p>
 *
 * <p>CORS is configured only here (not via {@code @CrossOrigin} or a second MVC config).</p>
 *
 * <p>CSRF is disabled because the SPA and API are cross-origin; cookie-based CSRF tokens
 * are not readable by the SPA. Mitigations: strict CORS allow-list, {@code SameSite} cookies,
 * and session auth. Prefer same-origin reverse-proxy + CSRF for hardened production.</p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CorsProperties corsProperties;

    public SecurityConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/swagger-ui/index.html"
                        ).permitAll()
                        .requestMatchers(
                                "/actuator/health",
                                "/actuator/health/**",
                                "/actuator/info"
                        ).permitAll()
                        .requestMatchers("/actuator/metrics", "/actuator/metrics/**")
                        .hasAnyRole("ADMIN", "FINANCE")
                        .requestMatchers(HttpMethod.POST, AppConstants.API_USERS, AppConstants.API_USERS + "/login")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, AppConstants.API_USERS + "/logout")
                        .authenticated()
                        .requestMatchers(HttpMethod.GET, AppConstants.API_WORKFLOW)
                        .hasAnyRole("ADMIN", "MANAGER", "SENIOR_MANAGER", "FINANCE")
                        .requestMatchers(HttpMethod.PUT, AppConstants.API_WORKFLOW)
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, AppConstants.API_REIMBURSEMENTS)
                        .hasAnyRole("EMPLOYEE", "ADMIN")
                        .requestMatchers(HttpMethod.GET, AppConstants.API_REIMBURSEMENTS + "/queue")
                        .hasAnyRole("MANAGER", "SENIOR_MANAGER", "FINANCE", "ADMIN")
                        .requestMatchers(HttpMethod.POST, AppConstants.API_REIMBURSEMENTS + "/approve/**")
                        .hasAnyRole("MANAGER", "SENIOR_MANAGER", "FINANCE", "ADMIN")
                        .requestMatchers(HttpMethod.POST, AppConstants.API_REIMBURSEMENTS + "/deny/**")
                        .hasAnyRole("MANAGER", "SENIOR_MANAGER", "FINANCE", "ADMIN")
                        .requestMatchers(HttpMethod.POST, AppConstants.API_REIMBURSEMENTS + "/mark-paid/**")
                        .hasAnyRole("FINANCE", "ADMIN")
                        .requestMatchers(AppConstants.API_VENDOR + "/**")
                        .hasAnyRole("FINANCE", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, AppConstants.API_USERS + "/**")
                        .hasRole("ADMIN")
                        .requestMatchers(AppConstants.API_BUDGETS + "/**")
                        .hasAnyRole("MANAGER", "SENIOR_MANAGER", "FINANCE", "ADMIN")
                        .requestMatchers(AppConstants.API_DEPARTMENTS + "/**", AppConstants.API_CATEGORIES + "/**")
                        .authenticated()
                        .requestMatchers(AppConstants.API_REIMBURSEMENTS + "/**")
                        .authenticated()
                        .anyRequest().authenticated()
                )
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable());

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Authentication required\"}");
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"status\":403,\"error\":\"Forbidden\",\"message\":\"Access denied\"}");
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(resolveOrigins());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private List<String> resolveOrigins() {
        List<String> configured = corsProperties.getAllowedOrigins();
        if (configured == null || configured.isEmpty()) {
            return List.of(AppConstants.CORS_ORIGIN_LOCAL, "http://localhost:8081");
        }
        return configured.stream()
                .flatMap(value -> Arrays.stream(value.split(",")))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }
}
