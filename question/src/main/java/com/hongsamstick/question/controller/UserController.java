package com.hongsamstick.question.controller;

import com.hongsamstick.question.dto.RegisterDto;
import com.hongsamstick.question.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  // 회원가입
  @PostMapping("/register")
  public String register(
    @ModelAttribute RegisterDto registerDto, // 입력값
    BindingResult bindingResult, // 입력값 검증
    RedirectAttributes redirectAttributes // 리다이렉트 시 값을 전달
  ) {
    try {
      userService.register(
        registerDto.getEmail(),
        registerDto.getPassword(),
        registerDto.getName()
      );
      return "redirect:/home"; // 일단 home으로 리다이렉트 해놓음
    } catch (RuntimeException e) {
      redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
      redirectAttributes.addFlashAttribute("registerDto", registerDto);
      return "redirect:/register";
    }
  }

  // 회원가입 페이지
  @GetMapping("/register")
  public String registerPage(
    @ModelAttribute("registerDto") RegisterDto registerDto
  ) {
    return "register";
  }

  // 이메일 확인
  @GetMapping("/check-email")
  @ResponseBody
  public boolean checkEmail(@RequestParam String email) {
    return userService.emailExists(email);
  }

  // 이름 중복 확인
  @GetMapping("/check-name")
  @ResponseBody
  public boolean checkName(@RequestParam String name) {
    return userService.nameExists(name);
  }

  // 로그인 페이지
  @GetMapping("/login")
  public String loginPage(
    @RequestParam(value = "error", required = false) String error,
    @RequestParam(value = "exception", required = false) String exception,
    Model model
  ) {
    model.addAttribute("error", error);
    model.addAttribute("exception", exception);
    return "login";
  }

  //홈
  @GetMapping("/home")
  public String homePage() {
    return "home";
  }
}
