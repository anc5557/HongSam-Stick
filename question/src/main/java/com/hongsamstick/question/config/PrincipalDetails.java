package com.hongsamstick.question.config;

import com.hongsamstick.question.domain.Member;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class PrincipalDetails implements UserDetails {

  private Member member;

  public PrincipalDetails(Member member) {
    this.member = member;
  }

  public Member getMember() {
    return member;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptyList();
  }

  @Override
  public String getUsername() {
    return member.getEmail();
  }

  @Override
  public String getPassword() {
    return member.getPassword();
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }
}
