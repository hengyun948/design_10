<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="aside">
      <div class="logo">智慧物业 · 业主端</div>
      <el-menu :default-active="activeMenu" router background-color="#001529" text-color="#ffffffa6" active-text-color="#ffffff">
        <el-menu-item index="/owner/home"><el-icon><Monitor /></el-icon>首页</el-menu-item>
        <el-menu-item index="/owner/profile"><el-icon><User /></el-icon>个人中心</el-menu-item>
        <el-menu-item index="/owner/house"><el-icon><House /></el-icon>我的房屋</el-menu-item>
        <el-menu-item index="/owner/bill"><el-icon><Wallet /></el-icon>我的账单</el-menu-item>
        <el-menu-item index="/owner/repair"><el-icon><Tools /></el-icon>我的报修</el-menu-item>
        <el-menu-item index="/owner/notice"><el-icon><Bell /></el-icon>公告通知</el-menu-item>
        <el-menu-item index="/owner/complaint"><el-icon><ChatDotRound /></el-icon>投诉建议</el-menu-item>
        <el-menu-item index="/owner/activity"><el-icon><Calendar /></el-icon>社区活动</el-menu-item>
        <el-menu-item index="/owner/parking"><el-icon><Van /></el-icon>停车信息</el-menu-item>
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
import { Monitor, User, House, Wallet, Tools, Bell, ChatDotRound, Calendar, Van } from '@element-plus/icons-vue'
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
