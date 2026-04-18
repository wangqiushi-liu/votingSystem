package com.voting.repository;

import com.voting.entity.VoteSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VoteSessionRepository extends JpaRepository<VoteSession, Long> {
    Optional<VoteSession> findFirstByStatusOrderByCreatedAtDesc(VoteSession.SessionStatus status);
}