<template>
  <div>
    <el-card shadow="never">
      <div class="toolbar">
        <el-form inline :model="query" @submit.prevent="loadData">
          <el-form-item label="账期">
            <el-input v-model="query.billingPeriod" placeholder="如：2024-01" clearable style="width:120px" />
          </el-form-item>
          <el-form-item label="费用类型">
            <el-select v-model="query.billType" placeholder="全部" clearable style="width:110px">
              <el-option v-for="t in billTypes" :key="t.value" :label="t.label" :value="t.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="query.status" placeholder="全部" clearable style="width:90px">
              <el-option label="未缴" :value="0" />
              <el-option label="已缴" :value="1" />
              <el-option label="逾期" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadData">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="billNo" label="账单编号" width="160" />
        <el-table-column prop="houseAddress" label="房屋" />
        <el-table-column prop="billTypeName" label="费用类型" width="90" />
        <el-table-column prop="billingPeriod" label="账期" width="90" />
        <el-table-column prop="amountDue" label="应缴(元)" width="90" />
        <el-table-column prop="amountPaid" label="已缴(元)" width="90" />
        <el-table-column prop="dueDate" label="截止日期" width="105" />
        <el-table-column prop="statusName" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="['warning','success','danger'][row.status ?? 0]">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handlePay(row)" v-if="row.status === 0">缴费</el-button>
            <el-tag v-else-if="row.status === 1" type="success" size="small">已缴</el-tag>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyBills, payBill } from '@/api/bill'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const billTypes = [
  { value: 'PROPERTY', label: '物业费' },
  { value: 'WATER', label: '水费' },
  { value: 'ELECTRIC', label: '电费' },
  { value: 'GAS', label: '燃气费' },
  { value: 'PARKING', label: '停车费' }
]

const query = reactive({ billingPeriod: '', billType: '', status: null, pageNum: 1, pageSize: 10 })

onMounted(loadData)

async function loadData() {
  loading.value = true
  try {
    const res = await getMyBills(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function resetQuery() {
  Object.assign(query, { billingPeriod: '', billType: '', status: null, pageNum: 1 })
  loadData()
}

async function handlePay(row) {
  await ElMessageBox.confirm(
    `确认缴纳 ${row.houseAddress} ${row.billTypeName} ${row.amountDue} 元（${row.billingPeriod}）？`,
    '缴费确认', { type: 'warning', confirmButtonText: '确认缴费' }
  )
  await payBill(row.id)
  ElMessage.success('缴费成功')
  loadData()
}
</script>

<style scoped>
.toolbar { margin-bottom: 12px; }
.pagination { margin-top: 16px; justify-content: flex-end; display: flex; }
</style>
