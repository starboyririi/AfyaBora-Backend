package com.example.afyabora.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfiguration(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider //ignore Bean warning we will get to that
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/**", "/users/**", "/paystack/**", "/subscriptions/**", "/appointments/**","/zoom/**","/feedback/**").permitAll() // Permit all appointment endpoints
                        .requestMatchers("/admin/**").hasAuthority("Admin") // Admin-only access
                        .requestMatchers("/doctor/**").hasAuthority("Doctor") // Doctor-only access
                        .requestMatchers("/patient/**").hasAuthority("Patient") // Patient-only access
                        .requestMatchers("/paystack/pay").permitAll() // Allow payment initiation
                        .requestMatchers("/paystack/verify").permitAll() // Allow transaction verification
                        .requestMatchers("/subscriptions/all").permitAll() // Allow fetching all active subscriptions
                        .requestMatchers("/subscriptions/check").permitAll() // View subscriptions for a specific user
                        .requestMatchers("/users/me").authenticated()
                        .requestMatchers("/zoom/webhook").permitAll() // Allow Zoom webhook events
                        .requestMatchers("/feedback/submit").permitAll()
                        .requestMatchers("/feedback/doctor/").permitAll()
                        .requestMatchers("/zoom/create").authenticated() // Only logged-in users can create meetings
                        .requestMatchers("/appointments/book").authenticated()
                        .requestMatchers("/users/doctors/specialty").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("https://app-backend.com", "http://localhost:8080")); //TODO: update backend url
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
