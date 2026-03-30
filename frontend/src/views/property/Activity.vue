<template>
  <div>
    <el-card shadow="never">
      <div class="toolbar">
        <el-form inline :model="query" @submit.prevent="loadData">
          <el-form-item label="类型">
            <el-select v-model="query.activityType" placeholder="全部" clearable style="width:110px">
              <el-option value="ACTIVITY" label="社区活动" /><el-option value="CARE" label="关怀服务" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="query.status" placeholder="全部" clearable style="width:100px">
              <el-option :value="1" label="报名中" /><el-option :value="2" label="已结束" /><el-option :value="0" label="已取消" />
            </el-select>
          </el-form-item>
          <el-form-item label="关键词">
            <el-input v-model="query.keyword" placeholder="标题/地点" clearable style="width:140px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadData">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
        <el-button type="primary" @click="openAdd">新增活动</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="activityTypeName" label="类型" width="85" />
        <el-table-column prop="location" label="地点" width="120" show-overflow-tooltip />
        <el-table-column prop="activityTime" label="活动时间" width="155" />
        <el-table-column label="报名" width="80">
          <template #default="{ row }">
            {{ row.signupCount }}{{ row.maxParticipants ? '/' + row.maxParticipants : '' }}
          </template>
        </el-table-column>
        <el-table-column prop="statusName" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="actStatusType(row.status)">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="230" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="showSignups(row)">报名名单</el-button>
            <el-button link type="warning" v-if="row.status===1" @click="openEdit(row)">编辑</el-button>
            <el-button link type="success" v-if="row.status===1" @click="handleFinish(row)">结束</el-button>
            <el-button link type="danger" v-if="row.status===1" @click="handleCancel(row)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="pagination" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize"
        :total="total" layout="total, prev, pager, next" @change="loadData" />
    </el-card>

    <!-- 新增/编辑 -->
    <el-dialog v-model="formVisible" :title="editId ? '编辑活动' : '新增活动'" width="560px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="90px">
        <el-form-item label="标题" prop="title"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.activityType" style="width:100%">
            <el-option value="ACTIVITY" label="社区活动" /><el-option value="CARE" label="关怀服务" />
          </el-select>
        </el-form-item>
        <el-form-item label="活动时间">
          <el-date-picker v-model="form.activityTime" type="datetime" style="width:100%" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="地点"><el-input v-model="form.location" /></el-form-item>
        <el-form-item label="人数上限">
          <el-input-number v-model="form.maxParticipants" :min="1" placeholder="不填则不限" style="width:100%" :controls="false" />
        </el-form-item>
        <el-form-item label="活动内容">
          <el-input v-model="form.content" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible=false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="doSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 报名名单 -->
    <el-dialog v-model="signupVisible" title="报名名单" width="540px">
      <el-table :data="signupList" border stripe>
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="ownerName" label="姓名" width="90" />
        <el-table-column prop="ownerPhone" label="手机" />
        <el-table-column prop="signupTime" label="报名时间" width="155" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getActivities, addActivity, updateActivity, cancelActivity, finishActivity, getActivitySignups } from '@/api/activity'

const loading = ref(false), tableData = ref([]), total = ref(0)
const query = reactive({ keyword: '', activityType: '', status: null, pageNum: 1, pageSize: 10 })
const formVisible = ref(false), saving = ref(false), formRef = ref()
const form = reactive({ title: '', activityType: 'ACTIVITY', activityTime: '', location: '', maxParticipants: null, content: '' })
const formRules = { title: [{ required: true, message: '请填写标题' }] }
let editId = null
const signupVisible = ref(false), signupList = ref([])

function actStatusType(s) { return s === 1 ? 'success' : s === 0 ? 'info' : 'warning' }

onMounted(loadData)

async function loadData() {
  loading.value = true
  try { const r = await getActivities(query); tableData.value = r.data.records; total.value = r.data.total }
  finally { loading.value = false }
}
function resetQuery() { Object.assign(query, { keyword: '', activityType: '', status: null, pageNum: 1 }); loadData() }
function openAdd() {
  editId = null
  Object.assign(form, { title: '', activityType: 'ACTIVITY', activityTime: '', location: '', maxParticipants: null, content: '' })
  formVisible.value = true
}
function openEdit(row) {
  editId = row.id
  Object.assign(form, { title: row.title, activityType: row.activityType, activityTime: row.activityTime || '',
    location: row.location || '', maxParticipants: row.maxParticipants || null, content: row.content || '' })
  formVisible.value = true
}
async function doSave() {
  await formRef.value.validate(); saving.value = true
  try {
    if (editId) await updateActivity(editId, form); else await addActivity(form)
    ElMessage.success('保存成功'); formVisible.value = false; loadData()
  } finally { saving.value = false }
}
async function handleFinish(row) {
  await ElMessageBox.confirm(`确认结束活动「${row.title}」？`, '确认', { type: 'warning' })
  await finishActivity(row.id); ElMessage.success('已结束'); loadData()
}
async function handleCancel(row) {
  await ElMessageBox.confirm(`确认取消活动「${row.title}」？`, '确认', { type: 'error' })
  await cancelActivity(row.id); ElMessage.success('已取消'); loadData()
}
async function showSignups(row) {
  const r = await getActivitySignups(row.id); signupList.value = r.data; signupVisible.value = true
}
</script>
<style scoped>
.toolbar { display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:12px; }
.pagination { margin-top:16px; justify-content:flex-end; display:flex; }
</style>
