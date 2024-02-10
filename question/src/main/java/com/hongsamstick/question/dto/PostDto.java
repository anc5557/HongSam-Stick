package com.hongsamstick.question.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

  @NotNull(message = "제목은 필수입니다.")
  @Size(
    min = 1,
    max = 255,
    message = "제목은 1자 이상 255자 이하이어야 합니다."
  )
  private String title;

  @NotNull(message = "내용은 필수입니다.")
  private String content;

  @NotNull(message = "읽기 권한 설정은 필수입니다.")
  private Integer readPermission;

  @NotNull(message = "쓰기 권한 설정은 필수입니다.")
  private Integer writePermission;

  @FutureOrPresent(message = "종료 날짜는 현재 또는 미래의 날짜여야 합니다.")
  private LocalDateTime endDate;
}
