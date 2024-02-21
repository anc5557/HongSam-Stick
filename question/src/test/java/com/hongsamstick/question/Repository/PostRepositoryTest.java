package com.hongsamstick.question.Repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hongsamstick.question.domain.Post;
import com.hongsamstick.question.repository.PostRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
public class PostRepositoryTest {

  @Autowired
  private PostRepository postRepository;

  @Test
  @DisplayName("readPermission이 0이고 endDate가 현재 시간보다 큰 게시글 찾기")
  void findByReadPermissionAndEndDateIsNullOrEndDateAfter_WhenValidReadPermissionAndEndDate_ThenReturnPosts() {
    // 준비
    LocalDateTime currentDate = LocalDateTime.now();
    Post post1 = new Post();
    post1.setTitle("title1");
    post1.setContent("content1");
    post1.setReadPermission(0);
    post1.setWritePermission(1);
    post1.setEndDate(currentDate.plusDays(1));

    postRepository.save(post1);

    Post post2 = new Post();
    post2.setTitle("title2");
    post2.setContent("content2");
    post2.setReadPermission(1);
    post2.setWritePermission(1);
    post2.setEndDate(currentDate.minusDays(1));

    postRepository.save(post2);

    Post post3 = new Post();
    post3.setTitle("title3");
    post3.setContent("content3");
    post3.setReadPermission(0);
    post3.setWritePermission(1);
    post3.setEndDate(null);

    postRepository.save(post3);

    Pageable pageable = PageRequest.of(0, 10);

    // 실행
    Page<Post> foundPosts = postRepository.findByReadPermissionAndEndDateIsNullOrEndDateAfter(
      0,
      currentDate,
      pageable
    );

    // 단언
    assertEquals(2, foundPosts.getTotalElements());
    assertEquals(post1, foundPosts.getContent().get(0));
  }
}
