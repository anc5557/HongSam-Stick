package com.hongsamstick.question.service;

import com.hongsamstick.question.config.PrincipalDetails;
import com.hongsamstick.question.domain.Member;
import com.hongsamstick.question.repository.MemberRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  public MemberService(
    MemberRepository memberRepository,
    PasswordEncoder passwordEncoder
  ) {
    this.memberRepository = memberRepository;
    this.passwordEncoder = passwordEncoder;
  }

  // 회원가입
  @Transactional
  public Member register(String email, String password, String name) {
    // 이메일 중복 검사
    if (memberRepository.existsByEmail(email)) {
      throw new RuntimeException("이미 가입된 이메일입니다.");
    }

    // 이메일 형식 검사
    if (!email.contains("@")) {
      throw new RuntimeException("이메일 형식이 올바르지 않습니다.");
    }

    // 이름 중복 검사
    if (memberRepository.existsByName(name)) {
      throw new RuntimeException("이미 존재하는 이름입니다.");
    }

    // 비밀번호 유효성 검사
    if (!isValidPassword(password)) {
      throw new RuntimeException(
        "비밀번호는 영문, 숫자, 특수문자를 포함한 8~16자리여야 합니다."
      );
    }

    if (email == null || password == null || name == null) {
      throw new RuntimeException("입력되지 않은 값이 있습니다.");
    }

    Member newMember = new Member();
    newMember.setEmail(email);
    newMember.setPassword(passwordEncoder.encode(password));
    newMember.setName(name);

    return memberRepository.save(newMember);
  }

  // 비밀번호 유효성 검사
  private boolean isValidPassword(String password) {
    String regex =
      "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,16}$";
    return password != null && password.matches(regex);
  }

  // 이메일 확인
  public boolean emailExists(String email) {
    return memberRepository.existsByEmail(email);
  }

  // 이름 중복 확인
  public boolean nameExists(String name) {
    return memberRepository.existsByName(name);
  }

  // 회원 탈퇴
  @Transactional
  public boolean unregister(
    @AuthenticationPrincipal PrincipalDetails principalDetails,
    String inputPassword
  ) {
    String email = principalDetails.getUsername(); // 현재 인증된 사용자의 이메일

    Member member = memberRepository.findByEmail(email).orElse(null); // 현재 인증된 사용자

    // 현재 인증된 사용자가 없거나, 입력한 비밀번호가 일치하지 않으면 false 반환
    if (
      member == null ||
      !passwordEncoder.matches(inputPassword, member.getPassword())
    ) {
      return false;
    }

    memberRepository.deleteByEmail(email);
    return true;
  }

  // get 내 정보 페이지
  public Member getMyInfo(
    @AuthenticationPrincipal PrincipalDetails principalDetails
  ) {
    String email = principalDetails.getUsername();
    return memberRepository.findByEmail(email).orElse(null);
  }
}
