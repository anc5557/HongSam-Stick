package com.hongsamstick.question.repository;

import com.hongsamstick.question.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
  Optional<Member> findByEmail(String email);

  Optional<Member> findByName(String name);

  boolean existsByEmail(String email); // 이메일 중복 검사

  boolean existsByName(String name); // 이름 중복 검사

  void deleteByEmail(String email); // 이메일로 회원 삭제
}
