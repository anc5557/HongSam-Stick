package com.hongsamstick.question.repository;

import com.hongsamstick.question.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email); 

  Optional<User> findByName(String name); 

  boolean existsByEmail(String email); // 이메일 중복 검사

  boolean existsByName(String name); // 이름 중복 검사
}
