package com.hongsamstick.question.controller;

import com.hongsamstick.question.dto.SignUpDto;
import com.hongsamstick.question.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/member")
public class MemberController {

  private final MemberService memberService;

  public MemberController(MemberService memberService) {
    this.memberService = memberService;
  }

  // 회원가입
  @PostMapping("/signup")
  public String register(
    @ModelAttribute SignUpDto signupDto, // 입력값
    BindingResult bindingResult, // 입력값 검증
    RedirectAttributes redirectAttributes // 리다이렉트 시 값을 전달
  ) {
    try {
      memberService.register(
        signupDto.getEmail(),
        signupDto.getPassword(),
        signupDto.getName()
      );
      return "redirect:/"; // 회원가입 성공 시 메인 페이지로 이동
    } catch (RuntimeException e) {
      redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
      redirectAttributes.addFlashAttribute("signupDto", signupDto);
      return "redirect:/member/signup"; // 회원가입 실패 시 회원가입 페이지로 이동
    }
  }

  // 회원가입 페이지
  @GetMapping("/signup")
  public String registerPage(@ModelAttribute("signupDto") SignUpDto signupDto) {
    return "signup";
  }

  // 이메일 확인
  @GetMapping("/check-email")
  @ResponseBody
  public boolean checkEmail(@RequestParam String email) {
    return memberService.emailExists(email);
  }

  // 이름 중복 확인
  @GetMapping("/check-name")
  @ResponseBody
  public boolean checkName(@RequestParam String name) {
    return memberService.nameExists(name);
  }

  // 로그인 페이지
  @GetMapping("/signin")
  public String loginPage(
    @RequestParam(value = "error", required = false) String error,
    @RequestParam(value = "exception", required = false) String exception,
    Model model
  ) {
    model.addAttribute("error", error);
    model.addAttribute("exception", exception);
    return "signin";
  }
}
