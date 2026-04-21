<template>
  <div class="login-page">
    <div class="login-card">
      <h1>AI Star 投票系统</h1>
      <p>请使用 Welink 扫码登录</p>
      <button @click="handleLogin" class="login-btn">使用 Welink 登录</button>
    </div>
  </div>
</template>

<script setup>
import { useUserStore } from '../stores/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

const handleLogin = async () => {
  try {
    // 模拟登录 - 实际应该跳转 Welink OAuth
    const mockCode = 'mock-user-' + Date.now()
    const success = await userStore.login(mockCode)
    if (success) {
      const user = userStore.user
      if (user.role === 'ADMIN') {
        router.push('/admin')
      } else {
        router.push('/vote')
      }
    } else {
      alert('登录失败，请确保后端服务已启动')
    }
  } catch (error) {
    alert('无法连接到服务器，请确保后端服务已启动')
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  background: white;
  padding: 48px;
  border-radius: 16px;
  text-align: center;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
}

h1 {
  margin: 0 0 16px 0;
  font-size: 28px;
  color: #333;
}

p {
  margin: 0 0 32px 0;
  color: #666;
}

.login-btn {
  padding: 14px 32px;
  font-size: 16px;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.login-btn:hover {
  background: #40a9ff;
}
</style>
