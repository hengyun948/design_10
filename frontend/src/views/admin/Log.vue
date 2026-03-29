<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-form inline :model="query" @submit.prevent="loadData">
        <el-form-item label="操作人">
          <el-input v-model="query.operatorName" placeholder="操作人姓名" clearable style="width:140px" />
        </el-form-item>
        <el-form-item label="模块">
          <el-input v-model="query.moduleName" placeholder="模块名称" clearable style="width:120px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width:100px">
            <el-option label="成功" :value="1" />
            <el-option label="失败" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="moduleName" label="模块" width="100" />
      <el-table-column prop="operationType" label="操作类型" width="100" />
      <el-table-column prop="requestUri" label="请求路径" />
      <el-table-column prop="operatorName" label="操作人" width="100" />
      <el-table-column prop="ip" label="IP地址" width="130" />
      <el-table-column prop="executeTime" label="耗时(ms)" width="100" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '成功' : '失败' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="时间" width="160" />
    </el-table>

    <el-pagination
      class="pagination"
      v-model:current-page="query.pageNum"
      v-model:page-size="query.pageSize"
      :total="total"
      layout="total, prev, pager, next"
      @change="loadData"
    />
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/api/request'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ operatorName: '', moduleName: '', status: null, pageNum: 1, pageSize: 10 })

onMounted(loadData)

async function loadData() {
  loading.value = true
  try {
    const res = await request.get('/logs/operations', { params: query })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  Object.assign(query, { operatorName: '', moduleName: '', status: null, pageNum: 1 })
  loadData()
}
</script>

<style scoped>
.toolbar { margin-bottom: 12px; }
.pagination { margin-top: 16px; justify-content: flex-end; display: flex; }
</style>
