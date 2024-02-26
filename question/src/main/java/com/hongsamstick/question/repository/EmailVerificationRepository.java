package com.hongsamstick.question.repository;

import com.hongsamstick.question.domain.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationRepository
  extends JpaRepository<EmailVerification, String> {
  
  EmailVerification findByEmail(String email);
}
