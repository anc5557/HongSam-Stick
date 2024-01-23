package com.hongsamstick.question.controller;

import com.hongsamstick.question.config.PrincipalDetails;
import com.hongsamstick.question.domain.Post;
import com.hongsamstick.question.dto.PostCreateDto;
import com.hongsamstick.question.dto.PostEditDto;
import com.hongsamstick.question.service.PostService;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
public class PostController {

  private final PostService postService;

  public PostController(PostService postService) {
    this.postService = postService;
  }

  /**
   * 게시판 개설하기
   * @param PostCreateDto
   * @return
   */
  @PostMapping
  public Post createPost(@RequestBody PostCreateDto postCreateDto) {
    return postService.createPost(
      postCreateDto.getMember(),
      postCreateDto.getTitle(),
      postCreateDto.getContent(),
      postCreateDto.getReadPermission(),
      postCreateDto.getWritePermission(),
      postCreateDto.getEndDate()
    );
  }

  /**
   * 게시판 수정하기
   * @param code
   * @param PostEditDto
   * @return
   */
  @PutMapping("/{code}")
  public Post updatePost(
    @AuthenticationPrincipal PrincipalDetails principalDetails,
    @PathVariable UUID code,
    @RequestBody PostEditDto postEditDto
  ) {
    return postService.updatePost(
      principalDetails,
      code,
      postEditDto.getTitle(),
      postEditDto.getContent(),
      postEditDto.getReadPermission(),
      postEditDto.getWritePermission(),
      postEditDto.getEndDate()
    );
  }

  /**
   * 게시판 삭제하기
   * @param code
   * @return
   */
  @DeleteMapping("/{code}")
  public void deletePost(
    @AuthenticationPrincipal PrincipalDetails principalDetails,
    @PathVariable UUID code
  ) {
    postService.deletePost(principalDetails, code);
  }
}
