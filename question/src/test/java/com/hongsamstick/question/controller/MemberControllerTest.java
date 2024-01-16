package com.hongsamstick.question.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.hongsamstick.question.dto.SignUpDto;
import com.hongsamstick.question.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemberController.class)
public class MemberControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private MemberService memberService;

  @Test
  @DisplayName("회원가입 페이지 렌더링")
  public void registerPageTest() throws Exception {
    mockMvc
      .perform(get("/member/signup").with(csrf()))
      .andExpect(status().isOk())
      .andExpect(view().name("signup"))
      .andExpect(model().attributeExists("signupDto"));
  }

  // 회원가입 성공 테스트
  @Test
  void testRegisterSuccess() throws Exception {
    SignUpDto signupDto = new SignUpDto(
      "user@example.com",
      "password123!",
      "User"
    );
    mockMvc
      .perform(
        post("/member/signup").flashAttr("signupDto", signupDto).with(csrf())
      )
      .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrl("/"));
  }

  // 이메일 중복 테스트
  @Test
  void testEmailDuplication() throws Exception {
    doThrow(new RuntimeException("이미 가입된 이메일입니다."))
      .when(memberService)
      .register(anyString(), anyString(), anyString());
    SignUpDto signupDto = new SignUpDto(
      "duplicate@example.com",
      "password123!",
      "User"
    );

    mockMvc
      .perform(
        post("/member/signup").flashAttr("signupDto", signupDto).with(csrf())
      )
      .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrl("/member/signup"));
  }

  // 이름 중복 테스트
  @Test
  void testNameDuplication() throws Exception {
    doThrow(new RuntimeException("이미 존재하는 이름입니다."))
      .when(memberService)
      .register(anyString(), anyString(), anyString());
    SignUpDto signupDto = new SignUpDto(
      "user@example.com",
      "password123!",
      "Duplicate"
    );

    mockMvc
      .perform(
        post("/member/signup").flashAttr("signupDto", signupDto).with(csrf())
      )
      .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrl("/member/signup"));
  }

  // 비밀번호 유효성 검사 테스트
  @Test
  void testInvalidPassword() throws Exception {
    doThrow(
      new RuntimeException(
        "비밀번호는 영문, 숫자, 특수문자를 포함한 8~16자리여야 합니다."
      )
    )
      .when(memberService)
      .register(anyString(), anyString(), anyString());
    SignUpDto signupDto = new SignUpDto(
      "user@example.com",
      "wrong_password",
      "User"
    );

    mockMvc
      .perform(
        post("/member/signup").flashAttr("signupDto", signupDto).with(csrf())
      )
      .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrl("/member/signup"));
  }
}
