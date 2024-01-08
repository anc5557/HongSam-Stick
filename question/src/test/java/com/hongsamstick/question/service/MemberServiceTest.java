package com.hongsamstick.question.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MemberServiceTest {

  @Autowired
  private MemberService memberService;

  @Test
  public void testEmailExists() {
    String email = "anc555@naver.com";
    boolean result = memberService.emailExists(email);
    assertTrue(result);

    email = "a55@naver.com";
    result = memberService.emailExists(email);
    assertFalse(result);
  }
}
