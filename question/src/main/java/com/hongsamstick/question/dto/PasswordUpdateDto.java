package com.hongsamstick.question.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordUpdateDto {

  @NotBlank(message = "현재 비밀번호를 입력해주세요.")
  private String currentPassword;

  @NotBlank(message = "새 비밀번호를 입력해주세요.")
  @Size(
    min = 8,
    max = 16,
    message = "비밀번호는 8자 이상 16자 이하이어야 합니다."
  )
  @Pattern(
    regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).{8,16}$",
    message = "비밀번호는 영문, 숫자, 특수문자를 포함한 8~16자리여야 합니다."
  )
  private String newPassword;

  @NotBlank(message = "새 비밀번호 확인을 입력해주세요.")
  private String newPasswordConfirm;
}
