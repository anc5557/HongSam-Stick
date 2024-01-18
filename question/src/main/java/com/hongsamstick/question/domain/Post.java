package com.hongsamstick.question.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long postId;

  @ManyToOne
  @JoinColumn(name = "email")
  private Member member;

  @Column(length = 255, nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @Column(nullable = false)
  private Long viewcount = 0L;

  @Column(nullable = false)
  private Integer read_permission; // 0 : 전체 공개 or 1 : (링크)코드 공개

  @Column(nullable = false)
  private Integer write_permission; // 0 : 회원만 가능 or 1 : 익명도 가능

  @Column(nullable = false)
  private LocalDateTime startDate;

  private LocalDateTime endDate; // null이면 무기한

  @Column(nullable = false)
  private UUID code;

  @PrePersist
  protected void onCreate() {
    startDate = LocalDateTime.now();
    code = UUID.randomUUID();
  }
}
