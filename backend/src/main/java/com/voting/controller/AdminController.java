package com.voting.controller;

import com.voting.dto.AdminResultResponse;
import com.voting.entity.Candidate;
import com.voting.entity.VoteRecord;
import com.voting.entity.VoteSession;
import com.voting.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ==================== VoteSession CRUD ====================

    @GetMapping("/sessions")
    public ResponseEntity<List<VoteSession>> getAllSessions() {
        return ResponseEntity.ok(adminService.getAllSessions());
    }

    @GetMapping("/sessions/{id}")
    public ResponseEntity<VoteSession> getSession(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getSessionById(id));
    }

    @PostMapping("/sessions")
    public ResponseEntity<VoteSession> createSession(@RequestBody VoteSession session) {
        return ResponseEntity.ok(adminService.createSession(session));
    }

    @PutMapping("/sessions/{id}")
    public ResponseEntity<VoteSession> updateSession(@PathVariable Long id, @RequestBody VoteSession session) {
        return ResponseEntity.ok(adminService.updateSession(id, session));
    }

    @DeleteMapping("/sessions/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        adminService.deleteSession(id);
        return ResponseEntity.ok().build();
    }

    // ==================== Candidate CRUD ====================

    @GetMapping("/candidates")
    public ResponseEntity<List<Candidate>> getAllCandidates() {
        return ResponseEntity.ok(adminService.getAllCandidates());
    }

    @GetMapping("/candidates/{id}")
    public ResponseEntity<Candidate> getCandidate(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getCandidateById(id));
    }

    @PostMapping("/candidates")
    public ResponseEntity<Candidate> createCandidate(@RequestBody Candidate candidate) {
        return ResponseEntity.ok(adminService.createCandidate(candidate));
    }

    @PutMapping("/candidates/{id}")
    public ResponseEntity<Candidate> updateCandidate(@PathVariable Long id, @RequestBody Candidate candidate) {
        return ResponseEntity.ok(adminService.updateCandidate(id, candidate));
    }

    @DeleteMapping("/candidates/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable Long id) {
        adminService.deleteCandidate(id);
        return ResponseEntity.ok().build();
    }

    // ==================== Results & Audit ====================

    @GetMapping("/results")
    public ResponseEntity<List<AdminResultResponse>> getResults() {
        return ResponseEntity.ok(adminService.getResults());
    }

    @GetMapping("/audit")
    public ResponseEntity<List<VoteRecord>> getAudit() {
        return ResponseEntity.ok(adminService.getAuditData());
    }
}
