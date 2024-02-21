package com.hongsamstick.question.controller;

import com.hongsamstick.question.domain.Post;
import com.hongsamstick.question.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

  private final PostService postService;

  public IndexController(PostService postService) {
    this.postService = postService;
  }

  /**
   * 게시판 목록 페이지
   * GET /post
   *
   * @param model
   * @param page 페이지 번호 (default: 0)
   * @param sort 정렬 기준 (default: latest)
   * @return  index.html
   */
  @GetMapping("/")
  public String listPosts(
    Model model,
    @RequestParam(required = false, defaultValue = "latest") String sort, // 정렬 기준
    @RequestParam(defaultValue = "0") int page // 페이지 번호
  ) {
    int size = 6; // 한 페이지에 보여줄 게시글 수

    Pageable pageable = getPageable(sort, page, size);

    Page<Post> posts = postService.getPosts(0, pageable); // 게시판 목록
    model.addAttribute("posts", posts);
    model.addAttribute("sort", sort);
    return "index"; // 뷰의 이름
  }

  // 페이지 정보를 반환
  // sort: 정렬 기준, page: 페이지 번호, size: 한 페이지에 보여줄 게시글 수
  private Pageable getPageable(String sort, int page, int size) {
    Sort sortCondition = getSortCondition(sort);
    return PageRequest.of(page, size, sortCondition);
  }

  // 정렬 기준에 따라 정렬 조건을 반환
  // latest: 최신순, oldest: 오래된순, views: 조회수순
  private Sort getSortCondition(String sort) {
    switch (sort) {
      case "latest":
        return Sort.by(Sort.Direction.DESC, "startDate");
      case "oldest":
        return Sort.by(Sort.Direction.ASC, "startDate");
      case "views":
        return Sort.by(Sort.Direction.DESC, "viewcount");
      default:
        return Sort.by(Sort.Direction.DESC, "startDate");
    }
  }
}
