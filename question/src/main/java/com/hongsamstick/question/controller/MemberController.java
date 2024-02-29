package com.hongsamstick.question.controller;

import com.hongsamstick.question.config.PrincipalDetails;
import com.hongsamstick.question.domain.Member;
import com.hongsamstick.question.dto.PasswordUpdateDto;
import com.hongsamstick.question.dto.SignUpDto;
import com.hongsamstick.question.dto.UnregisterRequestDto;
import com.hongsamstick.question.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

  /**
   * 새로운 회원을 등록합니다.
   *  - POST /member/signup
   *  - 회원가입 성공 시 메인 페이지로 이동합니다.
   *  - 회원가입 실패 시 회원가입 페이지로 이동합니다.
   *
   * @param signupDto            회원 정보를 담고 있는 SignUpDto 객체
   * @param bindingResult        입력 유효성 검사를 위한 BindingResult 객체
   * @param redirectAttributes   리다이렉션 중 값을 전달하기 위한 RedirectAttributes 객체
   * @return                     리다이렉션 URL을 나타내는 문자열
   */
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
      return "redirect:/member/signin"; // 회원가입 성공 시 로그인 페이지로 이동
    } catch (RuntimeException e) {
      redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
      redirectAttributes.addFlashAttribute("signupDto", signupDto);
      return "redirect:/member/signup"; // 회원가입 실패 시 회원가입 페이지로 이동
    }
  }

  /**
   * 회원가입 페이지 렌더링
   *  - GET /member/signup
   *
   * @param signupDto 회원가입 DTO 객체
   * @return 회원가입 페이지의 이름
   */
  @GetMapping("/signup")
  public String registerPage(@ModelAttribute("signupDto") SignUpDto signupDto) {
    return "signup";
  }

  /**
   * 로그인 페이지 렌더링
   *  - GET /member/signin
   *
   * @param error      오류 메시지 (있는 경우)
   * @param exception  예외 메시지 (있는 경우)
   * @param model      뷰를 위한 모델 객체
   * @return 렌더링할 뷰의 이름
   */
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

  /**
   * 내 정보 페이지 렌더링
   * - GET /member/myinfo
   * - 내 정보 페이지로 이동합니다.
   */
  @GetMapping("/myinfo")
  public String myInfo(
    @AuthenticationPrincipal PrincipalDetails principalDetails,
    Model model
  ) {
    Member member = memberService.getMyInfo(principalDetails);
    model.addAttribute("email", member.getEmail());
    model.addAttribute("name", member.getName());
    model.addAttribute("picture", member.getPicture());
    return "myinfo";
  }

  /**
   * 비밀번호 변경 페이지 렌더링
   * - GET /member/password
   * - 비밀번호 변경 페이지로 이동합니다.
   * @return 비밀번호 변경 페이지의 이름
   */
  @GetMapping("/password")
  public String password(Model model) {
    model.addAttribute("passwordUpdateDto", new PasswordUpdateDto());
    return "passwordUpdate";
  }

  /**
   * 회원 탈퇴 페이지 렌더링
   * - GET /member/unregister
   * - 회원 탈퇴 페이지로 이동합니다.
   * @return 회원 탈퇴 페이지의 이름
   */
  @GetMapping("/unregister")
  public String unregister(Model model) {
    model.addAttribute("unregisterRequestDto", new UnregisterRequestDto());
    return "unregister";
  }

  /**
   * 비밀번호 변경
   * - POST /member/password
   * - 비밀번호 변경 성공 시 홈페이지로 이동합니다. (로그아웃)
   * - 비밀번호 변경 실패 시 비밀번호 변경 페이지로 이동합니다.
   * @param passwordUpdateDto     비밀번호 변경 DTO 객체
   * @param bindingResult         입력 유효성 검사를 위한 BindingResult 객체
   * @param redirectAttributes    리다이렉션 중 값을 전달하기 위한 RedirectAttributes 객체
   * @return                      리다이렉션 URL을 나타내는 문자열
   */
  @PostMapping("/password")
  public String passwordUpdate(
    @Valid @ModelAttribute(
      "passwordUpdateDto"
    ) PasswordUpdateDto passwordUpdateDto,
    BindingResult bindingResult,
    RedirectAttributes redirectAttributes,
    @AuthenticationPrincipal PrincipalDetails principalDetails
  ) {
    if (bindingResult.hasErrors()) {
      return "passwordUpdate"; // 유효성 검증 오류가 있는 경우, 다시 비밀번호 변경 페이지로
    }

    try {
      Member member = principalDetails.getMember();
      memberService.changePassword(
        member,
        passwordUpdateDto.getCurrentPassword(),
        passwordUpdateDto.getNewPassword(),
        passwordUpdateDto.getNewPasswordConfirm()
      );
      return "redirect:/"; // 비밀번호 변경 성공 시 홈페이지로 리다이렉트
    } catch (RuntimeException e) {
      redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
      redirectAttributes.addFlashAttribute(
        "passwordUpdateDto",
        passwordUpdateDto
      );
      return "redirect:/member/password"; // 비밀번호 변경 실패 시, 에러 메시지와 함께 리다이렉트
    }
  }

  /**
   * 회원 탈퇴
   * - POST /member/unregister
   * - 회원 탈퇴 성공 시 로그아웃 후 메인 페이지로 이동합니다.
   * - 회원 탈퇴 실패 시 회원 탈퇴 페이지로 이동합니다.
   * @param UnregisterRequestDto 회원 탈퇴 DTO 객체
   * @param bindingResult         입력 유효성 검사를 위한 BindingResult 객체
   * @param redirectAttributes    리다이렉션 중 값을 전달하기 위한 RedirectAttributes 객체
   * @return                      리다이렉션 URL을 나타내는 문자열
   */
  @PostMapping("/unregister")
  public String unregister(
    @ModelAttribute(
      "unregisterRequestDto"
    ) UnregisterRequestDto unregisterRequestDto,
    BindingResult bindingResult,
    RedirectAttributes redirectAttributes,
    @AuthenticationPrincipal PrincipalDetails principalDetails
  ) {
    if (bindingResult.hasErrors()) {
      return "unregister"; // 유효성 검증 오류가 있는 경우, 다시 회원 탈퇴 페이지로
    }

    try {
      Member member = principalDetails.getMember();
      memberService.unregister(member, unregisterRequestDto.getPassword());
      return "redirect:/"; // 회원 탈퇴 성공 시 홈페이지로 리다이렉트
    } catch (RuntimeException e) {
      redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
      redirectAttributes.addFlashAttribute(
        "unregisterRequestDto",
        unregisterRequestDto
      );
      return "redirect:/member/unregister"; // 회원 탈퇴 실패 시, 에러 메시지와 함께 리다이렉트
    }
  }
}
