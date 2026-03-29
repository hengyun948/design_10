<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="aside">
      <div class="logo">智慧物业 · 物业端</div>
      <el-menu :default-active="activeMenu" router background-color="#001529" text-color="#ffffffa6" active-text-color="#ffffff">
        <el-menu-item index="/property/home"><el-icon><Monitor /></el-icon>工作台</el-menu-item>
        <el-menu-item index="/property/owner"><el-icon><User /></el-icon>业主管理</el-menu-item>
        <el-menu-item index="/property/house"><el-icon><House /></el-icon>房屋管理</el-menu-item>
        <el-menu-item index="/property/bill"><el-icon><Wallet /></el-icon>账单管理</el-menu-item>
        <el-menu-item index="/property/repair"><el-icon><Tools /></el-icon>报修工单</el-menu-item>
        <el-menu-item index="/property/notice"><el-icon><Bell /></el-icon>公告管理</el-menu-item>
        <el-menu-item index="/property/complaint"><el-icon><ChatDotRound /></el-icon>投诉处理</el-menu-item>
        <el-menu-item index="/property/activity"><el-icon><Calendar /></el-icon>活动管理</el-menu-item>
        <el-menu-item index="/property/parking"><el-icon><Van /></el-icon>停车管理</el-menu-item>
        <el-menu-item index="/property/facility"><el-icon><Setting /></el-icon>设施管理</el-menu-item>
        <el-menu-item index="/property/statistics"><el-icon><DataAnalysis /></el-icon>统计分析</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span class="breadcrumb">{{ currentTitle }}</span>
        <div class="header-right">
          <span class="user-name">{{ userStore.realName }}（{{ userStore.roleName }}）</span>
          <el-button link type="danger" @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="main"><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Monitor, User, House, Wallet, Tools, Bell, ChatDotRound, Calendar, Van, Setting, DataAnalysis } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta?.title || '')

async function handleLogout() {
  await ElMessageBox.confirm('确认退出登录？', '提示', { type: 'warning' })
  await userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout-container { height: 100vh; }
.aside { background-color: #001529; overflow: hidden; }
.logo { height: 60px; line-height: 60px; text-align: center; color: #fff; font-size: 16px; font-weight: bold; background: #002040; }
.header { display: flex; align-items: center; justify-content: space-between; background: #fff; border-bottom: 1px solid #f0f0f0; padding: 0 20px; }
.header-right { display: flex; align-items: center; gap: 12px; }
.user-name { color: #666; font-size: 14px; }
.main { background: #f5f7fa; padding: 20px; overflow-y: auto; }
</style>
