package com.hongsamstick.question.service;

import com.hongsamstick.question.domain.User;
import com.hongsamstick.question.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(
    UserRepository userRepository,
    PasswordEncoder passwordEncoder
  ) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  // 회원가입
  @Transactional
  public User register(String email, String password, String name) {
    // 이메일 중복 검사
    if (emailExists(email)) {
      throw new RuntimeException("이미 가입된 이메일입니다.");
    }

    // 이름 중복 검사
    if (nameExists(name)) {
      throw new RuntimeException("이미 가입된 이름입니다.");
    }

    // 비밀번호 유효성 검사
    if (!isValidPassword(password)) {
      throw new RuntimeException(
        "비밀번호는 영문, 숫자, 특수문자를 포함한 8~16자리여야 합니다."
      );
    }

    User newUser = new User();
    newUser.setEmail(email);
    newUser.setPassword(passwordEncoder.encode(password));
    newUser.setName(name);

    return userRepository.save(newUser);
  }

  // 이메일 중복 검사
  public boolean emailExists(String email) {
    return userRepository.findByEmail(email).isPresent(); // 이메일이 이미 존재하면 true
  }

  // 이름 중복 검사
  public boolean nameExists(String name) {
    return userRepository.findByName(name).isPresent(); // 이름이 이미 존재하면 true
  }

  // 비밀번호 유효성 검사
  private boolean isValidPassword(String password) {
    String regex =
      "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,16}$";
    return password != null && password.matches(regex);
  }
}
