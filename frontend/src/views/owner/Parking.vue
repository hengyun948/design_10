<template>
  <div>
    <el-card shadow="never">
      <div class="toolbar">
        <el-select v-model="query.payStatus" placeholder="全部缴费状态" clearable style="width:140px" @change="loadData">
          <el-option :value="0" label="未缴费" /><el-option :value="1" label="已缴费" />
        </el-select>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="spaceNo" label="车位编号" width="110" />
        <el-table-column prop="spaceLocation" label="车位位置" />
        <el-table-column prop="carNo" label="车牌号" width="120" />
        <el-table-column prop="startTime" label="进入时间" width="155" />
        <el-table-column prop="endTime" label="离开时间" width="155" />
        <el-table-column prop="feeAmount" label="费用(元)" width="90" />
        <el-table-column prop="payStatusName" label="缴费状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.payStatus === 1 ? 'success' : 'warning'">{{ row.payStatusName }}</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination class="pagination" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize"
        :total="total" layout="total, prev, pager, next" @change="loadData" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getMyParkingRecords } from '@/api/parking'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ payStatus: null, pageNum: 1, pageSize: 10 })

onMounted(loadData)

async function loadData() {
  loading.value = true
  try {
    const res = await getMyParkingRecords(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}
</script>

<style scoped>
.toolbar { margin-bottom: 12px; }
.pagination { margin-top: 16px; justify-content: flex-end; display: flex; }
</style>
