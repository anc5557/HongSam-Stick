package com.hongsamstick.question.service;

import com.hongsamstick.question.config.PrincipalDetails;
import com.hongsamstick.question.domain.Member;
import com.hongsamstick.question.domain.Post;
import com.hongsamstick.question.repository.PostRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;

@Service
public class PostService {

  private final PostRepository postRepository;

  public PostService(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  // 게시판 개설하기
  public Post createPost(
    Member member,
    String title,
    String content,
    Integer readPermission,
    Integer writePermission,
    LocalDateTime endDate
  ) {
    Post post = new Post();
    post.setMember(member);
    post.setTitle(title);
    post.setContent(content);
    post.setReadPermission(readPermission);
    post.setWritePermission(writePermission);
    post.setEndDate(endDate);
    return postRepository.save(post);
  }

  // 게시판 수정하기
  @PutMapping("/{code}")
  public Post updatePost(
    @AuthenticationPrincipal PrincipalDetails principalDetails,
    UUID code,
    Optional<String> title,
    Optional<String> content,
    Optional<Integer> readPermission,
    Optional<Integer> writePermission,
    Optional<LocalDateTime> endDate
  ) {
    Post post = postRepository
      .findByCode(code)
      .orElseThrow(() ->
        new IllegalArgumentException("존재하지 않는 게시판입니다.")
      );

    // 작성자가 아니면 수정 불가
    if (!post.getMember().getEmail().equals(principalDetails.getUsername())) {
      throw new IllegalArgumentException("작성자가 아닙니다.");
    }

    title.ifPresent(post::setTitle);
    content.ifPresent(post::setContent);
    readPermission.ifPresent(post::setReadPermission);
    writePermission.ifPresent(post::setWritePermission);
    endDate.ifPresent(post::setEndDate);

    return postRepository.save(post);
  }

  // 게시판 삭제하기
  @DeleteMapping("/{code}")
  public void deletePost(
    @AuthenticationPrincipal PrincipalDetails principalDetails,
    UUID code
  ) {
    Post post = postRepository
      .findByCode(code)
      .orElseThrow(() ->
        new IllegalArgumentException("존재하지 않는 게시판입니다.")
      );

    // 작성자가 아니면 삭제 불가
    if (!post.getMember().getEmail().equals(principalDetails.getUsername())) {
      throw new IllegalArgumentException("작성자가 아닙니다.");
    }

    postRepository.delete(post);
  }
}
