import AdminLayout from '@/layout/AdminLayout.vue'

export default [
  {
    path: '/admin',
    component: AdminLayout,
    redirect: '/admin/home',
    meta: { role: 'ADMIN' },
    children: [
      { path: 'home', component: () => import('@/views/admin/Home.vue'), meta: { title: '控制台' } },
      { path: 'user', component: () => import('@/views/admin/User.vue'), meta: { title: '用户管理' } },
      { path: 'role', component: () => import('@/views/admin/Role.vue'), meta: { title: '角色管理' } },
      { path: 'log', component: () => import('@/views/admin/Log.vue'), meta: { title: '日志管理' } }
    ]
  }
]
