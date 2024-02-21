package com.hongsamstick.question.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hongsamstick.question.domain.Member;
import com.hongsamstick.question.domain.Post;
import com.hongsamstick.question.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

  @Mock
  private PostRepository postRepository;

  @InjectMocks
  private PostService postService;

  private Member member;

  @Test
  @DisplayName("게시판 개설하기 테스트 - 성공")
  public void createPostTest() {
    // given
    String title = "게시판 제목";
    String content = "게시판 내용";
    Integer readPermission = 1;
    Integer writePermission = 1;
    LocalDateTime endDate = LocalDateTime.now().plusDays(7);
    Member member = new Member();
    member.setEmail("test@test.com");
    member.setPassword("test12345!");
    member.setName("test");

    Post post = new Post();
    post.setMember(member);
    post.setTitle(title);
    post.setContent(content);
    post.setReadPermission(readPermission);
    post.setWritePermission(writePermission);
    post.setEndDate(endDate);

    when(postRepository.save(any(Post.class)))
      .thenAnswer(invocation -> {
        Post savedPost = (Post) invocation.getArguments()[0];
        savedPost.setCode(UUID.randomUUID());
        return savedPost;
      });

    // when
    UUID returnedCode = postService.createPost(
      title,
      content,
      readPermission,
      writePermission,
      endDate,
      member
    );

    // then
    assertNotNull(returnedCode); // 반환값 확인
    verify(postRepository).save(any(Post.class)); // 호출 확인
  }

  @Test
  @DisplayName("게시판 개설하기 테스트 - 실패 - 멤버 정보가 없는 경우")
  public void createPostTestFail() {
    // given
    String title = "게시판 제목";
    String content = "게시판 내용";
    Integer readPermission = 1;
    Integer writePermission = 1;
    LocalDateTime endDate = LocalDateTime.now().plusDays(7);
    member = null;

    // when
    AccessDeniedException exception = assertThrows(
      AccessDeniedException.class,
      () -> {
        postService.createPost(
          title,
          content,
          readPermission,
          writePermission,
          endDate,
          member
        );
      }
    );

    // then
    assertEquals("로그인이 필요합니다.", exception.getMessage()); // 예외 메시지 확인
  }

  @Test
  @DisplayName("게시판 수정하기 테스트 - 성공")
  public void updatePostTest() {
    // given
    UUID code = UUID.randomUUID();
    Member member = new Member();
    member.setEmail("test@test.com");
    member.setPassword("test12345!");
    member.setName("test");

    Post post = new Post();
    post.setMember(member);
    post.setTitle("Original Title");
    post.setContent("Original Content");
    post.setReadPermission(1);
    post.setWritePermission(1);
    post.setEndDate(LocalDateTime.now().plusDays(7));
    post.setCode(code);

    when(postRepository.findByCode(code)).thenReturn(Optional.of(post));

    String newTitle = "New Title";
    String newContent = "New Content";
    Integer newReadPermission = 0;
    Integer newWritePermission = 0;
    LocalDateTime newEndDate = LocalDateTime.now().plusDays(14);

    // when
    postService.updatePost(
      code,
      newTitle,
      newContent,
      newReadPermission,
      newWritePermission,
      newEndDate,
      member
    );

    // then
    assertEquals(newTitle, post.getTitle());
    assertEquals(newContent, post.getContent());
    assertEquals(newReadPermission, post.getReadPermission());
    assertEquals(newWritePermission, post.getWritePermission());
    assertEquals(newEndDate, post.getEndDate());
  }

  @Test
  @DisplayName("게시판 수정하기 테스트 - 실패 (게시판을 찾을 수 없음)")
  public void updatePostTest_NotFound() {
    // given
    UUID code = UUID.randomUUID();
    Member member = new Member();
    member.setEmail("test@test.com");
    member.setPassword("test12345!");
    member.setName("test");

    when(postRepository.findByCode(code)).thenReturn(Optional.empty());

    String newTitle = "New Title";
    String newContent = "New Content";
    Integer newReadPermission = 1;
    Integer newWritePermission = 1;
    LocalDateTime newEndDate = LocalDateTime.now().plusDays(14);

    // when
    Exception exception = assertThrows(
      EntityNotFoundException.class,
      () -> {
        postService.updatePost(
          code,
          newTitle,
          newContent,
          newReadPermission,
          newWritePermission,
          newEndDate,
          member
        );
      }
    );

    // then
    assertEquals(
      "게시판을 찾을 수 없습니다. code : " + code,
      exception.getMessage()
    );
  }

  @Test
  @DisplayName("게시판 수정하기 테스트 - 실패 (로그인이 필요함)")
  public void updatePostTest_AccessDenied() {
    // given
    UUID code = UUID.randomUUID();
    Member member = null;

    String newTitle = "New Title";
    String newContent = "New Content";
    Integer newReadPermission = 1;
    Integer newWritePermission = 1;
    LocalDateTime newEndDate = LocalDateTime.now().plusDays(14);

    when(postRepository.findByCode(code)).thenReturn(Optional.of(new Post()));

    // when
    AccessDeniedException exception = assertThrows(
      AccessDeniedException.class,
      () -> {
        postService.updatePost(
          code,
          newTitle,
          newContent,
          newReadPermission,
          newWritePermission,
          newEndDate,
          member
        );
      }
    );

    // then
    assertEquals(
      "해당 게시물을 수정할 권한이 없습니다.",
      exception.getMessage()
    );
  }

  @Test
  @DisplayName("게시판 수정하기 테스트 - 실패 (게시판 만든 사람이 아님)")
  public void updatePostTest_NotOwner() {
    // given
    Member owner = new Member();
    owner.setEmail("owner@test.com");
    owner.setPassword("test12345!");
    owner.setName("owner");

    Member notOwner = new Member();
    notOwner.setEmail("notoner@test.com");
    notOwner.setPassword("test12345!");
    notOwner.setName("notowner");

    UUID code = UUID.randomUUID();

    Post post = new Post();
    post.setMember(owner);
    post.setTitle("Original Title");
    post.setContent("Original Content");
    post.setReadPermission(1);
    post.setWritePermission(1);
    post.setEndDate(LocalDateTime.now().plusDays(7));
    post.setCode(code);

    when(postRepository.findByCode(code)).thenReturn(Optional.of(post));

    String newTitle = "New Title";
    String newContent = "New Content";
    Integer newReadPermission = 1;
    Integer newWritePermission = 1;
    LocalDateTime newEndDate = LocalDateTime.now().plusDays(14);

    // when
    AccessDeniedException exception = assertThrows(
      AccessDeniedException.class,
      () -> {
        postService.updatePost(
          code,
          newTitle,
          newContent,
          newReadPermission,
          newWritePermission,
          newEndDate,
          notOwner
        );
      }
    );

    // then
    assertEquals(
      "해당 게시물을 수정할 권한이 없습니다.",
      exception.getMessage()
    );
  }

  @Test
  @DisplayName("게시판 삭제하기 테스트 - 성공")
  public void deletePostTest() {
    // given
    UUID code = UUID.randomUUID();
    Member member = new Member();
    member.setEmail("test@test.com");
    member.setPassword("test12345!");
    member.setName("test");

    Post post = new Post();
    post.setMember(member);
    post.setTitle("Original Title");
    post.setContent("Original Content");
    post.setReadPermission(1);
    post.setWritePermission(1);
    post.setEndDate(LocalDateTime.now().plusDays(7));
    post.setCode(code);

    when(postRepository.findByCode(code)).thenReturn(Optional.of(post));

    // when
    postService.deletePost(code, member);

    // then
    verify(postRepository).delete(post);
  }

  @Test
  @DisplayName("게시판 삭제하기 테스트 - 실패 (게시판을 찾을 수 없음)")
  public void deletePostTest_NotFound() {
    // given
    UUID code = UUID.randomUUID();
    Member member = new Member();
    member.setEmail("test@test.com");
    member.setPassword("test12345!");
    member.setName("test");

    when(postRepository.findByCode(code)).thenReturn(Optional.empty());

    // when
    Exception exception = assertThrows(
      EntityNotFoundException.class,
      () -> {
        postService.deletePost(code, member);
      }
    );

    // then
    assertEquals("게시판을 찾을 수 없습니다." + code, exception.getMessage());
  }

  @Test
  @DisplayName("게시판 삭제하기 테스트 - 실패 (로그인이 필요함)")
  public void deletePostTest_AccessDenied() {
    // given
    UUID code = UUID.randomUUID();
    Member member = null;

    when(postRepository.findByCode(code)).thenReturn(Optional.of(new Post()));

    // when
    AccessDeniedException exception = assertThrows(
      AccessDeniedException.class,
      () -> {
        postService.deletePost(code, member);
      }
    );

    // then
    assertEquals(
      "해당 게시물을 삭제할 권한이 없습니다.",
      exception.getMessage()
    );
  }

  @Test
  @DisplayName("게시판 삭제하기 테스트 - 실패 (게시판 만든 사람이 아님)")
  public void deletePostTest_NotOwner() {
    // given
    Member owner = new Member();
    owner.setEmail("owner@test.com");
    owner.setPassword("test12345!");
    owner.setName("owner");

    Member notOwner = new Member();
    notOwner.setEmail("notoner@test.com");
    notOwner.setPassword("test12345!");
    notOwner.setName("notowner");

    UUID code = UUID.randomUUID();

    Post post = new Post();
    post.setMember(owner);
    post.setTitle("Original Title");
    post.setContent("Original Content");
    post.setReadPermission(1);
    post.setWritePermission(1);
    post.setEndDate(LocalDateTime.now().plusDays(7));
    post.setCode(code);

    when(postRepository.findByCode(code)).thenReturn(Optional.of(post));

    // when
    AccessDeniedException exception = assertThrows(
      AccessDeniedException.class,
      () -> {
        postService.deletePost(code, notOwner);
      }
    );

    // then
    assertEquals(
      "해당 게시물을 삭제할 권한이 없습니다.",
      exception.getMessage()
    );
  }
}
