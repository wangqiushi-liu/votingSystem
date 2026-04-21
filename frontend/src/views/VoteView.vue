<template>
  <div class="vote-page">
    <VoteHeader :user="user" />

    <div class="vote-content" v-if="session">
      <div class="session-info"></div>

      <div class="timeline">
        <div class="timeline-item">
          <div class="timeline-dot"></div>
          <div class="timeline-content">
            <span class="timeline-date">11.20</span>
            <span class="timeline-title">推荐</span>
          </div>
        </div>
        <div class="timeline-item">
          <div class="timeline-dot"></div>
          <div class="timeline-content">
            <span class="timeline-date">12.01</span>
            <span class="timeline-title">专家会审视</span>
          </div>
        </div>
        <div class="timeline-item active">
          <div class="timeline-dot"></div>
          <div class="timeline-content">
            <span class="timeline-date">12.10</span>
            <span class="timeline-title">大众投票</span>
          </div>
        </div>
        <div class="timeline-item">
          <div class="timeline-dot"></div>
          <div class="timeline-content">
            <span class="timeline-date">12.20</span>
            <span class="timeline-title">专评会评选</span>
          </div>
        </div>
        <div class="timeline-item">
          <div class="timeline-dot"></div>
          <div class="timeline-content">
            <span class="timeline-date">12.30</span>
            <span class="timeline-title">结果公示</span>
          </div>
        </div>
      </div>

      <div class="vote-status" v-if="hasVoted">
        <span class="voted-badge">您已完成投票</span>
      </div>

      <div class="selected-info" v-else>
        <span>已选择: {{ selectedIds.length }} / {{ session.minVotes }}-{{ session.maxVotes }}</span>
        <span class="vote-rule-text">投票规则：每人可投5-10票</span>
      </div>

      <div class="candidates-grid">
        <CandidateCard
          v-for="candidate in candidates"
          :key="candidate.id"
          :candidate="candidate"
          :is-selected="selectedIds.includes(candidate.id)"
          @toggle="toggleCandidate"
        />
      </div>

      <div class="vote-action" v-if="!hasVoted">
        <button
          @click="submitVote"
          :disabled="selectedIds.length < session.minVotes || selectedIds.length > session.maxVotes"
          class="submit-btn"
        >
          确认投票
        </button>
      </div>
    </div>

    <div class="no-session" v-else>
      <p>当前没有活跃的投票活动</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import { voteApi } from '../api'
import VoteHeader from '../components/VoteHeader.vue'
import CandidateCard from '../components/CandidateCard.vue'

const userStore = useUserStore()
const user = userStore.user

const session = ref(null)
const candidates = ref([])
const selectedIds = ref([])
const hasVoted = ref(false)

onMounted(async () => {
  // 获取当前投票活动
  session.value = await voteApi.getActiveSession().catch(() => null)
  // 获取候选人列表
  candidates.value = await voteApi.getCandidates().catch(() => [])
  // 检查投票状态
  const status = await voteApi.getVoteStatus().catch(() => ({ voted: false }))
  hasVoted.value = status.voted
})

const toggleCandidate = (id) => {
  const index = selectedIds.value.indexOf(id)
  if (index > -1) {
    selectedIds.value.splice(index, 1)
  } else {
    // Check if max votes reached
    if (session.value && selectedIds.value.length >= session.value.maxVotes) {
      alert(`最多只能选择 ${session.value.maxVotes} 位候选人`)
      return
    }
    selectedIds.value.push(id)
  }
}

const submitVote = async () => {
  try {
    await voteApi.submitVote(selectedIds.value)
    hasVoted.value = true
    alert('投票成功！')
  } catch (error) {
    alert(error.message || '投票失败')
  }
}
</script>

<style scoped>
.vote-page {
  min-height: 100vh;
}

.vote-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.session-info {
  background: url('/background.png') no-repeat center center;
  background-size: 100% 100%;
  height: 120px;
  padding: 0;
  border-radius: 8px;
  margin-bottom: 24px;
}

.session-info h2 {
  margin: 0 0 8px 0;
}

.session-info p {
  margin: 0;
  color: #666;
}

.vote-rule {
  margin-top: 8px !important;
  color: #1890ff !important;
  font-weight: 500;
}

.timeline {
  display: flex;
  justify-content: space-between;
  background: white;
  padding: 24px;
  border-radius: 8px;
  margin-bottom: 24px;
  position: relative;
}

.timeline::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 24px;
  right: 24px;
  height: 2px;
  background: #e8e8e8;
  transform: translateY(-50%);
  z-index: 0;
}

.timeline-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  z-index: 1;
  flex: 1;
}

.timeline-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #d9d9d9;
  margin-bottom: 8px;
}

.timeline-item.active .timeline-dot {
  background: #1890ff;
  box-shadow: 0 0 0 4px rgba(24, 144, 255, 0.2);
}

.timeline-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.timeline-date {
  font-size: 18px;
  color: #ff0000;
  margin-bottom: 4px;
}

.timeline-title {
  font-size: 18px;
  color: #ff0000;
  font-weight: 500;
}

.timeline-item.active .timeline-date,
.timeline-item.active .timeline-title {
  color: #ff0000;
}

.selected-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
  background: white;
  border-radius: 8px;
  margin-bottom: 24px;
  font-weight: 500;
}

.vote-rule-text {
  color: #1890ff;
  font-weight: 500;
}

.voted-badge {
  display: inline-block;
  padding: 8px 24px;
  background: #52c41a;
  color: white;
  border-radius: 20px;
  font-weight: 500;
}

.candidates-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.vote-action {
  text-align: center;
}

.submit-btn {
  padding: 14px 48px;
  font-size: 16px;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.submit-btn:hover:not(:disabled) {
  background: #40a9ff;
}

.submit-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.no-session {
  text-align: center;
  padding: 48px;
  background: white;
  border-radius: 8px;
  margin: 24px auto;
  max-width: 600px;
}
</style>
