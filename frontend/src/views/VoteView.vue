<template>
  <div class="vote-page">
    <VoteHeader :user="user" />

    <div class="vote-content" v-if="session">
      <div class="session-info">
        <h2>{{ session.title }}</h2>
        <p>{{ session.description }}</p>
        <p class="vote-rule">请选择 {{ session.minVotes }}-{{ session.maxVotes }} 位候选人</p>
      </div>

      <div class="vote-status" v-if="hasVoted">
        <span class="voted-badge">您已完成投票</span>
      </div>

      <div class="selected-info" v-else>
        <span>已选择: {{ selectedIds.length }} / {{ session.minVotes }}-{{ session.maxVotes }}</span>
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
  background: #f5f5f5;
}

.vote-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.session-info {
  background: white;
  padding: 24px;
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

.selected-info {
  text-align: center;
  padding: 12px;
  background: white;
  border-radius: 8px;
  margin-bottom: 24px;
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
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
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
