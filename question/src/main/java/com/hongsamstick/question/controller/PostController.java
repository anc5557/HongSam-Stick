package com.hongsamstick.question.controller;

import com.hongsamstick.question.config.PrincipalDetails;
import com.hongsamstick.question.domain.Post;
import com.hongsamstick.question.dto.PostCreateDto;
import com.hongsamstick.question.dto.PostEditDto;
import com.hongsamstick.question.repository.PostRepository;
import com.hongsamstick.question.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/post")
public class PostController {

  private final PostService postService;
  private final PostRepository postRepository;

  public PostController(
    PostService postService,
    PostRepository postRepository
  ) {
    this.postService = postService;
    this.postRepository = postRepository;
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
    @Valid @ModelAttribute PostCreateDto postCreateDto,
    BindingResult result,
    Model model,
    RedirectAttributes redirectAttributes
  ) {
    if (result.hasErrors()) {
      redirectAttributes.addFlashAttribute("errors", result.getAllErrors());
      redirectAttributes.addFlashAttribute("postCreateDto", postCreateDto);
      return "redirect:/post/create";
    }

    if (principalDetails == null) {
      redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
      return "redirect:/post/create";
    }

    UUID code = postService.createPost(
      postCreateDto.getTitle(),
      postCreateDto.getContent(),
      postCreateDto.getReadPermission(),
      postCreateDto.getWritePermission(),
      postCreateDto.getEndDate(),
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
    @ModelAttribute PostEditDto postEditDto,
    BindingResult result,
    Model model
  ) {
    if (result.hasErrors()) {
      model.addAttribute("errors", result.getAllErrors());
      model.addAttribute("postEditDto", postEditDto);
      return "edit-post"; // 에러 메시지와 함께 수정 폼 페이지로 리다이렉트
    }

    if (principalDetails == null) {
      model.addAttribute("errorMessage", "로그인이 필요합니다.");
      return "errorPage"; // 오류 페이지로 리다이렉트
    }

    try {
      postService.updatePost(
        code,
        postEditDto.getTitle(),
        postEditDto.getContent(),
        postEditDto.getReadPermission(),
        postEditDto.getWritePermission(),
        postEditDto.getEndDate(),
        principalDetails.getMember()
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
    @PathVariable UUID code,
    RedirectAttributes redirectAttributes
  ) {
    if (principalDetails == null) {
      redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
      return "redirect:/post/" + code;
    }
    try {
      postService.deletePost(code, principalDetails.getMember());
    } catch (AccessDeniedException ex) {
      redirectAttributes.addFlashAttribute("error", ex.getMessage());
      return "redirect:/post/" + code;
    }
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
    Post post = postRepository
      .findByCode(code)
      .orElseThrow(() ->
        new EntityNotFoundException("Invalid post UUID:" + code)
      );
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
    Post post = postRepository
      .findByCode(code)
      .orElseThrow(() ->
        new EntityNotFoundException("Invalid post UUID:" + code)
      );

    // Post 객체의 데이터를 PostEditDto 객체로 복사
    PostEditDto postEditDto = new PostEditDto();
    postEditDto.setTitle(post.getTitle());
    postEditDto.setContent(post.getContent());
    postEditDto.setReadPermission(post.getReadPermission());
    postEditDto.setWritePermission(post.getWritePermission());
    postEditDto.setEndDate(post.getEndDate());

    // 모델에 PostEditDto 객체 추가
    model.addAttribute("postEditDto", postEditDto);

    // 모델에 code 추가
    model.addAttribute("code", code);

    return "edit-post";
  }
}
