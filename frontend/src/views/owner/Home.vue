<template>
  <div>
    <el-card shadow="never">
      <template #header>欢迎回来，{{ userStore.realName }}</template>
      <el-row :gutter="16">
        <el-col :span="6" v-for="item in quickLinks" :key="item.path">
          <el-card shadow="hover" class="quick-card" @click="$router.push(item.path)">
            <el-icon :size="32" :color="item.color"><component :is="item.icon" /></el-icon>
            <div class="quick-label">{{ item.label }}</div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
    <el-card shadow="never" style="margin-top: 16px;">
      <template #header>最新公告</template>
      <el-empty description="暂无公告" v-if="!notices.length" />
      <div v-else class="notice-list">
        <div v-for="n in notices" :key="n.id" class="notice-item">
          <span>{{ n.title }}</span>
          <span class="notice-time">{{ n.publishTime }}</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { House, Wallet, Tools, Bell, ChatDotRound, Calendar, Van } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const notices = ref([])

const quickLinks = [
  { label: '我的房屋', path: '/owner/house', icon: House, color: '#1890ff' },
  { label: '我的账单', path: '/owner/bill', icon: Wallet, color: '#52c41a' },
  { label: '在线报修', path: '/owner/repair', icon: Tools, color: '#fa8c16' },
  { label: '公告通知', path: '/owner/notice', icon: Bell, color: '#722ed1' },
  { label: '投诉建议', path: '/owner/complaint', icon: ChatDotRound, color: '#eb2f96' },
  { label: '社区活动', path: '/owner/activity', icon: Calendar, color: '#13c2c2' },
  { label: '停车信息', path: '/owner/parking', icon: Van, color: '#2f54eb' }
]
</script>

<style scoped>
.quick-card { text-align: center; cursor: pointer; padding: 16px 0; }
.quick-label { margin-top: 8px; font-size: 14px; color: #333; }
.notice-list { display: flex; flex-direction: column; }
.notice-item { display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid #f0f0f0; }
.notice-time { color: #999; font-size: 12px; }
</style>
