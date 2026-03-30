<template>
  <div>
    <el-card shadow="never">
      <div class="toolbar">
        <el-form inline :model="query" @submit.prevent="loadData">
          <el-form-item label="类型">
            <el-select v-model="query.category" placeholder="全部" clearable style="width:100px">
              <el-option value="COMPLAINT" label="投诉" />
              <el-option value="SUGGESTION" label="建议" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="query.status" placeholder="全部" clearable style="width:100px">
              <el-option :value="0" label="待处理" /><el-option :value="1" label="处理中" /><el-option :value="2" label="已处理" />
            </el-select>
          </el-form-item>
          <el-form-item label="关键词">
            <el-input v-model="query.keyword" placeholder="标题/内容" clearable style="width:150px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadData">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="ownerName" label="业主" width="80" />
        <el-table-column prop="categoryName" label="类型" width="75">
          <template #default="{ row }">
            <el-tag :type="row.category === 'COMPLAINT' ? 'danger' : 'warning'">{{ row.categoryName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="statusName" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="155" />
        <el-table-column prop="replyByName" label="回复人" width="90" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="showDetail(row)">详情</el-button>
            <el-button link type="success" v-if="row.status === 0" @click="handleAccept(row)">受理</el-button>
            <el-button link type="warning" v-if="row.status < 2" @click="openReply(row)">回复</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination class="pagination" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize"
        :total="total" layout="total, prev, pager, next" @change="loadData" />
    </el-card>

    <!-- 回复对话框 -->
    <el-dialog v-model="replyVisible" title="回复" width="480px">
      <el-form ref="replyFormRef" :model="replyForm" :rules="replyRules" label-width="80px">
        <el-form-item label="回复内容" prop="replyContent">
          <el-input v-model="replyForm.replyContent" type="textarea" :rows="4" placeholder="请填写回复内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="replyVisible = false">取消</el-button>
        <el-button type="primary" :loading="replying" @click="doReply">提交回复</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="投诉建议详情" width="560px">
      <el-descriptions :column="1" border v-if="current">
        <el-descriptions-item label="类型">
          <el-tag :type="current.category === 'COMPLAINT' ? 'danger' : 'warning'">{{ current.categoryName }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="业主">{{ current.ownerName }}（{{ current.ownerPhone }}）</el-descriptions-item>
        <el-descriptions-item label="标题">{{ current.title }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(current.status)">{{ current.statusName }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="内容">
          <div style="white-space:pre-wrap">{{ current.content }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ current.createTime }}</el-descriptions-item>
        <template v-if="current.replyContent">
          <el-descriptions-item label="回复">
            <div style="white-space:pre-wrap;color:#67c23a">{{ current.replyContent }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="回复人">{{ current.replyByName }}</el-descriptions-item>
          <el-descriptions-item label="回复时间">{{ current.replyTime }}</el-descriptions-item>
        </template>
        <el-descriptions-item v-else label="回复"><span style="color:#999">暂无回复</span></el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getComplaints, handleComplaint, replyComplaint, deleteComplaint, getComplaintDetail } from '@/api/complaint'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ keyword: '', category: '', status: null, pageNum: 1, pageSize: 10 })

const replyVisible = ref(false)
const replying = ref(false)
const replyFormRef = ref()
const replyForm = reactive({ replyContent: '' })
const replyRules = { replyContent: [{ required: true, message: '请填写回复内容' }] }
let replyId = null

const detailVisible = ref(false)
const current = ref(null)

function statusTagType(s) {
  return s === 0 ? 'warning' : s === 1 ? 'primary' : 'success'
}

onMounted(loadData)

async function loadData() {
  loading.value = true
  try {
    const res = await getComplaints(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function resetQuery() {
  Object.assign(query, { keyword: '', category: '', status: null, pageNum: 1 })
  loadData()
}

async function handleAccept(row) {
  await handleComplaint(row.id)
  ElMessage.success('已受理')
  loadData()
}

function openReply(row) {
  replyId = row.id
  replyForm.replyContent = ''
  replyVisible.value = true
}

async function doReply() {
  await replyFormRef.value.validate()
  replying.value = true
  try {
    await replyComplaint(replyId, { replyContent: replyForm.replyContent })
    ElMessage.success('回复成功')
    replyVisible.value = false
    loadData()
  } finally { replying.value = false }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确认删除该记录？', '删除确认', { type: 'error' })
  await deleteComplaint(row.id)
  ElMessage.success('已删除')
  loadData()
}

async function showDetail(row) {
  current.value = (await getComplaintDetail(row.id)).data
  detailVisible.value = true
}
</script>

<style scoped>
.toolbar { margin-bottom: 12px; }
.pagination { margin-top: 16px; justify-content: flex-end; display: flex; }
</style>
