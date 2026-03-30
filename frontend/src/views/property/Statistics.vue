<template>
  <div>
    <!-- 概览卡片 -->
    <el-row :gutter="16" style="margin-bottom:16px">
      <el-col :span="6" v-for="item in overviewCards" :key="item.label">
        <el-card shadow="never" class="stat-card">
          <div class="stat-num" :style="{ color: item.color }">{{ item.value }}</div>
          <div class="stat-label">{{ item.label }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-bottom:16px">
      <!-- 工单状态 -->
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>工单状态分布</template>
          <div class="bar-group">
            <div v-for="(val, key) in repairStatus" :key="key" class="bar-row">
              <span class="bar-label">{{ key }}</span>
              <div class="bar-track">
                <div class="bar-fill repair" :style="{ width: barWidth(val, repairStatusTotal) }"></div>
              </div>
              <span class="bar-val">{{ val }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 账单状态 -->
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>账单状态分布</template>
          <div class="bar-group">
            <div v-for="key in ['未缴','已缴','逾期']" :key="key" class="bar-row">
              <span class="bar-label">{{ key }}</span>
              <div class="bar-track">
                <div class="bar-fill bill" :style="{ width: barWidth(billStatus[key], billStatusTotal) }"></div>
              </div>
              <span class="bar-val">{{ billStatus[key] ?? 0 }}</span>
            </div>
          </div>
          <el-divider />
          <el-descriptions :column="2" size="small" border>
            <el-descriptions-item label="应缴总额">¥ {{ billStatus['应缴总额'] }}</el-descriptions-item>
            <el-descriptions-item label="已缴总额">¥ {{ billStatus['已缴总额'] }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-bottom:16px">
      <!-- 房屋状态 -->
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>房屋状态</template>
          <div class="bar-group">
            <div v-for="(val, key) in houseStatus" :key="key" class="bar-row">
              <span class="bar-label">{{ key }}</span>
              <div class="bar-track">
                <div class="bar-fill house" :style="{ width: barWidth(val, houseStatusTotal) }"></div>
              </div>
              <span class="bar-val">{{ val }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 停车状态 -->
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>停车位状态</template>
          <div class="bar-group">
            <div v-for="key in ['空闲','已占用','已锁定']" :key="key" class="bar-row">
              <span class="bar-label">{{ key }}</span>
              <div class="bar-track">
                <div class="bar-fill parking" :style="{ width: barWidth(parkingStatus[key], parkingSpaceTotal) }"></div>
              </div>
              <span class="bar-val">{{ parkingStatus[key] ?? 0 }}</span>
            </div>
          </div>
          <el-tag type="warning" style="margin-top:8px">未缴费记录：{{ parkingStatus['未缴费记录'] ?? 0 }} 条</el-tag>
        </el-card>
      </el-col>

      <!-- 投诉建议 -->
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>投诉建议</template>
          <div class="bar-group">
            <div v-for="key in ['投诉','建议','待处理','已处理']" :key="key" class="bar-row">
              <span class="bar-label">{{ key }}</span>
              <div class="bar-track">
                <div class="bar-fill complaint" :style="{ width: barWidth(complaintCategory[key], complaintTotal) }"></div>
              </div>
              <span class="bar-val">{{ complaintCategory[key] ?? 0 }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { getOverview, getRepairStatus, getBillStatus, getHouseStatus, getParkingStatus, getComplaintCategory } from '@/api/statistics'

const overview = ref({})
const repairStatus = ref({})
const billStatus = ref({})
const houseStatus = ref({})
const parkingStatus = ref({})
const complaintCategory = ref({})

const overviewCards = computed(() => [
  { label: '业主总数', value: overview.value.ownerCount ?? 0, color: '#1890ff' },
  { label: '房屋总数', value: overview.value.houseCount ?? 0, color: '#52c41a' },
  { label: '待处理工单', value: overview.value.pendingRepair ?? 0, color: '#fa8c16' },
  { label: '待缴账单', value: overview.value.pendingBill ?? 0, color: '#f5222d' },
  { label: '已发公告', value: overview.value.publishedNotice ?? 0, color: '#722ed1' },
  { label: '待处理投诉', value: overview.value.pendingComplaint ?? 0, color: '#eb2f96' },
  { label: '报名中活动', value: overview.value.activeActivity ?? 0, color: '#13c2c2' },
  { label: '空闲车位', value: `${overview.value.freeSpace ?? 0}/${overview.value.totalSpace ?? 0}`, color: '#2f54eb' },
])

const repairStatusTotal = computed(() => Object.values(repairStatus.value).reduce((a, b) => a + b, 0))
const billStatusTotal = computed(() => (billStatus.value['未缴'] ?? 0) + (billStatus.value['已缴'] ?? 0) + (billStatus.value['逾期'] ?? 0))
const houseStatusTotal = computed(() => Object.values(houseStatus.value).reduce((a, b) => a + b, 0))
const parkingSpaceTotal = computed(() => (parkingStatus.value['空闲'] ?? 0) + (parkingStatus.value['已占用'] ?? 0) + (parkingStatus.value['已锁定'] ?? 0))
const complaintTotal = computed(() => (complaintCategory.value['投诉'] ?? 0) + (complaintCategory.value['建议'] ?? 0))

function barWidth(val, total) {
  if (!total) return '0%'
  return Math.round((val / total) * 100) + '%'
}

onMounted(async () => {
  const [ov, rs, bs, hs, ps, cc] = await Promise.all([
    getOverview(), getRepairStatus(), getBillStatus(), getHouseStatus(), getParkingStatus(), getComplaintCategory()
  ])
  overview.value = ov.data
  repairStatus.value = rs.data
  billStatus.value = bs.data
  houseStatus.value = hs.data
  parkingStatus.value = ps.data
  complaintCategory.value = cc.data
})
</script>

<style scoped>
.stat-card { text-align: center; }
.stat-num { font-size: 32px; font-weight: bold; }
.stat-label { font-size: 13px; color: #666; margin-top: 4px; }
.bar-group { display: flex; flex-direction: column; gap: 10px; }
.bar-row { display: flex; align-items: center; gap: 8px; }
.bar-label { width: 55px; font-size: 13px; color: #555; text-align: right; flex-shrink: 0; }
.bar-track { flex: 1; height: 14px; background: #f0f0f0; border-radius: 7px; overflow: hidden; }
.bar-fill { height: 100%; border-radius: 7px; transition: width 0.4s; }
.bar-fill.repair { background: #1890ff; }
.bar-fill.bill { background: #52c41a; }
.bar-fill.house { background: #722ed1; }
.bar-fill.parking { background: #13c2c2; }
.bar-fill.complaint { background: #eb2f96; }
.bar-val { width: 28px; font-size: 13px; text-align: right; color: #333; }
</style>
