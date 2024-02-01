package com.hongsamstick.question.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Autowired
  private AuthenticationFailureHandler userLoginFailHandler;

  @Autowired
  private PrincipalDetailsService principalDetailService;

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
      .userDetailsService(principalDetailService)
      .passwordEncoder(bCryptPasswordEncoder());
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(auth -> {
      auth
        // 로그인 및 회원가입 페이지는 인증되지 않은 사용자만 접근 가능
        .requestMatchers("/member/signin", "/member/signup")
        .anonymous()
        // 나머지 경로는 인증된 사용자만 접근 가능
        .requestMatchers("/post/create")
        .authenticated()
        // 그 외 나머지 경로는 모든 사용자 접근 가능
        .anyRequest()
        .permitAll();
    });
    http.formLogin(form -> // 커스텀 로그인
      form
        .loginPage("/member/signin") // 커스텀 로그인 페이지
        .loginProcessingUrl("/member/login-process") // 로그인 처리 URL
        .failureHandler(userLoginFailHandler) // 로그인 실패 핸들러
        .defaultSuccessUrl("/") // 로그인 성공 후 이동할 URL
        .usernameParameter("email")
        .passwordParameter("password")
    );

    http.logout(logout -> // 로그아웃 정책
      logout
        .logoutUrl("/member/logout") // 로그아웃 처리 URL
        .logoutSuccessUrl("/") // 로그아웃 성공 후 이동할 URL
        .invalidateHttpSession(true) // 세션 초기화
    );

    return http.build();
  }
}
