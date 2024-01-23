package com.hongsamstick.question.controller;

import com.hongsamstick.question.config.PrincipalDetails;
import com.hongsamstick.question.domain.Post;
import com.hongsamstick.question.dto.PostCreateDto;
import com.hongsamstick.question.dto.PostEditDto;
import com.hongsamstick.question.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
   * POST /post
   *
   * @param PostCreateDto
   * @param result
   * @param model
   * @return redirect:/post/{code}
   */
  @PostMapping
  public String createPost(
    @Valid @RequestBody PostCreateDto postCreateDto,
    BindingResult result,
    Model model
  ) {
    if (result.hasErrors()) {
      model.addAttribute("errors", result.getAllErrors());
      model.addAttribute("postCreateDto", postCreateDto);
      return "createPost";
    }
    Post newPost = postService.createPost(
      postCreateDto.getMember(),
      postCreateDto.getTitle(),
      postCreateDto.getContent(),
      postCreateDto.getReadPermission(),
      postCreateDto.getWritePermission(),
      postCreateDto.getEndDate()
    );
    return "redirect:/post/" + newPost.getCode().toString();
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
    @Valid @RequestBody PostEditDto postEditDto,
    BindingResult result,
    Model model
  ) {
    if (result.hasErrors()) {
      model.addAttribute("errors", result.getAllErrors());
      model.addAttribute("postEditDto", postEditDto);
      return "editPost"; // 에러 메시지와 함께 수정 폼 페이지로 리다이렉트
    }

    try {
      postService.updatePost(
        principalDetails,
        code,
        postEditDto.getTitle(),
        postEditDto.getContent(),
        postEditDto.getReadPermission(),
        postEditDto.getWritePermission(),
        postEditDto.getEndDate()
      );
      return "redirect:/post/" + code.toString(); // 수정 성공 후, 해당 게시물 상세 페이지로 리다이렉트
    } catch (EntityNotFoundException e) {
      // 게시물을 찾을 수 없는 경우의 처리
      model.addAttribute("errorMessage", "게시물을 찾을 수 없습니다.");
      return "errorPage"; // 오류 페이지로 리다이렉트
    }
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
    postService.deletePost(principalDetails, code);
    return "redirect:/"; // 삭제 성공 후, 메인 페이지로 리다이렉트
  }

  /**
   * 게시판 개설하기 페이지
   * GET /post/create
   *
   * @return  create-post.html
   */
  @GetMapping("/create")
  public String createPostPage(
    @ModelAttribute("postCreateDto") PostCreateDto postCreateDto
  ) {
    return "create-post";
  }
}
