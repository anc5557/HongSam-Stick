package com.hongsamstick.question.dto;

import com.hongsamstick.question.domain.Member;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class PostCreateDto {

  private Member member;
  private String title;
  private String content;
  private Integer readPermission;
  private Integer writePermission;
  private LocalDateTime endDate;
}
