package com.hongsamstick.question.config;

import com.hongsamstick.question.domain.User;
import com.hongsamstick.question.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PrincipalDetailsService implements UserDetailsService {

  private UserRepository userRepository;

  public PrincipalDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email)
    throws UsernameNotFoundException {
    User user = userRepository
      .findByEmail(email)
      .orElseThrow(() ->
        new UsernameNotFoundException(
          "아이디 또는 비밀번호가 맞지 않습니다. 다시 확인해 주세요."
        )
      );
    return new PrincipalDetails(user);
  }
}
