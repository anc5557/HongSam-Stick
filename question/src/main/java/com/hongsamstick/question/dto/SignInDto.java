package com.hongsamstick.question.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignInDto {

  private String email;
  private String password;
}
