package com.hongsamstick.question.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String email; // 이메일(중복 불가)

  private String password; // 비밀번호

  @Column(unique = true)
  private String name; // 이름(중복 불가)

  private String picture;

  @PrePersist
  public void prePersist() {
    if (picture == null) {
      picture = "/profile_image.png"; // 기본 프로필 사진 URL
    }
  }
}
