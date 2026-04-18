package com.voting.service;

import com.voting.dto.AdminResultResponse;
import com.voting.entity.*;
import com.voting.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final VoteSessionRepository voteSessionRepository;
    private final CandidateRepository candidateRepository;
    private final VoteRecordRepository voteRecordRepository;
    private final UserRepository userRepository;

    // ==================== VoteSession CRUD ====================

    public List<VoteSession> getAllSessions() {
        return voteSessionRepository.findAll();
    }

    public VoteSession getSessionById(Long id) {
        return voteSessionRepository.findById(id).orElseThrow(() -> new RuntimeException("投票活动不存在"));
    }

    @Transactional
    public VoteSession createSession(VoteSession session) {
        return voteSessionRepository.save(session);
    }

    @Transactional
    public VoteSession updateSession(Long id, VoteSession session) {
        VoteSession existing = getSessionById(id);
        existing.setTitle(session.getTitle());
        existing.setDescription(session.getDescription());
        existing.setStartTime(session.getStartTime());
        existing.setEndTime(session.getEndTime());
        existing.setMinVotes(session.getMinVotes());
        existing.setMaxVotes(session.getMaxVotes());
        existing.setStatus(session.getStatus());
        return voteSessionRepository.save(existing);
    }

    @Transactional
    public void deleteSession(Long id) {
        voteSessionRepository.deleteById(id);
    }

    // ==================== Candidate CRUD ====================

    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    public Candidate getCandidateById(Long id) {
        return candidateRepository.findById(id).orElseThrow(() -> new RuntimeException("候选人不存在"));
    }

    @Transactional
    public Candidate createCandidate(Candidate candidate) {
        return candidateRepository.save(candidate);
    }

    @Transactional
    public Candidate updateCandidate(Long id, Candidate candidate) {
        Candidate existing = getCandidateById(id);
        existing.setName(candidate.getName());
        existing.setDepartment(candidate.getDepartment());
        existing.setAvatar(candidate.getAvatar());
        existing.setBio(candidate.getBio());
        existing.setStatus(candidate.getStatus());
        return candidateRepository.save(existing);
    }

    @Transactional
    public void deleteCandidate(Long id) {
        candidateRepository.deleteById(id);
    }

    // ==================== Results & Audit ====================

    public List<AdminResultResponse> getResults() {
        List<Candidate> candidates = candidateRepository.findByStatusOrderByVoteCountDesc(Candidate.CandidateStatus.ACTIVE);
        return candidates.stream()
                .map(c -> {
                    AdminResultResponse r = new AdminResultResponse();
                    r.setCandidateId(c.getId());
                    r.setName(c.getName());
                    r.setDepartment(c.getDepartment());
                    r.setAvatar(c.getAvatar());
                    r.setVoteCount(c.getVoteCount());
                    r.setRank(c.getRank());
                    return r;
                })
                .collect(Collectors.toList());
    }

    public List<VoteRecord> getAuditData() {
        return voteRecordRepository.findAll();
    }
}
