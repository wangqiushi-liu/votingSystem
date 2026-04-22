import axios from 'axios'

// Convert snake_case to camelCase
const toCamelCase = (obj) => {
  if (Array.isArray(obj)) {
    return obj.map(toCamelCase)
  } else if (obj !== null && typeof obj === 'object') {
    return Object.keys(obj).reduce((acc, key) => {
      const camelKey = key.replace(/_([a-z])/g, (_, letter) => letter.toUpperCase())
      acc[camelKey] = toCamelCase(obj[key])
      return acc
    }, {})
  }
  return obj
}

// Convert camelCase to snake_case
const toSnakeCase = (obj) => {
  if (Array.isArray(obj)) {
    return obj.map(toSnakeCase)
  } else if (obj !== null && typeof obj === 'object') {
    return Object.keys(obj).reduce((acc, key) => {
      const snakeKey = key.replace(/[A-Z]/g, letter => `_${letter.toLowerCase()}`)
      acc[snakeKey] = toSnakeCase(obj[key])
      return acc
    }, {})
  }
  return obj
}

const apiClient = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
apiClient.interceptors.request.use(config => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  if (user.id) {
    config.headers['X-User-Id'] = user.id
    config.headers['X-User-Role'] = user.role
  }
  return config
})

// 响应拦截器
apiClient.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// Auth API
export const authApi = {
  welinkLogin: () => apiClient.get('/auth/welink/login'),
  welinkCallback: (code) => apiClient.get('/auth/welink/callback', { params: { code } }),
  getMe: () => apiClient.get('/auth/me')
}

// Vote API
export const voteApi = {
  getActiveSession: () => apiClient.get('/votes/sessions/active'),
  getCandidates: () => apiClient.get('/votes/candidates'),
  submitVote: (candidateIds) => apiClient.post('/votes/submit', { candidateIds }),
  getVoteStatus: () => apiClient.get('/votes/status')
}

// Admin API
export const adminApi = {
  // Sessions
  getSessions: () => apiClient.get('/admin/sessions'),
  getSession: (id) => apiClient.get(`/admin/sessions/${id}`),
  createSession: (data) => apiClient.post('/admin/sessions', data),
  updateSession: (id, data) => apiClient.put(`/admin/sessions/${id}`, data),
  deleteSession: (id) => apiClient.delete(`/admin/sessions/${id}`),
  // Candidates
  getCandidates: () => apiClient.get('/admin/candidates'),
  getCandidate: (id) => apiClient.get(`/admin/candidates/${id}`),
  createCandidate: (data) => apiClient.post('/admin/candidates', data),
  updateCandidate: (id, data) => apiClient.put(`/admin/candidates/${id}`, data),
  deleteCandidate: (id) => apiClient.delete(`/admin/candidates/${id}`),
  // Results & Audit
  getResults: () => apiClient.get('/admin/results'),
  getAudit: () => apiClient.get('/admin/audit')
}

export default apiClient
