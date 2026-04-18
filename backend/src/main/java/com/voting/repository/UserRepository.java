package com.voting.repository;

import com.voting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByWelinkUserId(String welinkUserId);
    boolean existsByWelinkUserId(String welinkUserId);
}