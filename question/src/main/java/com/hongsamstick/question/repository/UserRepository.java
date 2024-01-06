package com.hongsamstick.question.repository;

import com.hongsamstick.question.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email); 

  Optional<User> findByName(String name); 
}
