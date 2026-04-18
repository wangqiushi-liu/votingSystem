<template>
  <div class="admin-page">
    <div class="admin-layout">
      <AdminSidebar :current-path="currentSection" @navigate="handleNavigate" />

      <main class="admin-content">
        <!-- 投票活动管理 -->
        <div v-if="currentSection === 'sessions'" class="section">
          <h2>投票活动管理</h2>
          <button @click="showSessionModal = true" class="add-btn">创建活动</button>

          <table class="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>标题</th>
                <th>开始时间</th>
                <th>结束时间</th>
                <th>票数范围</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="s in sessions" :key="s.id">
                <td>{{ s.id }}</td>
                <td>{{ s.title }}</td>
                <td>{{ formatDate(s.startTime) }}</td>
                <td>{{ formatDate(s.endTime) }}</td>
                <td>{{ s.minVotes }}-{{ s.maxVotes }}</td>
                <td><span :class="'status-' + s.status">{{ s.status }}</span></td>
                <td>
                  <button @click="editSession(s)">编辑</button>
                  <button @click="deleteSession(s.id)">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 候选人管理 -->
        <div v-if="currentSection === 'candidates'" class="section">
          <h2>候选人管理</h2>
          <button @click="showCandidateModal = true" class="add-btn">添加候选人</button>

          <table class="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>姓名</th>
                <th>部门</th>
                <th>票数</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="c in candidates" :key="c.id">
                <td>{{ c.id }}</td>
                <td>{{ c.name }}</td>
                <td>{{ c.department }}</td>
                <td>{{ c.voteCount }}</td>
                <td><span :class="'status-' + c.status">{{ c.status }}</span></td>
                <td>
                  <button @click="editCandidate(c)">编辑</button>
                  <button @click="deleteCandidate(c.id)">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 投票结果 -->
        <div v-if="currentSection === 'results'" class="section">
          <h2>投票结果</h2>
          <table class="data-table">
            <thead>
              <tr>
                <th>排名</th>
                <th>姓名</th>
                <th>部门</th>
                <th>票数</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="r in results" :key="r.candidateId">
                <td>{{ r.rank }}</td>
                <td>{{ r.name }}</td>
                <td>{{ r.department }}</td>
                <td>{{ r.voteCount }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 投票审计 -->
        <div v-if="currentSection === 'audit'" class="section">
          <h2>投票审计</h2>
          <table class="data-table">
            <thead>
              <tr>
                <th>投票用户</th>
                <th>候选人</th>
                <th>投票时间</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="a in auditData" :key="a.id">
                <td>{{ a.userName }}</td>
                <td>{{ a.candidateName }}</td>
                <td>{{ formatDate(a.votedAt) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '../api'
import AdminSidebar from '../components/AdminSidebar.vue'

const currentSection = ref('sessions')
const sessions = ref([])
const candidates = ref([])
const results = ref([])
const auditData = ref([])

const showSessionModal = ref(false)
const showCandidateModal = ref(false)

onMounted(async () => {
  await loadData()
})

const loadData = async () => {
  sessions.value = await adminApi.getSessions().catch(() => [])
  candidates.value = await adminApi.getCandidates().catch(() => [])
  results.value = await adminApi.getResults().catch(() => [])
  auditData.value = await adminApi.getAudit().catch(() => [])
}

const handleNavigate = (section) => {
  currentSection.value = section
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

const editSession = (s) => {
  // 实现编辑逻辑
  console.log('edit session', s)
}

const deleteSession = async (id) => {
  if (confirm('确认删除？')) {
    await adminApi.deleteSession(id)
    await loadData()
  }
}

const editCandidate = (c) => {
  console.log('edit candidate', c)
}

const deleteCandidate = async (id) => {
  if (confirm('确认删除？')) {
    await adminApi.deleteCandidate(id)
    await loadData()
  }
}
</script>

<style scoped>
.admin-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.admin-layout {
  display: flex;
}

.admin-content {
  flex: 1;
  padding: 24px;
}

.section h2 {
  margin: 0 0 24px 0;
}

.add-btn {
  padding: 8px 16px;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-bottom: 16px;
}

.data-table {
  width: 100%;
  background: white;
  border-collapse: collapse;
  border-radius: 8px;
  overflow: hidden;
}

.data-table th,
.data-table td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid #f0f0f0;
}

.data-table th {
  background: #fafafa;
  font-weight: 500;
}

.data-table button {
  padding: 4px 8px;
  margin-right: 8px;
  border: 1px solid #d9d9d9;
  background: white;
  border-radius: 4px;
  cursor: pointer;
}

.status-ACTIVE { color: #52c41a; }
.status-INACTIVE { color: #999; }
.status-DRAFT { color: #faad14; }
.status-ENDED { color: #f5222d; }
</style>
