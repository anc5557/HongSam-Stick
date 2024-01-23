package com.hongsamstick.question.dto;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.Getter;

@Getter
public class PostEditDto {

  private Optional<String> title;
  private Optional<String> content;
  private Optional<Integer> readPermission;
  private Optional<Integer> writePermission;
  private Optional<LocalDateTime> endDate;
}
