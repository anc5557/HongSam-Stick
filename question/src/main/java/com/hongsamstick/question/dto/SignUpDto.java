package com.hongsamstick.question.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpDto {

  // 회원가입 요청에 필요한 데이터
  private String email;
  private String password;
  private String name;
}
