import OwnerLayout from '@/layout/OwnerLayout.vue'

export default [
  {
    path: '/owner',
    component: OwnerLayout,
    redirect: '/owner/home',
    meta: { role: 'OWNER' },
    children: [
      { path: 'home', component: () => import('@/views/owner/Home.vue'), meta: { title: '首页' } },
      { path: 'profile', component: () => import('@/views/owner/Profile.vue'), meta: { title: '个人中心' } },
      { path: 'house', component: () => import('@/views/owner/House.vue'), meta: { title: '我的房屋' } },
      { path: 'bill', component: () => import('@/views/owner/Bill.vue'), meta: { title: '我的账单' } },
      { path: 'repair', component: () => import('@/views/owner/Repair.vue'), meta: { title: '我的报修' } },
      { path: 'notice', component: () => import('@/views/owner/Notice.vue'), meta: { title: '公告通知' } },
      { path: 'complaint', component: () => import('@/views/owner/Complaint.vue'), meta: { title: '投诉建议' } },
      { path: 'activity', component: () => import('@/views/owner/Activity.vue'), meta: { title: '社区活动' } },
      { path: 'parking', component: () => import('@/views/owner/Parking.vue'), meta: { title: '停车信息' } }
    ]
  }
]
