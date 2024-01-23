package com.hongsamstick.question.service;

import com.hongsamstick.question.config.PrincipalDetails;
import com.hongsamstick.question.domain.Member;
import com.hongsamstick.question.domain.Post;
import com.hongsamstick.question.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

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
  public Post updatePost(
    PrincipalDetails principalDetails,
    UUID code,
    String title,
    String content,
    Integer readPermission,
    Integer writePermission,
    LocalDateTime endDate
  ) {
    Post post = postRepository
      .findByCode(code)
      .orElseThrow(() ->
        new EntityNotFoundException("게시판을 찾을 수 없습니다.")
      );

    // 현재 로그인한 사용자가 게시물의 개설자와 같은지 확인
    if (!post.getMember().getEmail().equals(principalDetails.getUsername())) {
      throw new AccessDeniedException("해당 게시물을 수정할 권한이 없습니다.");
    }
    post.setTitle(title);
    post.setContent(content);
    post.setReadPermission(readPermission);
    post.setWritePermission(writePermission);
    post.setEndDate(endDate);

    return postRepository.save(post);
  }

  // 게시판 삭제하기
  public void deletePost(PrincipalDetails principalDetails, UUID code) {
    Post post = postRepository
      .findByCode(code)
      .orElseThrow(() ->
        new EntityNotFoundException("게시판을 찾을 수 없습니다.")
      );

    // 현재 로그인한 사용자가 게시물의 개설자와 같은지 확인
    if (!post.getMember().getEmail().equals(principalDetails.getUsername())) {
      throw new AccessDeniedException("해당 게시물을 삭제할 권한이 없습니다.");
    }
    postRepository.delete(post);
  }
}
