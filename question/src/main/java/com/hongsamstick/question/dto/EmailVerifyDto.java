package com.hongsamstick.question.dto;

public class EmailVerifyDto {

  private String email;
  private String code;

  public EmailVerifyDto(String email, String code) {
    this.email = email;
    this.code = code;
  }

  public String getEmail() {
    return email;
  }

  public String getCode() {
    return code;
  }
}
