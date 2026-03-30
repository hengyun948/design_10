<template>
  <div>
    <el-row :gutter="16" class="stat-row">
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
          <div class="stat-num pending">{{ stats.pendingRepair }}</div>
          <div class="stat-label">待处理工单</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-num warn">{{ stats.pendingBill }}</div>
          <div class="stat-label">待缴账单</div>
        </el-card>
      </el-col>
    </el-row>
    <el-card shadow="never" style="margin-top: 16px;">
      <template #header>快捷入口</template>
      <div class="quick-links">
        <el-button type="primary" plain @click="$router.push('/property/repair')">报修工单</el-button>
        <el-button type="success" plain @click="$router.push('/property/bill')">账单管理</el-button>
        <el-button type="warning" plain @click="$router.push('/property/notice')">发布公告</el-button>
        <el-button type="info" plain @click="$router.push('/property/complaint')">投诉处理</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { getOverview } from '@/api/statistics'

const stats = reactive({ ownerCount: 0, houseCount: 0, pendingRepair: 0, pendingBill: 0 })

onMounted(async () => {
  try {
    const res = await getOverview()
    stats.ownerCount = res.data.ownerCount ?? 0
    stats.houseCount = res.data.houseCount ?? 0
    stats.pendingRepair = res.data.pendingRepair ?? 0
    stats.pendingBill = res.data.pendingBill ?? 0
  } catch {}
})
</script>

<style scoped>
.stat-card { text-align: center; }
.stat-num { font-size: 36px; font-weight: bold; color: #1890ff; }
.stat-num.pending { color: #fa8c16; }
.stat-num.warn { color: #f5222d; }
.stat-label { font-size: 14px; color: #666; margin-top: 4px; }
.quick-links { display: flex; gap: 12px; flex-wrap: wrap; }
</style>
