import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import VoteView from '../views/VoteView.vue'
import AdminView from '../views/AdminView.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginView
  },
  {
    path: '/vote',
    name: 'Vote',
    component: VoteView,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: AdminView,
    meta: { requiresAuth: true, requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = JSON.parse(localStorage.getItem('user') || '{}')
  if (to.meta.requiresAuth && !userStore.id) {
    next('/login')
  } else if (to.meta.requiresAdmin && userStore.role !== 'ADMIN') {
    next('/vote')
  } else {
    next()
  }
})

export default router
