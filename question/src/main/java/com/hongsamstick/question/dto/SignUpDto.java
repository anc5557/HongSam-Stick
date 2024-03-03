package com.hongsamstick.question.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor
public class SignUpDto {

  // 회원가입 요청에 필요한 데이터
  private String email;
  private String password;
  private String name;
  private String verificationCode;
}
