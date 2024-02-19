package com.hongsamstick.question.service;

import com.hongsamstick.question.domain.Member;
import com.hongsamstick.question.domain.Post;
import com.hongsamstick.question.dto.PostDto;
import com.hongsamstick.question.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PostService {

  private final PostRepository postRepository;

  public PostService(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  public Post getPostByCode(UUID code) {
    return postRepository
      .findByCode(code)
      .orElseThrow(() ->
        new EntityNotFoundException("게시판을 찾을 수 없습니다." + code)
      );
  }

  public PostDto convertPostToPostDto(Post post) {
    return new PostDto(
      post.getTitle(),
      post.getContent(),
      post.getReadPermission(),
      post.getWritePermission(),
      post.getEndDate()
    );
  }

  // 게시판 개설하기
  @Transactional
  public UUID createPost(
    String title,
    String content,
    Integer readPermission,
    Integer writePermission,
    LocalDateTime endDate,
    Member member
  ) {
    // 예외처리
    if (member == null) {
      throw new AccessDeniedException("로그인이 필요합니다.");
    }

    Post post = new Post();
    post.setMember(member);
    post.setTitle(title);
    post.setContent(content);
    post.setReadPermission(readPermission);
    post.setWritePermission(writePermission);
    post.setEndDate(endDate);
    postRepository.save(post);
    return post.getCode();
  }

  // 게시판 수정하기
  public Post updatePost(
    UUID code,
    String title,
    String content,
    Integer readPermission,
    Integer writePermission,
    LocalDateTime endDate,
    Member member
  ) {
    Post post = postRepository
      .findByCode(code)
      .orElseThrow(() ->
        new EntityNotFoundException("게시판을 찾을 수 없습니다. code : " + code)
      );

    // 현재 로그인한 사용자가 null이거나 게시물의 개설자와 같지 않은지 확인
    if (
      member == null || !post.getMember().getEmail().equals(member.getEmail())
    ) {
      throw new AccessDeniedException("해당 게시물을 수정할 권한이 없습니다.");
    }

    // 수정할 내용이 있으면 수정
    if (title != null) {
      post.setTitle(title);
    }
    if (content != null) {
      post.setContent(content);
    }
    if (readPermission != null) {
      post.setReadPermission(readPermission);
    }
    if (writePermission != null) {
      post.setWritePermission(writePermission);
    }
    if (endDate != null) {
      post.setEndDate(endDate);
    }

    return postRepository.save(post);
  }

  // 게시판 삭제하기
  public void deletePost(UUID code, Member member) {
    Post post = postRepository
      .findByCode(code)
      .orElseThrow(() ->
        new EntityNotFoundException("게시판을 찾을 수 없습니다." + code)
      );

    if (
      member == null || !post.getMember().getEmail().equals(member.getEmail())
    ) {
      throw new AccessDeniedException("해당 게시물을 삭제할 권한이 없습니다.");
    }

    postRepository.delete(post);
  }

  // index 페이지에 게시판 목록 보여주기
  // readPermission이 0이고 endDate가 현재 시간보다 큰 게시글 찾기
  public Page<Post> getPosts(Integer readPermission, Pageable pageable) {
    return postRepository.findByReadPermissionAndEndDateIsNullOrEndDateAfter(
      readPermission,
      LocalDateTime.now(),
      pageable
    );
  }
}
