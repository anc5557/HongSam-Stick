package com.hongsamstick.question.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorPageController {

  // 에러 페이지로 리다이렉트
  @GetMapping("/errorPage")
  public String errorPage() {
    return "errorPage";
  }
}
