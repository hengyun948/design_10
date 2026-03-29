<template>
  <div>
    <el-row :gutter="16">
      <el-col :span="8">
        <el-card shadow="never" class="stat-card">
          <div class="stat-num warn">{{ stats.pending }}</div>
          <div class="stat-label">待处理工单</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="stat-card">
          <div class="stat-num doing">{{ stats.processing }}</div>
          <div class="stat-label">处理中工单</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="stat-card">
          <div class="stat-num done">{{ stats.done }}</div>
          <div class="stat-label">已完成工单</div>
        </el-card>
      </el-col>
    </el-row>
    <el-card shadow="never" style="margin-top: 16px;">
      <template #header>快捷入口</template>
      <div class="quick-links">
        <el-button type="primary" plain @click="$router.push('/worker/order')">查看工单</el-button>
        <el-button type="info" plain @click="$router.push('/worker/inspection')">巡检记录</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
const stats = reactive({ pending: 0, processing: 0, done: 0 })
</script>

<style scoped>
.stat-card { text-align: center; }
.stat-num { font-size: 36px; font-weight: bold; color: #1890ff; }
.stat-num.warn { color: #fa8c16; }
.stat-num.doing { color: #1890ff; }
.stat-num.done { color: #52c41a; }
.stat-label { font-size: 14px; color: #666; margin-top: 4px; }
.quick-links { display: flex; gap: 12px; }
</style>
