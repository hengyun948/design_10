import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'
import { useUserStore } from '@/store/user'
import ownerRoutes from './owner'
import propertyRoutes from './property'
import workerRoutes from './worker'
import adminRoutes from './admin'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: () => import('@/views/auth/Login.vue'), meta: { title: '登录' } },
  { path: '/403', component: () => import('@/views/error/403.vue') },
  { path: '/:pathMatch(.*)*', component: () => import('@/views/error/404.vue') },
  ...ownerRoutes,
  ...propertyRoutes,
  ...workerRoutes,
  ...adminRoutes
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const HOME_MAP = {
  ADMIN: '/admin/home',
  PROPERTY_STAFF: '/property/home',
  REPAIR_WORKER: '/worker/home',
  OWNER: '/owner/home'
}

function hasRoutePermission(to, roleCode) {
  const requiredRole = to.matched.find(record => record.meta?.role)?.meta?.role
  return !requiredRole || requiredRole === roleCode
}

router.beforeEach(async (to, from, next) => {
  const token = getToken()

  if (!token) {
    if (to.path === '/login') return next()
    return next('/login')
  }

  if (to.path === '/login') {
    const userStore = useUserStore()
    if (!userStore.roleCode) {
      try {
        await userStore.fetchCurrentUser()
      } catch {
        return next()
      }
    }
    return next(HOME_MAP[userStore.roleCode] || '/login')
  }

  const userStore = useUserStore()
  if (!userStore.roleCode) {
    try {
      await userStore.fetchCurrentUser()
    } catch {
      return next('/login')
    }
  }

  if (!hasRoutePermission(to, userStore.roleCode)) {
    return next('/403')
  }

  next()
})

export default router
