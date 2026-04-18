<template>
  <aside class="admin-sidebar">
    <h2>管理后台</h2>
    <nav>
      <a
        v-for="item in menuItems"
        :key="item.path"
        :href="'#' + item.path"
        :class="{ active: currentPath === item.path }"
        @click.prevent="navigate(item.path)"
      >
        {{ item.name }}
      </a>
    </nav>
  </aside>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  currentPath: {
    type: String,
    default: 'sessions'
  }
})

const emit = defineEmits(['navigate'])

const menuItems = [
  { name: '投票活动', path: 'sessions' },
  { name: '候选人管理', path: 'candidates' },
  { name: '投票结果', path: 'results' },
  { name: '投票审计', path: 'audit' }
]

const navigate = (path) => {
  emit('navigate', path)
}
</script>

<style scoped>
.admin-sidebar {
  width: 200px;
  background: white;
  min-height: 100vh;
  padding: 24px 0;
}

h2 {
  padding: 0 24px;
  margin: 0 0 24px 0;
  font-size: 18px;
}

nav a {
  display: block;
  padding: 12px 24px;
  color: #333;
  text-decoration: none;
  transition: all 0.2s;
}

nav a:hover {
  background: #f5f5f5;
}

nav a.active {
  background: #e6f7ff;
  color: #1890ff;
  border-right: 3px solid #1890ff;
}
</style>
