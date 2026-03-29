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
        <el-button type="primary" :icon="Plus" @click="openAdd">新增账单</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="billNo" label="账单编号" width="160" />
        <el-table-column prop="houseAddress" label="房屋" />
        <el-table-column prop="ownerName" label="业主" width="100" />
        <el-table-column prop="billTypeName" label="费用类型" width="90" />
        <el-table-column prop="billingPeriod" label="账期" width="90" />
        <el-table-column prop="amountDue" label="应缴(元)" width="90" />
        <el-table-column prop="dueDate" label="截止日期" width="105" />
        <el-table-column prop="statusName" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="['warning','success','danger'][row.status ?? 0]">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handlePay(row)" v-if="row.status !== 1">代缴</el-button>
            <el-button link type="danger" @click="handleDelete(row)" v-if="row.status !== 1">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination class="pagination" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize"
        :total="total" layout="total, prev, pager, next" @change="loadData" />
    </el-card>

    <el-dialog v-model="dialogVisible" title="新增账单" width="500px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="房屋" prop="houseId">
          <el-select v-model="form.houseId" placeholder="请选择房屋" style="width:100%" filterable @change="onHouseChange">
            <el-option v-for="h in allHouses" :key="h.id" :label="h.fullAddress" :value="h.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="业主" prop="ownerId">
          <el-select v-model="form.ownerId" placeholder="请选择业主" style="width:100%">
            <el-option v-for="o in houseOwners" :key="o.ownerId" :label="o.realName" :value="o.ownerId" />
          </el-select>
        </el-form-item>
        <el-form-item label="费用类型" prop="billType">
          <el-select v-model="form.billType" style="width:100%">
            <el-option v-for="t in billTypes" :key="t.value" :label="t.label" :value="t.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="账期" prop="billingPeriod">
          <el-date-picker v-model="form.billingPeriod" type="month" value-format="YYYY-MM" style="width:100%" />
        </el-form-item>
        <el-form-item label="应缴金额" prop="amountDue">
          <el-input-number v-model="form.amountDue" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="截止日期">
          <el-date-picker v-model="form.dueDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBills, addBill, payBill, deleteBill } from '@/api/bill'
import { getAllHouses } from '@/api/house'
import { getOwners } from '@/api/owner'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const tableData = ref([])
const total = ref(0)
const allHouses = ref([])
const houseOwners = ref([])
const formRef = ref()

const billTypes = [
  { value: 'PROPERTY', label: '物业费' },
  { value: 'WATER', label: '水费' },
  { value: 'ELECTRIC', label: '电费' },
  { value: 'GAS', label: '燃气费' },
  { value: 'PARKING', label: '停车费' }
]

const query = reactive({ billingPeriod: '', billType: '', status: null, pageNum: 1, pageSize: 10 })
const form = reactive({ houseId: null, ownerId: null, billType: '', billingPeriod: '', amountDue: null, dueDate: null, remark: '' })
const rules = {
  houseId: [{ required: true, message: '请选择房屋' }],
  ownerId: [{ required: true, message: '请选择业主' }],
  billType: [{ required: true, message: '请选择费用类型' }],
  billingPeriod: [{ required: true, message: '请选择账期' }],
  amountDue: [{ required: true, message: '请输入应缴金额' }]
}

onMounted(async () => {
  const res = await getAllHouses()
  allHouses.value = res.data || []
  loadData()
})

async function loadData() {
  loading.value = true
  try {
    const res = await getBills(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function resetQuery() {
  Object.assign(query, { billingPeriod: '', billType: '', status: null, pageNum: 1 })
  loadData()
}

function openAdd() { dialogVisible.value = true }

function resetForm() {
  Object.assign(form, { houseId: null, ownerId: null, billType: '', billingPeriod: '', amountDue: null, dueDate: null, remark: '' })
  houseOwners.value = []
  formRef.value?.clearValidate()
}

async function onHouseChange(houseId) {
  form.ownerId = null
  houseOwners.value = []
  if (!houseId) return
  const res = await getOwners({ pageNum: 1, pageSize: 200 })
  houseOwners.value = (res.data?.records || []).map(o => ({ ownerId: o.id, realName: o.realName, phone: o.phone }))
}

async function handleSubmit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    await addBill(form)
    ElMessage.success('创建成功')
    dialogVisible.value = false
    loadData()
  } finally { submitting.value = false }
}

async function handlePay(row) {
  await ElMessageBox.confirm(`确认为 ${row.houseAddress} 代缴 ${row.billTypeName} ${row.amountDue} 元？`, '代缴确认', { type: 'warning' })
  await payBill(row.id)
  ElMessage.success('代缴成功')
  loadData()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确认删除账单 ${row.billNo}？`, '提示', { type: 'warning' })
  await deleteBill(row.id)
  ElMessage.success('删除成功')
  loadData()
}
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px; }
.pagination { margin-top: 16px; justify-content: flex-end; display: flex; }
</style>
