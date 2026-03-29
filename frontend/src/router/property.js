import PropertyLayout from '@/layout/PropertyLayout.vue'

export default [
  {
    path: '/property',
    component: PropertyLayout,
    redirect: '/property/home',
    meta: { role: 'PROPERTY_STAFF' },
    children: [
      { path: 'home', component: () => import('@/views/property/Home.vue'), meta: { title: '工作台' } },
      { path: 'owner', component: () => import('@/views/property/Owner.vue'), meta: { title: '业主管理' } },
      { path: 'house', component: () => import('@/views/property/House.vue'), meta: { title: '房屋管理' } },
      { path: 'bill', component: () => import('@/views/property/Bill.vue'), meta: { title: '账单管理' } },
      { path: 'repair', component: () => import('@/views/property/Repair.vue'), meta: { title: '报修工单' } },
      { path: 'notice', component: () => import('@/views/property/Notice.vue'), meta: { title: '公告管理' } },
      { path: 'complaint', component: () => import('@/views/property/Complaint.vue'), meta: { title: '投诉处理' } },
      { path: 'activity', component: () => import('@/views/property/Activity.vue'), meta: { title: '活动管理' } },
      { path: 'parking', component: () => import('@/views/property/Parking.vue'), meta: { title: '停车管理' } },
      { path: 'facility', component: () => import('@/views/property/Facility.vue'), meta: { title: '设施管理' } },
      { path: 'statistics', component: () => import('@/views/property/Statistics.vue'), meta: { title: '统计分析' } }
    ]
  }
]
