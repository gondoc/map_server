package com.gondo.map.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .httpBasic(AbstractHttpConfigurer::disable)

                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authConfig -> authConfig
                        // 정적 리소스 허용
                        .requestMatchers("/images/**").permitAll()

                        // 인증이 필요 없는 공개 API들
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/refresh").permitAll()
                        .requestMatchers("/api/auth/logout").permitAll()
                        .requestMatchers("/api/auth/forgotPw").permitAll()
                        .requestMatchers("/api/auth/resetPw").permitAll()

                        .requestMatchers("/api/user/signUp").permitAll()
                        .requestMatchers("/api/user/checkId").permitAll()
                        .requestMatchers("/api/user/checkNick").permitAll()
                        .requestMatchers("/api/user/findId").permitAll()
                        .requestMatchers("/api/user/findPw").permitAll()

                        // GET 요청으로 공개된 조회 API들
                        .requestMatchers("/api/hist/items").permitAll()
                        .requestMatchers("/api/hist/year-items").permitAll()
                        .requestMatchers("/api/site/items").permitAll()
                        .requestMatchers("/api/category/items").permitAll()
                        .requestMatchers("/api/category/stat").permitAll()

                        // 불허용
                        .requestMatchers("/api/auth/me").authenticated()
                        .requestMatchers("/api/user/mng/**").authenticated()

                        // 그 외 모든 요청은 JWT 필터에서 처리
                        .anyRequest().permitAll()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return httpSecurity.build();
    }
}


















