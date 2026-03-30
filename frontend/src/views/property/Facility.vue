<template>
  <div>
    <el-card shadow="never">
      <div class="toolbar">
        <el-form inline :model="query" @submit.prevent="loadData">
          <el-form-item label="类型">
            <el-select v-model="query.facilityType" placeholder="全部" clearable style="width:100px">
              <el-option v-for="t in typeOptions" :key="t.value" :label="t.label" :value="t.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="query.status" placeholder="全部" clearable style="width:100px">
              <el-option :value="1" label="正常" /><el-option :value="0" label="故障" /><el-option :value="2" label="维护中" />
            </el-select>
          </el-form-item>
          <el-form-item label="关键词">
            <el-input v-model="query.keyword" placeholder="名称/位置" clearable style="width:140px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadData">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
        <el-button type="primary" @click="openAdd">新增设施</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="facilityName" label="设施名称" />
        <el-table-column prop="facilityType" label="类型" width="80">
          <template #default="{ row }">
            <el-tag type="info">{{ typeLabel(row.facilityType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="位置" />
        <el-table-column prop="status" label="状态" width="85">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastMaintenanceTime" label="最近维保时间" width="155" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination class="pagination" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize"
        :total="total" layout="total, prev, pager, next" @change="loadData" />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="formVisible" :title="editId ? '编辑设施' : '新增设施'" width="520px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="设施名称" prop="facilityName">
          <el-input v-model="form.facilityName" />
        </el-form-item>
        <el-form-item label="设施类型">
          <el-select v-model="form.facilityType" placeholder="请选择" clearable style="width:100%">
            <el-option v-for="t in typeOptions" :key="t.value" :label="t.label" :value="t.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="form.location" placeholder="如：1号楼地下室" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">故障</el-radio>
            <el-radio :value="2">维护中</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="最近维保时间">
          <el-date-picker v-model="form.lastMaintenanceTime" type="datetime" placeholder="请选择"
            style="width:100%" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="doSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFacilities, addFacility, updateFacility, deleteFacility } from '@/api/facility'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ keyword: '', facilityType: '', status: null, pageNum: 1, pageSize: 10 })

const formVisible = ref(false)
const saving = ref(false)
const formRef = ref()
const form = reactive({
  facilityName: '', facilityType: '', location: '',
  status: 1, lastMaintenanceTime: '', remark: ''
})
const formRules = { facilityName: [{ required: true, message: '请填写设施名称' }] }
let editId = null

const typeOptions = [
  { value: 'ELEVATOR', label: '电梯' },
  { value: 'FIRE', label: '消防' },
  { value: 'WATER', label: '水务' },
  { value: 'POWER', label: '供电' },
  { value: 'OTHER', label: '其他' }
]

function typeLabel(t) {
  return typeOptions.find(o => o.value === t)?.label || t || '—'
}
function statusLabel(s) {
  return s === 1 ? '正常' : s === 0 ? '故障' : '维护中'
}
function statusTagType(s) {
  return s === 1 ? 'success' : s === 0 ? 'danger' : 'warning'
}

onMounted(loadData)

async function loadData() {
  loading.value = true
  try {
    const res = await getFacilities(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function resetQuery() {
  Object.assign(query, { keyword: '', facilityType: '', status: null, pageNum: 1 })
  loadData()
}

function openAdd() {
  editId = null
  Object.assign(form, { facilityName: '', facilityType: '', location: '', status: 1, lastMaintenanceTime: '', remark: '' })
  formVisible.value = true
}

function openEdit(row) {
  editId = row.id
  Object.assign(form, {
    facilityName: row.facilityName,
    facilityType: row.facilityType || '',
    location: row.location || '',
    status: row.status ?? 1,
    lastMaintenanceTime: row.lastMaintenanceTime || '',
    remark: row.remark || ''
  })
  formVisible.value = true
}

async function doSave() {
  await formRef.value.validate()
  saving.value = true
  try {
    if (editId) await updateFacility(editId, form)
    else await addFacility(form)
    ElMessage.success('保存成功')
    formVisible.value = false
    loadData()
  } finally { saving.value = false }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确认删除设施「${row.facilityName}」？`, '删除确认', { type: 'error' })
  await deleteFacility(row.id)
  ElMessage.success('已删除')
  loadData()
}
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px; }
.pagination { margin-top: 16px; justify-content: flex-end; display: flex; }
</style>
