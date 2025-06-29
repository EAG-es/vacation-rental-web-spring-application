package com.vacationstay.config;

import com.vacationstay.security.CustomOAuth2UserService;
import com.vacationstay.security.JwtAuthenticationEntryPoint;
import com.vacationstay.security.JwtAuthenticationFilter;
import com.vacationstay.security.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Security configuration for the VacationStay application.
 * <p>
 * This class configures Spring Security, including authentication, authorization,
 * OAuth2 login, JWT authentication for APIs, and password encoding.
 * </p>
 *
 * @author VacationStay Team
 * @version 1.0
 * @since 2023-06-18
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configures the security filter chain.
     * <p>
     * This method defines URL-based security, authentication methods,
     * JWT authentication for APIs, and other security-related configurations.
     * </p>
     *
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Public web pages (form-based authentication)
                .requestMatchers("/", "/properties", "/properties/**", "/css/**", "/js/**", "/images/**", "/h2-console/**", "/error/**").permitAll()
                // Public API endpoints (no authentication required)
                .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                .requestMatchers("/api/properties/**").permitAll()
                // OAuth2 and form login endpoints
                .requestMatchers("/oauth2/**", "/login/**", "/register").permitAll()
                // Protected web pages (form-based authentication)
                .requestMatchers("/dashboard", "/bookings/**", "/reviews/add").authenticated()
                // JWT-only API endpoints (require valid JWT token)
                .requestMatchers("/api/bookings/**", "/api/auth/me").authenticated()
                .anyRequest().authenticated()
            )
            // Configure session management - stateless for APIs, stateful for web
                //  The session management is set to SessionCreationPolicy.IF_REQUIRED,
                //  which means it could potentially accept both JWT tokens and session-based authentication.
                //  For API endpoints /api/bookings/**, we need to ensure they are stateless
                //  and only accept JWT tokens programming extra checks.
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            // Form login for web interface
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .permitAll()
            )
            // OAuth2 login for web interface
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                )
                .successHandler(oAuth2AuthenticationSuccessHandler)
            )
            // Logout configuration
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            // JWT authentication entry point for API endpoints
            .exceptionHandling(exceptions -> exceptions
                .defaultAuthenticationEntryPointFor(
                    jwtAuthenticationEntryPoint,
                    new AntPathRequestMatcher("/api/**")
                )
            )
            // CSRF configuration
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**", "/api/**")
            )
            // Headers configuration
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
            )
            // Add JWT filter before UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    /**
     * Creates a password encoder bean.
     * <p>
     * This method provides a BCryptPasswordEncoder for secure password hashing.
     * </p>
     *
     * @return the password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Creates an authentication manager bean.
     *
     * @param authConfig the authentication configuration
     * @return the authentication manager
     * @throws Exception if an error occurs during creation
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}