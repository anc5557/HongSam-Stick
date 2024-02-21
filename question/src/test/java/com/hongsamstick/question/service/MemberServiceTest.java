package com.hongsamstick.question.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hongsamstick.question.config.PrincipalDetails;
import com.hongsamstick.question.domain.Member;
import com.hongsamstick.question.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class MemberServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private MemberService memberService;

  @Test
  @DisplayName("회원가입 테스트")
  void registerTest_WhenNewMember_ThenSuccess() {
    // 준비
    String email = "test@example.com";
    String password = "Password123!";
    String name = "TestUser";

    when(memberRepository.existsByEmail(anyString())).thenReturn(false); // 이메일 중복 검사
    when(memberRepository.existsByName(anyString())).thenReturn(false); // 이름 중복 검사
    when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword"); // 비밀번호 암호화
    when(memberRepository.save(any(Member.class))) // 회원 저장
      .thenAnswer(i -> i.getArgument(0)); // 저장된 회원 반환

    // 실행
    Member result = memberService.register(email, password, name); // 회원가입

    // 검증
    assertNotNull(result); // 회원이 정상적으로 생성되었는지 검증
    assertEquals(email, result.getEmail()); // 회원의 이메일이 정상적으로 저장되었는지 검증
    assertEquals("encodedPassword", result.getPassword()); // 회원의 비밀번호가 정상적으로 저장되었는지 검증
    assertEquals(name, result.getName()); // 회원의 이름이 정상적으로 저장되었는지 검증
  }

  @ParameterizedTest
  @DisplayName("회원가입 테스트 - 비밀번호 유효성 검사 실패")
  @ValueSource(
    strings = {
      "short", // 8자 미만
      "thispasswordiswaytoolong", // 16자 초과
      "NoSpecialChar1", // 특수문자 미포함
      "OnlyChars", // 문자만 포함
      "12345678", // 숫자만 포함
    }
  )
  void registerTest_WhenInvalidPassword_ThenFail(String invalidPassword) {
    // 준비
    String email = "test@example.com";
    String name = "TestUser";

    when(memberRepository.existsByEmail(anyString())).thenReturn(false); // 이메일 중복 검사
    when(memberRepository.existsByName(anyString())).thenReturn(false); // 이름 중복 검사

    // 실행 & 검증
    assertThrows(
      RuntimeException.class,
      () -> {
        memberService.register(email, invalidPassword, name); // 회원가입
      }
    );
  }

  @Test
  @DisplayName("이메일 중복 검사 테스트 - 중복되는 이메일이 없을 때")
  void emailExists_WhenExists_ThenReturnTrue() {
    when(memberRepository.existsByEmail(anyString())).thenReturn(true);
    assertTrue(memberService.emailExists("test@example.com"));
  }

  @Test
  @DisplayName("이메일 중복 검사 테스트 - 중복되는 이메일이 있을 때")
  void emailExists_WhenNotExists_ThenReturnFalse() {
    when(memberRepository.existsByEmail(anyString())).thenReturn(false);
    assertFalse(memberService.emailExists("test@example.com"));
  }

  @Test
  @DisplayName("이름 중복 검사 테스트 - 중복되는 이름이 없을 때")
  void nameExists_WhenExists_ThenReturnTrue() {
    when(memberRepository.existsByName(anyString())).thenReturn(true);
    assertTrue(memberService.nameExists("TestUser"));
  }

  @Test
  @DisplayName("이름 중복 검사 테스트 - 중복되는 이름이 있을 때")
  void nameExists_WhenNotExists_ThenReturnFalse() {
    when(memberRepository.existsByName(anyString())).thenReturn(false);
    assertFalse(memberService.nameExists("TestUser"));
  }

  @Test
  @DisplayName("회원 탈퇴 테스트 - 비밀번호가 맞는 경우")
  void testUnregisterWithCorrectPassword() {
    String email = "test@example.com";
    String correctPassword = "correctPassword123!";
    Member mockMember = new Member();
    mockMember.setEmail(email);
    mockMember.setPassword("encodedPassword");

    when(memberRepository.findByEmail(email))
      .thenReturn(Optional.of(mockMember));
    when(passwordEncoder.matches(correctPassword, "encodedPassword"))
      .thenReturn(true);

    boolean result = memberService.unregister(
      new PrincipalDetails(mockMember),
      correctPassword
    );

    assertTrue(result);
    verify(memberRepository, times(1)).deleteByEmail(email);
  }

  @Test
  @DisplayName("회원 탈퇴 테스트 - 비밀번호가 틀린 경우")
  @WithMockUser(username = "test@example.com")
  void testUnregisterWithIncorrectPassword() {
    String email = "test@example.com";
    String correctPassword = "correctPassword123!";
    String incorrectPassword = "wrongPassword!";
    Member mockMember = new Member();
    mockMember.setEmail(email);
    mockMember.setPassword(passwordEncoder.encode(correctPassword));

    when(memberRepository.findByEmail(email))
      .thenReturn(Optional.of(mockMember));
    when(passwordEncoder.matches(incorrectPassword, mockMember.getPassword()))
      .thenReturn(false);

    boolean result = memberService.unregister(
      new PrincipalDetails(mockMember),
      incorrectPassword
    );
    assertFalse(result);
    verify(memberRepository, never()).deleteByEmail(email);
  }
}
