package com.hongsamstick.question.repository;

import com.hongsamstick.question.domain.Post;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
  // code로 게시글 찾기
  Optional<Post> findByCode(UUID code);

  // postId로 게시글 찾기
  Post findByPostId(Long postId);

  // 게시글 삭제하기
  void deleteByCode(UUID code);
}
