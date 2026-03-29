import { defineStore } from 'pinia'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { login as loginApi, getCurrentUser, logout as logoutApi } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    userId: null,
    username: '',
    realName: '',
    roleCode: '',
    roleName: '',
    avatar: ''
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    homeRoute: (state) => {
      const map = {
        ADMIN: '/admin/home',
        PROPERTY_STAFF: '/property/home',
        REPAIR_WORKER: '/worker/home',
        OWNER: '/owner/home'
      }
      return map[state.roleCode] || '/login'
    }
  },

  actions: {
    async login(credentials) {
      const res = await loginApi(credentials)
      const data = res.data
      this.token = data.token
      this.userId = data.userId
      this.username = data.username
      this.realName = data.realName
      this.roleCode = data.roleCode
      this.roleName = data.roleName
      this.avatar = data.avatar || ''
      setToken(data.token)
      return data
    },

    async fetchCurrentUser() {
      const res = await getCurrentUser()
      const data = res.data
      this.userId = data.userId
      this.username = data.username
      this.realName = data.realName
      this.roleCode = data.roleCode
      this.roleName = data.roleName
      this.avatar = data.avatar || ''
      return data
    },

    async logout() {
      try {
        await logoutApi()
      } finally {
        this.token = ''
        this.userId = null
        this.username = ''
        this.realName = ''
        this.roleCode = ''
        this.roleName = ''
        this.avatar = ''
        removeToken()
      }
    }
  }
})
