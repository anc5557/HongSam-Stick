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
    http.authorizeHttpRequests(auth -> // 인가 정책
      auth
        // 내정보 조회, 수정 페이지, 질문게시판 등록, 수정 페이지는 인증만 되어 있어야 한다.
        .requestMatchers("/mypage", "/mypage/**", "/question/**")
        .authenticated() // 인증만 되어 있으면 접근 가능
        // 그 외의 페이지는 인증이나 권한이 없어도 접근 가능
        .anyRequest()
        .permitAll()
    );
    http.formLogin(form -> // 커스텀 로그인
      form
        .loginPage("/login") // 커스텀 로그인 페이지
        .loginProcessingUrl("/login-process") // 로그인 처리 URL
        .failureHandler(userLoginFailHandler) // 로그인 실패 핸들러
        .defaultSuccessUrl("/home") // 로그인 성공 후 이동할 URL
        .usernameParameter("email")
        .passwordParameter("password")
        .permitAll() // 로그인 페이지는 인증이나 권한이 없어도 접근 가능
    );

    http.logout(logout -> // 로그아웃 정책
      logout
        .logoutUrl("/logout") // 로그아웃 처리 URL
        .logoutSuccessUrl("/home") // 로그아웃 성공 후 이동할 URL
        .invalidateHttpSession(true) // 세션 초기화
    );

    return http.build();
  }
}
