package com.voting.service;

import com.voting.dto.VoteStatusResponse;
import com.voting.dto.VoteSubmitRequest;
import com.voting.entity.*;
import com.voting.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteSessionRepository voteSessionRepository;
    private final VoteRecordRepository voteRecordRepository;
    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;

    public VoteSession getActiveSession() {
        return voteSessionRepository.findFirstByStatusOrderByCreatedAtDesc(SessionStatus.ACTIVE)
                .orElse(null);
    }

    public List<Candidate> getCandidates() {
        return candidateRepository.findByStatusOrderByVoteCountDesc(Candidate.CandidateStatus.ACTIVE);
    }

    public VoteStatusResponse getVoteStatus(Long userId) {
        boolean voted = voteRecordRepository.existsByUserId(userId);
        int voteCount = voted ? voteRecordRepository.findByUserId(userId).size() : 0;
        return new VoteStatusResponse(voted, voteCount);
    }

    @Transactional
    public void submitVote(Long userId, VoteSubmitRequest request) {
        // 检查是否已投票
        if (voteRecordRepository.existsByUserId(userId)) {
            throw new RuntimeException("您已经投过票了，不能修改");
        }

        // 检查投票活动是否有效
        VoteSession session = getActiveSession();
        if (session == null) {
            throw new RuntimeException("当前没有活跃的投票活动");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(session.getStartTime()) || now.isAfter(session.getEndTime())) {
            throw new RuntimeException("投票不在有效时间内");
        }

        // 检查票数范围
        int voteCount = request.getCandidateIds().size();
        if (voteCount < session.getMinVotes() || voteCount > session.getMaxVotes()) {
            throw new RuntimeException("投票数量必须在" + session.getMinVotes() + "-" + session.getMaxVotes() + "之间");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));

        // 保存投票记录
        for (Long candidateId : request.getCandidateIds()) {
            VoteRecord record = new VoteRecord();
            record.setUser(user);
            Candidate candidate = candidateRepository.findById(candidateId)
                    .orElseThrow(() -> new RuntimeException("候选人不存在: " + candidateId));
            record.setCandidate(candidate);
            voteRecordRepository.save(record);

            // 增加候选人票数
            candidateRepository.incrementVoteCount(candidateId);
        }
    }
}
