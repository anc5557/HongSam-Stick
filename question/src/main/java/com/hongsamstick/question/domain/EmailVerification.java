package com.hongsamstick.question.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class EmailVerification {

  @Id
  @Column(unique = true, nullable = false)
  private String email; // 이메일로 기본 키 설정

  @Column(nullable = false)
  private String code; // 인증 코드

  @Column(nullable = false)
  private LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(3); // 만료 시간

  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now(); // 생성 시간

  @Column(nullable = false)
  private boolean verified = false; // 인증 여부 (true: 인증됨, false: 미인증)
}
