<template>
  <div>
    <el-card shadow="never">
      <div class="toolbar">
        <el-select v-model="query.category" placeholder="全部类型" clearable style="width:120px" @change="loadData">
          <el-option value="COMPLAINT" label="投诉" />
          <el-option value="SUGGESTION" label="建议" />
        </el-select>
        <el-button type="primary" @click="openSubmit">提交投诉/建议</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="categoryName" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.category === 'COMPLAINT' ? 'danger' : 'warning'">{{ row.categoryName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="statusName" label="状态" width="85">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="155" />
        <el-table-column prop="replyTime" label="回复时间" width="155" />
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="showDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination class="pagination" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize"
        :total="total" layout="total, prev, pager, next" @change="loadData" />
    </el-card>

    <!-- 提交对话框 -->
    <el-dialog v-model="submitVisible" title="提交投诉/建议" width="520px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="类型">
          <el-radio-group v-model="form.category">
            <el-radio value="COMPLAINT">投诉</el-radio>
            <el-radio value="SUGGESTION">建议</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请填写标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请详细描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="submitVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="doSubmit">提交</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="详情" width="540px">
      <el-descriptions :column="1" border v-if="current">
        <el-descriptions-item label="类型">
          <el-tag :type="current.category === 'COMPLAINT' ? 'danger' : 'warning'">{{ current.categoryName }}</el-tag>
        </el-descriptions-item>
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
        <el-descriptions-item v-else label="回复">
          <span style="color:#999">暂无回复</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyComplaints, submitComplaint, getComplaintDetail } from '@/api/complaint'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ category: '', pageNum: 1, pageSize: 10 })

const submitVisible = ref(false)
const submitting = ref(false)
const formRef = ref()
const form = reactive({ category: 'COMPLAINT', title: '', content: '' })
const formRules = {
  title: [{ required: true, message: '请填写标题' }],
  content: [{ required: true, message: '请填写内容' }]
}

const detailVisible = ref(false)
const current = ref(null)

function statusTagType(s) {
  return s === 0 ? 'warning' : s === 1 ? 'primary' : 'success'
}

onMounted(loadData)

async function loadData() {
  loading.value = true
  try {
    const res = await getMyComplaints(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function openSubmit() {
  Object.assign(form, { category: 'COMPLAINT', title: '', content: '' })
  submitVisible.value = true
}

async function doSubmit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    await submitComplaint(form)
    ElMessage.success('提交成功')
    submitVisible.value = false
    loadData()
  } finally { submitting.value = false }
}

async function showDetail(row) {
  current.value = (await getComplaintDetail(row.id)).data
  detailVisible.value = true
}
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; margin-bottom: 12px; }
.pagination { margin-top: 16px; justify-content: flex-end; display: flex; }
</style>
