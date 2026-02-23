package com.techietester.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Password encoder – BCrypt is the recommended standard.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Single in-memory user: test / test
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails user = User.builder()
                .username("test")
                .password(encoder.encode("test"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    /**
     * Security filter chain:
     *  - Swagger UI and OpenAPI spec are publicly accessible
     *  - H2 console is publicly accessible (dev convenience)
     *  - Everything else requires HTTP Basic authentication
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for the REST API and H2 console (stateless)
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/app/**", "/h2-console/**")
            )
            // Allow H2 console frames (it uses iframes)
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            )
            .authorizeHttpRequests(auth -> auth
                // Public: Swagger UI page and its assets
                .requestMatchers(
                    "/swagger-ui.html",
                    "/webjars/**",
                    // OpenAPI JSON served by Jersey
                    "/app/openapi.json",
                    "/app/openapi.yaml"
                ).permitAll()
                // Public: H2 console (dev only)
                .requestMatchers("/h2-console/**").permitAll()
                // Everything else must be authenticated
                .anyRequest().authenticated()
            )
            // Enable HTTP Basic Auth but suppress the WWW-Authenticate header
            // so the browser does NOT show its native login dialog on 401.
            // Authentication is handled via the Swagger UI "Authorize" button instead.
            .httpBasic(basic -> basic
                .authenticationEntryPoint((request, response, authException) ->
                    response.sendError(401, "Unauthorized")
                )
            );

        return http.build();
    }
}

