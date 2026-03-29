<template>
  <div>
    <el-card shadow="never">
      <div class="toolbar">
        <el-select v-model="query.status" placeholder="全部状态" clearable style="width:130px" @change="loadData">
          <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="orderNo" label="工单编号" width="175" />
        <el-table-column prop="title" label="报修标题" />
        <el-table-column prop="ownerName" label="业主" width="80" />
        <el-table-column prop="houseAddress" label="房屋" width="155" />
        <el-table-column prop="repairTypeName" label="类型" width="70" />
        <el-table-column prop="priorityName" label="优先级" width="70">
          <template #default="{ row }">
            <el-tag :type="row.priority === 1 ? 'danger' : row.priority === 3 ? 'info' : ''">{{ row.priorityName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="statusName" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assignTime" label="派单时间" width="155" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="showDetail(row)">详情</el-button>
            <el-button link type="success" v-if="row.status === 2" @click="handleReceive(row)">接单</el-button>
            <el-button link type="warning" v-if="row.status === 3" @click="openFinish(row)">完工</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination class="pagination" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize"
        :total="total" layout="total, prev, pager, next" @change="loadData" />
    </el-card>

    <!-- 完工对话框 -->
    <el-dialog v-model="finishVisible" title="填写完工说明" width="420px">
      <el-form ref="finishFormRef" :model="finishForm" label-width="90px">
        <el-form-item label="完工说明">
          <el-input v-model="finishForm.remark" type="textarea" :rows="3" placeholder="请描述维修情况（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="finishVisible = false">取消</el-button>
        <el-button type="primary" :loading="finishing" @click="doFinish">确认完工</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="工单详情" width="600px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="工单编号" :span="2">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="报修标题" :span="2">{{ currentOrder.title }}</el-descriptions-item>
        <el-descriptions-item label="业主">{{ currentOrder.ownerName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentOrder.ownerPhone }}</el-descriptions-item>
        <el-descriptions-item label="房屋">{{ currentOrder.houseAddress }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ currentOrder.repairTypeName }}</el-descriptions-item>
        <el-descriptions-item label="优先级">{{ currentOrder.priorityName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(currentOrder.status)">{{ currentOrder.statusName }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="问题描述" :span="2">{{ currentOrder.content }}</el-descriptions-item>
      </el-descriptions>

      <div style="margin-top:16px">
        <div style="font-weight:600;margin-bottom:8px">处理进度</div>
        <el-timeline>
          <el-timeline-item v-for="r in processRecords" :key="r.id" :timestamp="r.recordTime" placement="top">
            <el-tag size="small" style="margin-right:6px">{{ r.actionTypeName }}</el-tag>
            {{ r.actionContent }}
            <span style="color:#999;font-size:12px;margin-left:8px">— {{ r.operatorName }}</span>
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAssignedRepairs, receiveRepair, finishRepair, getRepairDetail, getRepairRecords } from '@/api/repair'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ status: null, pageNum: 1, pageSize: 10 })

const finishVisible = ref(false)
const finishing = ref(false)
const finishFormRef = ref()
const finishForm = reactive({ remark: '' })
let finishOrderId = null

const detailVisible = ref(false)
const currentOrder = ref(null)
const processRecords = ref([])

const statusOptions = [
  { value: 2, label: '已分派（待接单）' },
  { value: 3, label: '处理中' },
  { value: 4, label: '已完成' },
  { value: 5, label: '已取消' }
]

function statusTagType(s) {
  return ['warning', 'primary', 'primary', '', 'success', 'info'][s ?? 0]
}

onMounted(loadData)

async function loadData() {
  loading.value = true
  try {
    const res = await getAssignedRepairs(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

async function handleReceive(row) {
  await ElMessageBox.confirm(`确认接单「${row.title}」，开始处理？`, '接单确认', { type: 'info' })
  await receiveRepair(row.id)
  ElMessage.success('已接单，请尽快处理')
  loadData()
}

function openFinish(row) {
  finishOrderId = row.id
  finishForm.remark = ''
  finishVisible.value = true
}

async function doFinish() {
  finishing.value = true
  try {
    await finishRepair(finishOrderId, { remark: finishForm.remark })
    ElMessage.success('工单已完工')
    finishVisible.value = false
    loadData()
  } finally { finishing.value = false }
}

async function showDetail(row) {
  currentOrder.value = (await getRepairDetail(row.id)).data
  processRecords.value = (await getRepairRecords(row.id)).data || []
  detailVisible.value = true
}
</script>

<style scoped>
.toolbar { margin-bottom: 12px; }
.pagination { margin-top: 16px; justify-content: flex-end; display: flex; }
</style>
