<template>
  <header class="vote-header">
    <div class="header-content">
      <h1>AI Star 投票</h1>
      <div class="user-info">
        <img :src="user.avatar || '/default-avatar.png'" :alt="user.name" class="user-avatar" />
        <span>{{ user.name }}</span>
        <button @click="handleLogout" class="logout-btn">退出</button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { useUserStore } from '../stores/user'
import { useRouter } from 'vue-router'

const props = defineProps({
  user: {
    type: Object,
    required: true
  }
})

const userStore = useUserStore()
const router = useRouter()

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.vote-header {
  background: white;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  padding: 16px 24px;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

h1 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
}

.logout-btn {
  padding: 6px 12px;
  background: #f5f5f5;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>
