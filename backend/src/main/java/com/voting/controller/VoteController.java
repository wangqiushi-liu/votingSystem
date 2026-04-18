package com.voting.controller;

import com.voting.dto.VoteStatusResponse;
import com.voting.dto.VoteSubmitRequest;
import com.voting.entity.Candidate;
import com.voting.entity.VoteSession;
import com.voting.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @GetMapping("/sessions/active")
    public ResponseEntity<VoteSession> getActiveSession() {
        VoteSession session = voteService.getActiveSession();
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(session);
    }

    @GetMapping("/candidates")
    public ResponseEntity<List<Candidate>> getCandidates() {
        return ResponseEntity.ok(voteService.getCandidates());
    }

    @PostMapping("/submit")
    public ResponseEntity<Void> submitVote(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody VoteSubmitRequest request) {
        voteService.submitVote(userId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status")
    public ResponseEntity<VoteStatusResponse> getVoteStatus(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(voteService.getVoteStatus(userId));
    }
}
