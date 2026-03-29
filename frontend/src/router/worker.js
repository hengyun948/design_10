import WorkerLayout from '@/layout/WorkerLayout.vue'

export default [
  {
    path: '/worker',
    component: WorkerLayout,
    redirect: '/worker/home',
    meta: { role: 'REPAIR_WORKER' },
    children: [
      { path: 'home', component: () => import('@/views/worker/Home.vue'), meta: { title: '工作台' } },
      { path: 'order', component: () => import('@/views/worker/Order.vue'), meta: { title: '我的工单' } },
      { path: 'inspection', component: () => import('@/views/worker/Inspection.vue'), meta: { title: '巡检记录' } },
      { path: 'profile', component: () => import('@/views/worker/Profile.vue'), meta: { title: '个人中心' } }
    ]
  }
]
