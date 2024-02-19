package com.hongsamstick.question.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hongsamstick.question.config.PrincipalDetails;
import com.hongsamstick.question.domain.Member;
import com.hongsamstick.question.domain.Post;
import com.hongsamstick.question.dto.PostDto;
import com.hongsamstick.question.service.PostService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Description;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PostController.class)
public class PostControllerTest {

  @MockBean // MockBean을 사용하여 PostService를 목 객체로 만듭니다.
  private PostService postService;

  @Autowired // MockMvc를 주입합니다.
  private MockMvc mockMvc;

  private Validator validator;

  private static final Logger logger = LoggerFactory.getLogger(
    PostControllerTest.class
  );

  @BeforeEach
  public void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  public void validatePostDto() {
    PostDto postDto = new PostDto();

    // 유효한 PostDto
    postDto.setTitle("Valid Title");
    postDto.setContent("Valid Content");
    postDto.setReadPermission(0);
    postDto.setWritePermission(1);
    postDto.setEndDate(LocalDateTime.now().plusDays(1));

    assertTrue(validator.validate(postDto).isEmpty());

    // 유효하지 않은 제목
    postDto.setTitle("");
    assertFalse(validator.validate(postDto).isEmpty());
    postDto.setTitle("Valid Title");

    // 유효하지 않은 내용
    postDto.setContent(null);
    assertFalse(validator.validate(postDto).isEmpty());
    postDto.setContent("Valid Content");

    // 유효하지 않은 읽기 권한
    postDto.setReadPermission(null);
    assertFalse(validator.validate(postDto).isEmpty());
    postDto.setReadPermission(0);

    // 유효하지 않은 쓰기 권한
    postDto.setWritePermission(null);
    assertFalse(validator.validate(postDto).isEmpty());
    postDto.setWritePermission(1);

    // 유효하지 않은 종료 날짜
    postDto.setEndDate(LocalDateTime.now().minusDays(1));
    assertFalse(validator.validate(postDto).isEmpty());
  }

  @Test
  @Description("게시판 개설 페이지 접근하기 - 성공")
  @WithMockUser
  public void createPostPage_success() throws Exception {
    // when
    // 게시판 개설 페이지에 접근합니다.
    mockMvc
      .perform(get("/post/create").with(csrf()))
      .andExpect(status().isOk());
  }

  @Test
  @Description("게시판 개설하기 - 성공")
  @WithMockUser(username = "test@test.com")
  public void createPost_success() throws Exception {
    // given
    // 게시판 개설에 필요한 데이터를 생성합니다.

    // 모의 postDto(게시판 제목, 게시판 내용, 읽기 권한, 쓰기 권한, 게시판 종료일자) 객체 생성
    PostDto postDto = new PostDto();
    postDto.setTitle("게시판 제목");
    postDto.setContent("게시판 내용");
    postDto.setReadPermission(0);
    postDto.setWritePermission(1);
    postDto.setEndDate(LocalDate.now().plusDays(3).atStartOfDay());

    // 서비스 메소드를 호출할 때 반환할 모의 UUID를 생성
    UUID mockCode = UUID.randomUUID();
    when(
      postService.createPost(
        anyString(), // 게시판 제목
        anyString(), // 게시판 내용
        anyInt(), // 읽기 권한
        anyInt(), // 쓰기 권한
        any(), // 게시판 종료일자
        any() // PrincipalDetails.getMember()
      )
    )
      .thenReturn(mockCode);

    // 요청 전 로그 출력
    logger.info("게시판 개설 요청 시작: " + postDto);

    // when
    // 게시판 개설을 요청합니다.
    mockMvc
      .perform(
        post("/post")
          .with(csrf())
          .with(user(new PrincipalDetails(new Member())))
          .flashAttr("postDto", postDto)
      )
      .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrl("/post/" + mockCode.toString()));

    // 요청 후 로그 출력
    logger.info("게시판 개설 요청 완료: 게시판 ID = {}", mockCode);
  }

  @Test
  @Description("게시판 개설하기 - 실패(인증되지 않은 사용자)")
  @WithAnonymousUser
  public void createPost_fail_unauthenticated() throws Exception {
    // given
    // 게시판 개설에 필요한 데이터를 생성합니다.

    // 모의 postDto(게시판 제목, 게시판 내용, 읽기 권한, 쓰기 권한, 게시판 종료일자) 객체 생성
    PostDto postDto = new PostDto();
    postDto.setTitle("게시판 제목");
    postDto.setContent("게시판 내용");
    postDto.setReadPermission(0);
    postDto.setWritePermission(1);
    postDto.setEndDate(LocalDate.now().plusDays(3).atStartOfDay());

    // when
    // 게시판 개설을 요청합니다.
    mockMvc
      .perform(post("/post").with(csrf()).flashAttr("postDto", postDto))
      .andExpect(status().is4xxClientError());
  }

  @Test
  @Description("게시판 수정하기 - 성공")
  @WithMockUser(username = "user@test.com")
  public void updatePost_success_withPut() throws Exception {
    // given
    UUID code = UUID.randomUUID();
    PostDto postDto = new PostDto();
    postDto.setTitle("수정된 게시판 제목");
    postDto.setContent("수정된 게시판 내용");
    postDto.setReadPermission(1);
    postDto.setWritePermission(0);
    postDto.setEndDate(LocalDate.now().plusDays(5).atStartOfDay());

    // mocking behavior
    when(
      postService.updatePost(
        any(UUID.class),
        anyString(),
        anyString(),
        anyInt(),
        anyInt(),
        any(),
        any()
      )
    )
      .thenReturn(new Post());

    // when & then
    mockMvc
      .perform(
        put("/post/" + code.toString())
          .with(csrf())
          .contentType("application/x-www-form-urlencoded")
          .with(user(new PrincipalDetails(new Member())))
          .flashAttr("postDto", postDto)
      )
      .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrl("/post/" + code.toString()));
  }

  @Test
  @Description("게시판 삭제하기 - 성공")
  @WithMockUser(username = "user@test.com")
  public void deletePost_success() throws Exception {
    // given
    UUID code = UUID.randomUUID();

    // mocking behavior
    doNothing().when(postService).deletePost(any(UUID.class), any());

    // when & then
    mockMvc
      .perform(
        delete("/post/" + code.toString())
          .with(csrf())
          .with(user(new PrincipalDetails(new Member())))
      )
      .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrl("/"));
  }
}
