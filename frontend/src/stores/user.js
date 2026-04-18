import { defineStore } from 'pinia'
import { authApi } from '../api'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user') || '{}'),
    isLoggedIn: !!JSON.parse(localStorage.getItem('user') || '{}').id,
    isAdmin: JSON.parse(localStorage.getItem('user') || '{}').role === 'ADMIN'
  }),

  actions: {
    async login(code) {
      try {
        const response = await authApi.welinkCallback(code)
        this.user = response.user
        this.isLoggedIn = true
        this.isAdmin = response.user.role === 'ADMIN'
        localStorage.setItem('user', JSON.stringify(response.user))
        localStorage.setItem('token', response.token)
        return true
      } catch (error) {
        console.error('Login failed:', error)
        return false
      }
    },

    logout() {
      this.user = {}
      this.isLoggedIn = false
      this.isAdmin = false
      localStorage.removeItem('user')
      localStorage.removeItem('token')
    }
  }
})