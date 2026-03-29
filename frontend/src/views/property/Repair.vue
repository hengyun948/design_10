<template>
  <div>
    <el-card shadow="never">
      <div class="toolbar">
        <el-form inline :model="query" @submit.prevent="loadData">
          <el-form-item label="状态">
            <el-select v-model="query.status" placeholder="全部" clearable style="width:110px">
              <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="类型">
            <el-select v-model="query.repairType" placeholder="全部" clearable style="width:100px">
              <el-option v-for="t in repairTypes" :key="t.value" :label="t.label" :value="t.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="关键词">
            <el-input v-model="query.keyword" placeholder="工单号/标题" clearable style="width:160px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadData">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="orderNo" label="工单编号" width="175" />
        <el-table-column prop="title" label="报修标题" />
        <el-table-column prop="ownerName" label="业主" width="80" />
        <el-table-column prop="houseAddress" label="房屋" width="150" />
        <el-table-column prop="repairTypeName" label="类型" width="70" />
        <el-table-column prop="priorityName" label="优先级" width="70" />
        <el-table-column prop="statusName" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="workerName" label="维修人员" width="90">
          <template #default="{ row }">{{ row.workerName || '未派单' }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="155" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="showDetail(row)">详情</el-button>
            <el-button link type="success" v-if="row.status === 0" @click="handleAccept(row)">受理</el-button>
            <el-button link type="warning" v-if="row.status === 1" @click="openAssign(row)">派单</el-button>
            <el-button link type="danger" v-if="row.status < 4" @click="handleCancel(row)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination class="pagination" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize"
        :total="total" layout="total, prev, pager, next" @change="loadData" />
    </el-card>

    <!-- 派单对话框 -->
    <el-dialog v-model="assignVisible" title="派单" width="420px">
      <el-form ref="assignFormRef" :model="assignForm" :rules="assignRules" label-width="90px">
        <el-form-item label="维修人员" prop="workerId">
          <el-select v-model="assignForm.workerId" placeholder="请选择维修人员" style="width:100%" filterable>
            <el-option v-for="w in workers" :key="w.id" :label="w.realName + '（' + (w.phone || '无电话') + '）'" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="assignForm.remark" type="textarea" :rows="2" placeholder="派单备注（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" :loading="assigning" @click="doAssign">确认派单</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="工单详情" width="620px">
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
        <el-descriptions-item label="维修人员">{{ currentOrder.workerName || '待派单' }}</el-descriptions-item>
        <el-descriptions-item label="工人电话">{{ currentOrder.workerPhone || '—' }}</el-descriptions-item>
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
import { getRepairs, acceptRepair, assignRepair, cancelRepair, getRepairDetail, getRepairRecords } from '@/api/repair'
import { getUsers } from '@/api/user'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ status: null, repairType: '', keyword: '', pageNum: 1, pageSize: 10 })

const assignVisible = ref(false)
const assigning = ref(false)
const assignFormRef = ref()
const assignForm = reactive({ workerId: null, remark: '' })
const assignRules = { workerId: [{ required: true, message: '请选择维修人员' }] }
let assignOrderId = null

const detailVisible = ref(false)
const currentOrder = ref(null)
const processRecords = ref([])
const workers = ref([])

const statusOptions = [
  { value: 0, label: '待受理' }, { value: 1, label: '已受理' },
  { value: 2, label: '已分派' }, { value: 3, label: '处理中' },
  { value: 4, label: '已完成' }, { value: 5, label: '已取消' }
]
const repairTypes = [
  { value: 'WATER', label: '水管' }, { value: 'ELECTRIC', label: '电路' },
  { value: 'DOOR', label: '门窗' }, { value: 'OTHER', label: '其他' }
]

function statusTagType(s) {
  return ['warning', 'primary', 'primary', '', 'success', 'info'][s ?? 0]
}

onMounted(async () => {
  loadData()
  try {
    const res = await getUsers({ roleCode: 'REPAIR_WORKER', pageNum: 1, pageSize: 100 })
    workers.value = res.data?.records || []
  } catch {}
})

async function loadData() {
  loading.value = true
  try {
    const res = await getRepairs(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function resetQuery() {
  Object.assign(query, { status: null, repairType: '', keyword: '', pageNum: 1 })
  loadData()
}

async function handleAccept(row) {
  await ElMessageBox.confirm(`确认受理工单「${row.title}」？`, '受理确认', { type: 'info' })
  await acceptRepair(row.id)
  ElMessage.success('已受理')
  loadData()
}

function openAssign(row) {
  assignOrderId = row.id
  Object.assign(assignForm, { workerId: null, remark: '' })
  assignVisible.value = true
}

async function doAssign() {
  await assignFormRef.value.validate()
  assigning.value = true
  try {
    await assignRepair(assignOrderId, assignForm)
    ElMessage.success('派单成功')
    assignVisible.value = false
    loadData()
  } finally { assigning.value = false }
}

async function handleCancel(row) {
  const { value: reason } = await ElMessageBox.prompt('取消原因（可留空）', '确认取消', {
    confirmButtonText: '确认取消', cancelButtonText: '保留',
    inputPlaceholder: '请输入原因'
  }).catch(() => ({ value: null }))
  if (reason === null) return
  await cancelRepair(row.id, { remark: reason })
  ElMessage.success('已取消')
  loadData()
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
