package com.hongsamstick.question.controller;

import com.hongsamstick.question.config.PrincipalDetails;
import com.hongsamstick.question.domain.Post;
import com.hongsamstick.question.dto.PostDto;
import com.hongsamstick.question.service.PostService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/post")
public class PostController {

  private final PostService postService;

  public PostController(PostService postService) {
    this.postService = postService;
  }

  /**
   * 게시판 개설하기
   * POST /post
   *
   * @param principalDetails
   * @param PostCreateDto
   * @param result
   * @param model
   * @return redirect:/post/{code}
   */
  @PostMapping
  public String createPost(
    @AuthenticationPrincipal PrincipalDetails principalDetails,
    @Valid @ModelAttribute PostDto postDto
  ) {
    UUID code = postService.createPost(
      postDto.getTitle(),
      postDto.getContent(),
      postDto.getReadPermission(),
      postDto.getWritePermission(),
      postDto.getEndDate(),
      principalDetails.getMember()
    );
    return "redirect:/post/" + code.toString();
  }

  /**
   * 게시판 수정하기
   * PUT /post/{code}
   *
   * @param code
   * @param PostEditDto
   * @param result
   * @param model
   * @param principalDetails
   * @return redirect:/post/{code}
   */
  @PutMapping("/{code}")
  public String updatePost(
    @AuthenticationPrincipal PrincipalDetails principalDetails,
    @PathVariable UUID code,
    @Valid @ModelAttribute PostDto postDto
  ) {
    postService.updatePost(
      code,
      postDto.getTitle(),
      postDto.getContent(),
      postDto.getReadPermission(),
      postDto.getWritePermission(),
      postDto.getEndDate(),
      principalDetails.getMember()
    );
    return "redirect:/post/" + code.toString();
  }

  /**
   * 게시판 삭제하기
   *  DELETE /post/{code}
   *
   * @param principalDetails
   * @param code
   * @return redirect:/
   */
  @DeleteMapping("/{code}")
  public String deletePost(
    @AuthenticationPrincipal PrincipalDetails principalDetails,
    @PathVariable UUID code
  ) {
    postService.deletePost(code, principalDetails.getMember());
    return "redirect:/"; // 삭제 성공 후, 메인 페이지로 리다이렉트
  }

  /**
   * 게시판 개설하기 페이지
   * GET /post/create
   *
   * @return  create-post.html
   */
  @GetMapping("/create")
  public String createPostPage(@ModelAttribute("postDto") PostDto postDto) {
    return "create-post";
  }

  /**
   * 게시판 상세 페이지
   * GET /post/{code}
   *
   * @param code
   * @param model
   * @return  post.html
   */
  @GetMapping("/{code}")
  public String postDetail(
    @PathVariable UUID code,
    Model model,
    @AuthenticationPrincipal PrincipalDetails principalDetails
  ) {
    Post post = postService.getPostByCode(code);
    model.addAttribute("post", post);
    model.addAttribute("principalDetails", principalDetails);

    return "post";
  }

  /**
   * 게시판 수정하기 페이지
   * GET /post/{code}/edit
   *
   * @param code
   * @param model
   * @return  edit-post.html
   */
  @GetMapping("/{code}/edit")
  public String editPostPage(@PathVariable UUID code, Model model) {
    Post post = postService.getPostByCode(code);
    PostDto postDto = postService.convertPostToPostDto(post);
    model.addAttribute("postDto", postDto);
    model.addAttribute("code", code);
    return "edit-post";
  }
}
