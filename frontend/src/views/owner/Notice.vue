<template>
  <div>
    <el-card shadow="never">
      <div class="toolbar">
        <el-form inline :model="query">
          <el-form-item label="类型">
            <el-select v-model="query.noticeType" placeholder="全部" clearable style="width:100px" @change="loadData">
              <el-option v-for="t in typeOptions" :key="t.value" :label="t.label" :value="t.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="关键词">
            <el-input v-model="query.keyword" placeholder="标题/内容" clearable style="width:160px"
              @keyup.enter="loadData" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadData">查询</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border @row-click="showDetail">
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="noticeTypeName" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="typeTagType(row.noticeType)">{{ row.noticeTypeName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publisherName" label="发布人" width="90" />
        <el-table-column prop="publishTime" label="发布时间" width="155" />
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click.stop="showDetail(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination class="pagination" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize"
        :total="total" layout="total, prev, pager, next" @change="loadData" />
    </el-card>

    <el-dialog v-model="detailVisible" title="公告详情" width="560px">
      <el-descriptions :column="1" border v-if="current">
        <el-descriptions-item label="标题">{{ current.title }}</el-descriptions-item>
        <el-descriptions-item label="类型">
          <el-tag :type="typeTagType(current.noticeType)">{{ current.noticeTypeName }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发布人">{{ current.publisherName }}</el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ current.publishTime }}</el-descriptions-item>
        <el-descriptions-item label="内容">
          <div style="white-space:pre-wrap;line-height:1.8">{{ current.content }}</div>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getPublishedNotices, getNoticeDetail } from '@/api/notice'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ keyword: '', noticeType: '', pageNum: 1, pageSize: 10 })
const detailVisible = ref(false)
const current = ref(null)

const typeOptions = [
  { value: 'NOTICE', label: '通知' },
  { value: 'ACTIVITY', label: '活动' },
  { value: 'URGENCY', label: '紧急' }
]

function typeTagType(t) {
  return t === 'URGENCY' ? 'danger' : t === 'ACTIVITY' ? 'warning' : 'primary'
}

onMounted(loadData)

async function loadData() {
  loading.value = true
  try {
    const res = await getPublishedNotices(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

async function showDetail(row) {
  current.value = (await getNoticeDetail(row.id)).data
  detailVisible.value = true
}
</script>

<style scoped>
.toolbar { margin-bottom: 12px; }
.pagination { margin-top: 16px; justify-content: flex-end; display: flex; }
</style>
