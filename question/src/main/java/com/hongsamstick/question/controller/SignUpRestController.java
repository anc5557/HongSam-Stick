package com.hongsamstick.question.controller;

import com.hongsamstick.question.dto.EmailDto;
import com.hongsamstick.question.dto.EmailVerifyDto;
import com.hongsamstick.question.dto.NameDto;
import com.hongsamstick.question.service.MemberService;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/signup")
public class SignUpRestController {

  private final MemberService memberService;

  public SignUpRestController(MemberService memberService) {
    this.memberService = memberService;
  }

  // 이메일 인증 코드 전송
  /**
   * 지정된 이메일 주소로 이메일 인증 코드를 전송합니다.
   *
   * @param emailDto 이메일 주소를 포함한 이메일 DTO
   * @return 작업 결과를 포함한 ResponseEntity
   */
  @PostMapping("/send-email-verification-code")
  public ResponseEntity<?> sendEmailVerificationCode(
    @RequestBody EmailDto emailDto
  ) {
    try {
      memberService.sendVerificationCode(emailDto.getEmail());
      // 인증 코드 전송 성공 응답
      return ResponseEntity
        .ok()
        .body(Map.of("message", "인증 코드가 이메일로 전송되었습니다."));
    } catch (RuntimeException ex) {
      // 오류 처리 (예: 이미 가입된 이메일, 이메일 형식 오류 등)
      return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
  }

  // 이름 중복 검사
  @PostMapping("/check-name")
  public ResponseEntity<?> checkName(@RequestBody NameDto nameDto) {
    boolean exists = memberService.nameExists(nameDto.getName());
    if (exists) {
      // 이름이 중복될 경우
      return ResponseEntity
        .ok()
        .body(Map.of("exists", true, "message", "이미 존재하는 이름입니다."));
    } else {
      // 이름이 중복되지 않을 경우
      return ResponseEntity
        .ok()
        .body(Map.of("exists", false, "message", "사용 가능한 이름입니다."));
    }
  }

  // 인증 코드 확인
  @PostMapping("/check-verification-code")
  public ResponseEntity<?> checkVerificationCode(
    @RequestBody EmailVerifyDto emailVerifyDto
  ) {
    try {
      boolean verified = memberService.verifyCode(
        emailVerifyDto.getEmail(),
        emailVerifyDto.getCode()
      );
      if (verified) {
        return ResponseEntity
          .ok()
          .body(
            Map.of("verified", true, "message", "인증 코드가 확인되었습니다.")
          );
      } else {
        return ResponseEntity
          .status(HttpStatus.UNAUTHORIZED)
          .body(
            Map.of(
              "verified",
              false,
              "message",
              "인증 코드가 일치하지 않습니다."
            )
          );
      }
    } catch (IllegalArgumentException ex) {
      // 인증 코드가 만료되었거나, 전송된 코드가 없는 경우 등
      return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(Map.of("error", ex.getMessage()));
    }
  }

  // 인증 코드 재전송
  @PostMapping("/resend-verification-code")
  public ResponseEntity<?> resendVerificationCode(
    @RequestBody EmailDto emailDto
  ) {
    try {
      memberService.resendVerificationCode(emailDto.getEmail());
      // 새로운 인증 코드 전송 성공 응답
      return ResponseEntity
        .ok()
        .body(Map.of("message", "새로운 인증 코드가 이메일로 전송되었습니다."));
    } catch (IllegalArgumentException ex) {
      // 예외 처리: 전송된 코드가 없거나 다른 문제가 발생한 경우
      return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    } catch (Exception ex) {
      // 기타 예외 처리
      return ResponseEntity
        .internalServerError()
        .body(Map.of("error", "인증 코드 재전송 중 문제가 발생했습니다."));
    }
  }
}
