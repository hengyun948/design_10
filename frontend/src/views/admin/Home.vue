<template>
  <div>
    <el-row :gutter="16" class="stat-row">
      <el-col :span="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-num">{{ stats.userCount }}</div>
          <div class="stat-label">用户总数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-num">{{ stats.ownerCount }}</div>
          <div class="stat-label">业主总数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-num">{{ stats.houseCount }}</div>
          <div class="stat-label">房屋总数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-num">{{ stats.repairCount }}</div>
          <div class="stat-label">本月工单</div>
        </el-card>
      </el-col>
    </el-row>
    <el-card shadow="never" style="margin-top: 16px;">
      <template #header>系统信息</template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="系统名称">智慧物业服务平台</el-descriptions-item>
        <el-descriptions-item label="版本">v1.0.0</el-descriptions-item>
        <el-descriptions-item label="当前账号">{{ userStore.realName }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ userStore.roleName }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const stats = reactive({ userCount: 5, ownerCount: 2, houseCount: 0, repairCount: 0 })
</script>

<style scoped>
.stat-row { margin-bottom: 0; }
.stat-card { text-align: center; }
.stat-num { font-size: 36px; font-weight: bold; color: #1890ff; }
.stat-label { font-size: 14px; color: #666; margin-top: 4px; }
</style>
