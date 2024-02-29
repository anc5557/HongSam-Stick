package com.hongsamstick.question.service;

import com.hongsamstick.question.config.PrincipalDetails;
import com.hongsamstick.question.domain.EmailVerification;
import com.hongsamstick.question.domain.Member;
import com.hongsamstick.question.repository.EmailVerificationRepository;
import com.hongsamstick.question.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.Random;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

  private final JavaMailSender emailSender;
  private final MemberRepository memberRepository;
  private final EmailVerificationRepository emailVerificationRepository;
  private final PasswordEncoder passwordEncoder;

  public MemberService(
    MemberRepository memberRepository,
    PasswordEncoder passwordEncoder,
    JavaMailSender emailSender,
    EmailVerificationRepository emailVerificationRepository
  ) {
    this.memberRepository = memberRepository;
    this.passwordEncoder = passwordEncoder;
    this.emailSender = emailSender;
    this.emailVerificationRepository = emailVerificationRepository;
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

    // 입력값이 null이면 예외 발생
    if (email == null || password == null || name == null) {
      throw new RuntimeException("입력되지 않은 값이 있습니다.");
    }

    // 인증 코드 확인
    // 이메일 인증 정보를 조회합니다.
    // verified가 false인 경우, 인증되지 않은 이메일로 간주합니다.
    EmailVerification emailVerification = emailVerificationRepository
      .findById(email)
      .orElseThrow(() ->
        new IllegalArgumentException("인증되지 않은 이메일입니다.")
      );

    if (!emailVerification.isVerified()) {
      throw new RuntimeException("이메일 인증을 완료해주세요.");
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

  // get 내 정보 페이지
  public Member getMyInfo(
    @AuthenticationPrincipal PrincipalDetails principalDetails
  ) {
    String email = principalDetails.getUsername();
    return memberRepository.findByEmail(email).orElse(null);
  }

  // 이메일로 인증 코드 보내기
  public void sendVerificationCode(String email) {
    // 이메일 중복 검사
    if (emailExists(email)) {
      throw new RuntimeException("이미 가입된 이메일입니다.");
    }

    // 이메일 형식 검사
    if (!email.contains("@")) {
      throw new RuntimeException("이메일 형식이 올바르지 않습니다.");
    }

    // 인증 코드 생성
    String code = generateCode();

    // 이메일 인증 정보 저장
    EmailVerification emailVerification = new EmailVerification();
    emailVerification.setEmail(email);
    emailVerification.setCode(code);
    emailVerificationRepository.save(emailVerification);

    // 이메일 전송
    sendEmail(email, code);
  }

  // 코드 생성 메소드
  public String generateCode() {
    // 랜덤한 6자리 코드 생성
    Random random = new Random();
    StringBuilder codeBuilder = new StringBuilder();
    for (int i = 0; i < 6; i++) {
      int digit = random.nextInt(10);
      codeBuilder.append(digit);
    }
    String code = codeBuilder.toString();

    return code;
  }

  // 이메일 보내는 메소드
  public void sendEmail(String email, String code) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("hongsamstick1@gmail.com");
    message.setTo(email);
    message.setSubject("회원가입 인증 코드");
    message.setText("회원가입 인증 코드: " + code);
    emailSender.send(message);
  }

  // 이메일로 인증 코드 재전송
  public void resendVerificationCode(String email) {
    // 이메일 인증 정보를 조회합니다.
    EmailVerification emailVerification = emailVerificationRepository
      .findById(email)
      .orElseThrow(() -> new IllegalArgumentException("전송된 코드가 없습니다.")
      );

    // 인증 코드를 재생성합니다.
    String code = generateCode();
    emailVerification.setCode(code);

    // 이메일 인증 정보를 업데이트합니다.
    emailVerificationRepository.save(emailVerification);

    // 이메일을 재전송합니다.
    sendEmail(email, code);
  }

  // 이메일로 인증 코드 확인
  public boolean verifyCode(String email, String code) {
    // 이메일 인증 정보를 조회합니다.
    EmailVerification emailVerification = emailVerificationRepository
      .findById(email)
      .orElseThrow(() -> new IllegalArgumentException("전송된 코드가 없습니다.")
      );

    // 만료 시간을 확인합니다.
    LocalDateTime now = LocalDateTime.now();
    if (emailVerification.getExpiredAt().isBefore(now)) {
      throw new IllegalArgumentException(
        "인증 코드가 만료되었습니다. 새로운 인증 코드를 요청해주세요."
      );
    }

    // 입력한 코드와 저장된 코드가 일치하면 true, 아니면 false 반환
    boolean isVerified = emailVerification.getCode().equals(code);

    // 인증 성공한 경우 verified 필드를 true로 변경
    if (isVerified) {
      emailVerification.setVerified(true);
      emailVerificationRepository.save(emailVerification);
    }

    return isVerified;
  }

  // 비밀번호 변경
  @Transactional
  public void changePassword(
    Member member,
    String oldPassword,
    String newPassword,
    String newPasswordConfirm
  ) {
    // 입력값이 null이면 예외 발생
    if (
      oldPassword == null || newPassword == null || newPasswordConfirm == null
    ) {
      throw new RuntimeException("입력되지 않은 값이 있습니다.");
    }

    // 기존 비밀번호가 일치하는지 확인
    if (!passwordEncoder.matches(oldPassword, member.getPassword())) {
      throw new RuntimeException("기존 비밀번호를 확인해주세요.");
    }

    // 새로운 비밀번호가 일치하는지 확인
    if (!newPassword.equals(newPasswordConfirm)) {
      throw new RuntimeException("새로운 비밀번호가 일치하지 않습니다.");
    }

    // 새로운 비밀번호 유효성 검사
    if (!isValidPassword(newPassword)) {
      throw new RuntimeException(
        "비밀번호는 영문, 숫자, 특수문자를 포함한 8~16자리여야 합니다."
      );
    }

    // 새로운 비밀번호로 변경
    member.setPassword(passwordEncoder.encode(newPassword));

    // 변경된 회원 정보 저장
    memberRepository.save(member);

    // 세션 무효화
    SecurityContextHolder.getContext().setAuthentication(null);
  }

  // 회원 탈퇴
  @Transactional
  public void unregister(Member member, String password) {
    // 입력값이 null이면 예외 발생
    if (password == null) {
      throw new RuntimeException("입력되지 않은 값이 있습니다.");
    }
    // member가 null이면 예외 발생
    if (member == null) {
      throw new RuntimeException("회원 정보가 없습니다.");
    }

    // 비밀번호가 일치하는지 확인
    if (!passwordEncoder.matches(password, member.getPassword())) {
      throw new RuntimeException("비밀번호가 일치하지 않습니다.");
    }

    // 회원 탈퇴
    memberRepository.delete(member);

    // 세션 무효화
    SecurityContextHolder.getContext().setAuthentication(null);
  }
}
