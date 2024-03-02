package com.hongsamstick.question.repository;

import com.hongsamstick.question.domain.Post;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {
  // code로 게시글 찾기
  Optional<Post> findByCode(UUID code);

  // postId로 게시글 찾기
  Post findByPostId(Long postId);

  // 게시글 삭제하기
  void deleteByCode(UUID code);

  // readPermission이 0이고 endDate가 현재 시간보다 큰 게시글 찾기
  Page<Post> findByReadPermissionAndEndDateIsNullOrEndDateAfter(
    Integer readPermission,
    LocalDateTime currentDate,
    Pageable pageable
  );

  // 내가 쓴 게시글 찾기 (페이지네이션 적용)
  @Query(
    "SELECT p FROM Post p WHERE p.member.email = :email AND (p.endDate > CURRENT_TIMESTAMP OR p.endDate IS NULL OR :excludeEnded = false)"
  )
  Page<Post> findByMemberEmailAndExcludeEnded(
    String email,
    boolean excludeEnded,
    Pageable pageable
  );

  // 코드로 게시글 찾기(마감 기한이 지나지 않은 게시글)
  Optional<Post> findByCodeAndEndDateAfter(UUID code, LocalDateTime currentDate);
}
