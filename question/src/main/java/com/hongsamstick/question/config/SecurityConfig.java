package com.hongsamstick.question.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http
                // 인가 정책
                .authorizeHttpRequests((auth) -> auth 
                    // 내정보 조회, 수정 페이지, 질문게시판 등록, 수정 페이지는 인증만 되어 있어야 한다.
                    .requestMatchers("/mypage", "/mypage/**", "/question/**").authenticated() // 인증만 되어 있으면 접근 가능
                    // 그 외의 페이지는 인증이나 권한이 없어도 접근 가능
                    .anyRequest().permitAll()
                );
        http    
                // 커스텀 로그인
                .formLogin((form) -> form
                    .loginPage("/login") // 커스텀 로그인 페이지
                    .loginProcessingUrl("/login-process") // 로그인 처리 URL
                    .permitAll() // 로그인 페이지는 인증이나 권한이 없어도 접근 가능
                );  
        http
                // 세션 정책
                .sessionManagement((session) -> session
                    .maximumSessions(1) // 최대 세션 수
                    .maxSessionsPreventsLogin(true) // 동시 로그인 차단
                );
        http
                // 로그아웃 정책
                .logout((logout) -> logout
                    .logoutUrl("/logout") // 로그아웃 처리 URL
                    .logoutSuccessUrl("/") // 로그아웃 성공 후 이동할 URL
                    .invalidateHttpSession(true) // 세션 초기화
                );
            
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

    return new BCryptPasswordEncoder();
}
}