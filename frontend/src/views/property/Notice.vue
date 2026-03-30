<template>
  <div>
    <el-card shadow="never">
      <div class="toolbar">
        <el-form inline :model="query" @submit.prevent="loadData">
          <el-form-item label="类型">
            <el-select v-model="query.noticeType" placeholder="全部" clearable style="width:100px">
              <el-option v-for="t in typeOptions" :key="t.value" :label="t.label" :value="t.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="query.publishStatus" placeholder="全部" clearable style="width:100px">
              <el-option :value="0" label="草稿" /><el-option :value="1" label="已发布" />
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
        <el-button type="primary" @click="openAdd">新增公告</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="noticeTypeName" label="类型" width="75" />
        <el-table-column prop="publishStatusName" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.publishStatus === 1 ? 'success' : 'info'">{{ row.publishStatusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publisherName" label="发布人" width="90" />
        <el-table-column prop="publishTime" label="发布时间" width="155" />
        <el-table-column prop="createTime" label="创建时间" width="155" />
        <el-table-column label="操作" width="210" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="showDetail(row)">详情</el-button>
            <el-button link type="warning" v-if="row.publishStatus === 0" @click="openEdit(row)">编辑</el-button>
            <el-button link type="success" v-if="row.publishStatus === 0" @click="handlePublish(row)">发布</el-button>
            <el-button link type="info" v-if="row.publishStatus === 1" @click="handleUnpublish(row)">撤回</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination class="pagination" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize"
        :total="total" layout="total, prev, pager, next" @change="loadData" />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="formVisible" :title="editId ? '编辑公告' : '新增公告'" width="580px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.noticeType" placeholder="请选择" clearable style="width:100%">
            <el-option v-for="t in typeOptions" :key="t.value" :label="t.label" :value="t.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="5" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="doSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="公告详情" width="560px">
      <el-descriptions :column="1" border v-if="current">
        <el-descriptions-item label="标题">{{ current.title }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ current.noticeTypeName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="current.publishStatus === 1 ? 'success' : 'info'">{{ current.publishStatusName }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发布人">{{ current.publisherName }}</el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ current.publishTime || '—' }}</el-descriptions-item>
        <el-descriptions-item label="内容">
          <div style="white-space:pre-wrap">{{ current.content }}</div>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getNotices, addNotice, updateNotice, publishNotice, unpublishNotice, deleteNotice, getNoticeDetail } from '@/api/notice'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ keyword: '', noticeType: '', publishStatus: null, pageNum: 1, pageSize: 10 })

const formVisible = ref(false)
const saving = ref(false)
const formRef = ref()
const form = reactive({ title: '', content: '', noticeType: '' })
const formRules = {
  title: [{ required: true, message: '请填写标题' }],
  content: [{ required: true, message: '请填写内容' }]
}
let editId = null

const detailVisible = ref(false)
const current = ref(null)

const typeOptions = [
  { value: 'NOTICE', label: '通知' },
  { value: 'ACTIVITY', label: '活动' },
  { value: 'URGENCY', label: '紧急' }
]

onMounted(loadData)

async function loadData() {
  loading.value = true
  try {
    const res = await getNotices(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function resetQuery() {
  Object.assign(query, { keyword: '', noticeType: '', publishStatus: null, pageNum: 1 })
  loadData()
}

function openAdd() {
  editId = null
  Object.assign(form, { title: '', content: '', noticeType: '' })
  formVisible.value = true
}

function openEdit(row) {
  editId = row.id
  Object.assign(form, { title: row.title, content: row.content, noticeType: row.noticeType || '' })
  formVisible.value = true
}

async function doSave() {
  await formRef.value.validate()
  saving.value = true
  try {
    if (editId) await updateNotice(editId, form)
    else await addNotice(form)
    ElMessage.success('保存成功')
    formVisible.value = false
    loadData()
  } finally { saving.value = false }
}

async function handlePublish(row) {
  await ElMessageBox.confirm(`确认发布公告「${row.title}」？`, '发布确认', { type: 'info' })
  await publishNotice(row.id)
  ElMessage.success('已发布')
  loadData()
}

async function handleUnpublish(row) {
  await ElMessageBox.confirm(`确认撤回公告「${row.title}」？`, '撤回确认', { type: 'warning' })
  await unpublishNotice(row.id)
  ElMessage.success('已撤回')
  loadData()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确认删除公告「${row.title}」？`, '删除确认', { type: 'error' })
  await deleteNotice(row.id)
  ElMessage.success('已删除')
  loadData()
}

async function showDetail(row) {
  current.value = (await getNoticeDetail(row.id)).data
  detailVisible.value = true
}
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px; }
.pagination { margin-top: 16px; justify-content: flex-end; display: flex; }
</style>
