package com.samsamhajo.deepground.global.config;

import com.samsamhajo.deepground.auth.jwt.JwtAuthenticationFilter;
import com.samsamhajo.deepground.auth.jwt.JwtProvider;
import com.samsamhajo.deepground.auth.security.CustomUserDetailsService;
import com.samsamhajo.deepground.member.repository.MemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] PERMIT_URL_ARRAY = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**",
            "/v3/api-docs.yaml",
            "/auth/**",
            "/ws/**",
            "/**"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtProvider jwtProvider,
            CustomUserDetailsService userDetailsService,
            MemberRepository memberRepository
    ) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PERMIT_URL_ARRAY).permitAll()
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtProvider, userDetailsService, memberRepository),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}