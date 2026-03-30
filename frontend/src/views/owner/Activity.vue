<template>
  <div>
    <el-card shadow="never">
      <div class="toolbar">
        <el-select v-model="query.activityType" placeholder="全部类型" clearable style="width:120px" @change="loadData">
          <el-option value="ACTIVITY" label="社区活动" /><el-option value="CARE" label="关怀服务" />
        </el-select>
        <el-select v-model="query.status" placeholder="全部状态" clearable style="width:110px;margin-left:8px" @change="loadData">
          <el-option :value="1" label="报名中" /><el-option :value="2" label="已结束" />
        </el-select>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="title" label="活动标题" />
        <el-table-column prop="activityTypeName" label="类型" width="85" />
        <el-table-column prop="location" label="地点" width="130" show-overflow-tooltip />
        <el-table-column prop="activityTime" label="活动时间" width="155" />
        <el-table-column label="报名" width="80">
          <template #default="{ row }">
            {{ row.signupCount }}{{ row.maxParticipants ? '/' + row.maxParticipants : '' }}
          </template>
        </el-table-column>
        <el-table-column prop="statusName" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status===1 ? 'success' : 'info'">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="145" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="showDetail(row)">详情</el-button>
            <el-button link type="success" v-if="row.status===1 && !row.signedUp" @click="handleSignup(row)">报名</el-button>
            <el-button link type="warning" v-if="row.status===1 && row.signedUp" @click="handleCancelSignup(row)">取消报名</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="pagination" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize"
        :total="total" layout="total, prev, pager, next" @change="loadData" />
    </el-card>

    <el-dialog v-model="detailVisible" title="活动详情" width="540px">
      <el-descriptions :column="1" border v-if="current">
        <el-descriptions-item label="活动类型">{{ current.activityTypeName }}</el-descriptions-item>
        <el-descriptions-item label="标题">{{ current.title }}</el-descriptions-item>
        <el-descriptions-item label="时间">{{ current.activityTime }}</el-descriptions-item>
        <el-descriptions-item label="地点">{{ current.location }}</el-descriptions-item>
        <el-descriptions-item label="报名情况">{{ current.signupCount }}{{ current.maxParticipants ? '/' + current.maxParticipants + ' 人' : ' 人（不限）' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="current.status===1 ? 'success' : 'info'">{{ current.statusName }}</el-tag>
          <el-tag v-if="current.signedUp" type="success" style="margin-left:8px">已报名</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="内容">
          <div style="white-space:pre-wrap">{{ current.content }}</div>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPublishedActivities, getActivityDetail, signupActivity, cancelSignupActivity } from '@/api/activity'

const loading = ref(false), tableData = ref([]), total = ref(0)
const query = reactive({ activityType: '', status: null, pageNum: 1, pageSize: 10 })
const detailVisible = ref(false), current = ref(null)

onMounted(loadData)

async function loadData() {
  loading.value = true
  try { const r = await getPublishedActivities(query); tableData.value = r.data.records; total.value = r.data.total }
  finally { loading.value = false }
}

async function handleSignup(row) {
  await signupActivity(row.id)
  ElMessage.success('报名成功')
  loadData()
}

async function handleCancelSignup(row) {
  await cancelSignupActivity(row.id)
  ElMessage.success('已取消报名')
  loadData()
}

async function showDetail(row) {
  current.value = (await getActivityDetail(row.id)).data
  detailVisible.value = true
}
</script>
<style scoped>
.toolbar { margin-bottom:12px; }
.pagination { margin-top:16px; justify-content:flex-end; display:flex; }
</style>
