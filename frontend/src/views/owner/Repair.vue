<template>
  <div>
    <el-card shadow="never">
      <div class="toolbar">
        <el-select v-model="query.status" placeholder="全部状态" clearable style="width:120px" @change="loadData">
          <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
        <el-button type="primary" @click="openSubmit">提交报修</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="orderNo" label="工单编号" width="175" />
        <el-table-column prop="title" label="报修标题" />
        <el-table-column prop="houseAddress" label="房屋" width="160" />
        <el-table-column prop="repairTypeName" label="类型" width="75" />
        <el-table-column prop="priorityName" label="优先级" width="75" />
        <el-table-column prop="statusName" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="155" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="showDetail(row)">详情</el-button>
            <el-button link type="danger" v-if="row.status < 4" @click="handleCancel(row)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination class="pagination" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize"
        :total="total" layout="total, prev, pager, next" @change="loadData" />
    </el-card>

    <!-- 提交报修对话框 -->
    <el-dialog v-model="submitVisible" title="提交报修" width="540px">
      <el-form ref="submitFormRef" :model="submitForm" :rules="submitRules" label-width="90px">
        <el-form-item label="报修房屋" prop="houseId">
          <el-select v-model="submitForm.houseId" placeholder="请选择房屋" style="width:100%">
            <el-option v-for="h in myHouses" :key="h.houseId" :label="h.fullAddress" :value="h.houseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="报修标题" prop="title">
          <el-input v-model="submitForm.title" placeholder="简要描述问题" />
        </el-form-item>
        <el-form-item label="问题描述" prop="content">
          <el-input v-model="submitForm.content" type="textarea" :rows="3" placeholder="详细描述问题情况" />
        </el-form-item>
        <el-form-item label="报修类型">
          <el-select v-model="submitForm.repairType" placeholder="请选择" clearable style="width:100%">
            <el-option v-for="t in repairTypes" :key="t.value" :label="t.label" :value="t.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-radio-group v-model="submitForm.priority">
            <el-radio :value="1">紧急</el-radio>
            <el-radio :value="2">普通</el-radio>
            <el-radio :value="3">低</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="submitVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="doSubmit">提交</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="工单详情" width="600px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="工单编号" :span="2">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="报修标题" :span="2">{{ currentOrder.title }}</el-descriptions-item>
        <el-descriptions-item label="房屋">{{ currentOrder.houseAddress }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ currentOrder.repairTypeName }}</el-descriptions-item>
        <el-descriptions-item label="优先级">{{ currentOrder.priorityName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(currentOrder.status)">{{ currentOrder.statusName }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="维修人员">{{ currentOrder.workerName || '待派单' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentOrder.workerPhone || '—' }}</el-descriptions-item>
        <el-descriptions-item label="问题描述" :span="2">{{ currentOrder.content }}</el-descriptions-item>
      </el-descriptions>

      <div style="margin-top:16px">
        <div style="font-weight:600;margin-bottom:8px">处理进度</div>
        <el-timeline>
          <el-timeline-item v-for="r in processRecords" :key="r.id"
            :timestamp="r.recordTime" placement="top">
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
import { getMyRepairs, submitRepair, cancelRepair, getRepairDetail, getRepairRecords } from '@/api/repair'
import { getMyOwnerInfo, getOwnerHouses } from '@/api/owner'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ status: null, pageNum: 1, pageSize: 10 })

const submitVisible = ref(false)
const submitting = ref(false)
const submitFormRef = ref()
const submitForm = reactive({ houseId: null, title: '', content: '', repairType: '', priority: 2 })
const submitRules = {
  houseId: [{ required: true, message: '请选择房屋' }],
  title: [{ required: true, message: '请填写报修标题' }],
  content: [{ required: true, message: '请描述问题' }]
}

const detailVisible = ref(false)
const currentOrder = ref(null)
const processRecords = ref([])
const myHouses = ref([])

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
    const infoRes = await getMyOwnerInfo()
    if (infoRes.data?.id) {
      const hRes = await getOwnerHouses(infoRes.data.id)
      myHouses.value = hRes.data || []
    }
  } catch {}
})

async function loadData() {
  loading.value = true
  try {
    const res = await getMyRepairs(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function openSubmit() {
  Object.assign(submitForm, { houseId: null, title: '', content: '', repairType: '', priority: 2 })
  submitVisible.value = true
}

async function doSubmit() {
  await submitFormRef.value.validate()
  submitting.value = true
  try {
    await submitRepair(submitForm)
    ElMessage.success('报修提交成功')
    submitVisible.value = false
    loadData()
  } finally { submitting.value = false }
}

async function showDetail(row) {
  currentOrder.value = (await getRepairDetail(row.id)).data
  processRecords.value = (await getRepairRecords(row.id)).data || []
  detailVisible.value = true
}

async function handleCancel(row) {
  const { value: reason } = await ElMessageBox.prompt('取消原因（可留空）', '确认取消工单', {
    confirmButtonText: '确认取消', cancelButtonText: '保留',
    inputPlaceholder: '请输入取消原因'
  }).catch(() => ({ value: null }))
  if (reason === null) return
  await cancelRepair(row.id, { remark: reason })
  ElMessage.success('已取消')
  loadData()
}
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; margin-bottom: 12px; }
.pagination { margin-top: 16px; justify-content: flex-end; display: flex; }
</style>
