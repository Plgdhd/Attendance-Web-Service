package belstuattend.by.qr_attendance.config;

import java.util.Arrays;
import java.util.Collections;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import belstuattend.by.qr_attendance.components.JWTFilter;
import belstuattend.by.qr_attendance.security.JWTUtil;
import belstuattend.by.qr_attendance.security.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final JWTFilter jwtFilter;
    private final Logger logger;

    @Autowired
    public SecurityConfig(UserDetailsServiceImpl userDetailsServiceImpl, JWTFilter jwtFilter,
                            Logger logger){
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.jwtFilter = jwtFilter;
        this.logger = logger;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

        @SuppressWarnings("deprecation")
        @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring security filter chain");

            http
                    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                    .csrf(csrf -> csrf.disable())
                    .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeRequests(authorize -> {
                        logger.info("Configuring authorization rules");

                        authorize
                                // Публичные эндпоинты (без аутентификации)
                                .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/create-admin").permitAll()
                                // Эндпоинты, требующие аутентификации и определенной роли
                                .requestMatchers("/api/auth/user/info").authenticated()
                                .requestMatchers("/api/auth/passchange").authenticated()
                                .requestMatchers("/api/qr/verify").authenticated()
                                .requestMatchers("/api/attendance/record").authenticated()
                                .requestMatchers("/api/attendance/user").authenticated()
                                .requestMatchers("/api/disciplines").authenticated()
                                // Администратор - используем hasRole вместо hasAuthority, Spring Security автоматически добавит префикс ROLE_
                                .requestMatchers("/api/qr/generate").hasRole("ADMIN")
                                .requestMatchers("/api/qr/current-codes").hasRole("ADMIN")
                                .requestMatchers("/api/attendance/all").hasRole("ADMIN")
                                .requestMatchers("/api/disciplines/init-default").hasRole("ADMIN")
                                .requestMatchers("/api/disciplines/add").hasRole("ADMIN")
                                .requestMatchers("/api/disciplines/update/**").hasRole("ADMIN")
                                .requestMatchers("/api/disciplines/delete/**").hasRole("ADMIN")
                                // По умолчанию требуется аутентификация
                                .anyRequest().authenticated();
                    })
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        logger.info("Security filter chain configured successfully");
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder
            .userDetailsService(userDetailsServiceImpl)
            .passwordEncoder(passwordEncoder());
        return builder.build();
    }

    // @Bean
    // public AuthenticationManager authenticationManager(
    //         AuthenticationConfiguration authenticationConfiguration
    // ) throws Exception {
    //     return authenticationConfiguration.getAuthenticationManager();
    // }
}

