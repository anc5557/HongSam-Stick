package com.hongsamstick.question.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class IndexController {

  @GetMapping("/")
  public String indexPage() {
    return "index";
  }
}
