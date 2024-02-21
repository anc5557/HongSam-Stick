package com.hongsamstick.question.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hongsamstick.question.repository.MemberRepository;
import com.hongsamstick.question.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class SpringSecurityTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MemberService memberService;

  @Autowired
  private MemberRepository memberRepository;

  @BeforeEach
  void beforeEach() {
    if (
      !memberRepository.existsByEmail("test@example.com") ||
      !memberRepository.existsByName("test")
    ) {
      memberService.register("test@example.com", "password123!", "test");
    }
  }

  @Test
  @DisplayName("로그인 테스트")
  void loginTest_WhenCorrectCredentials_ThenSuccess() throws Exception {
    // 실행
    mockMvc
      .perform(
        formLogin("/member/login-process")
          .user("email", "test@example.com")
          .password("password", "password123!")
      )
      .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrl("/"))
      .andExpect(authenticated()); // 인증된 사용자인지 확인
  }

  @Test
  @DisplayName("로그인 테스트 - 실패")
  void loginTest_WhenIncorrectCredentials_ThenFail1() throws Exception {
    // 실행
    mockMvc
      .perform(
        formLogin("/member/login-process")
          .user("email", "test1@example.com")
          .password("password", "password123!")
      )
      .andExpect(status().is3xxRedirection())
      .andExpect(
        redirectedUrl(
          "/member/signin?error=true&exception=%EC%95%84%EC%9D%B4%EB%94%94+%EB%98%90%EB%8A%94+%EB%B9%84%EB%B0%80%EB%B2%88%ED%98%B8%EA%B0%80+%EB%A7%9E%EC%A7%80+%EC%95%8A%EC%8A%B5%EB%8B%88%EB%8B%A4.+%EB%8B%A4%EC%8B%9C+%ED%99%95%EC%9D%B8%ED%95%B4+%EC%A3%BC%EC%84%B8%EC%9A%94."
        )
      )
      .andExpect(unauthenticated()); // 실패한 로그인 후의 리디렉션 URL
  }

  @Test
  @DisplayName("로그인 테스트 - 실패")
  void loginTest_WhenIncorrectCredentials_ThenFail2() throws Exception {
    // 실행
    mockMvc
      .perform(
        formLogin("/member/login-process")
          .user("email", "test@example.com")
          .password("password", "password")
      )
      .andExpect(status().is3xxRedirection())
      .andExpect(
        redirectedUrl(
          "/member/signin?error=true&exception=%EC%95%84%EC%9D%B4%EB%94%94+%EB%98%90%EB%8A%94+%EB%B9%84%EB%B0%80%EB%B2%88%ED%98%B8%EA%B0%80+%EB%A7%9E%EC%A7%80+%EC%95%8A%EC%8A%B5%EB%8B%88%EB%8B%A4.+%EB%8B%A4%EC%8B%9C+%ED%99%95%EC%9D%B8%ED%95%B4+%EC%A3%BC%EC%84%B8%EC%9A%94."
        )
      )
      .andExpect(unauthenticated()); // 실패한 로그인 후의 리디렉션 URL
  }

  @Test
  @DisplayName("로그아웃 테스트")
  void logoutTest() throws Exception {
    mockMvc
      .perform(post("/member/logout").with(csrf()))
      .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrl("/")) // 로그아웃 후의 리디렉션 URL
      .andExpect(unauthenticated());
  }
}
