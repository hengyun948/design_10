<template>
  <div>
    <el-card shadow="never">
      <div class="toolbar">
        <el-form inline :model="query" @submit.prevent="loadData">
          <el-form-item label="关键词">
            <el-input v-model="query.keyword" placeholder="姓名/手机/用户名" clearable style="width:180px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadData">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="username" label="用户名" width="110" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="genderName" label="性别" width="70" />
        <el-table-column prop="idCard" label="身份证号" width="180" />
        <el-table-column prop="address" label="联系地址" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="155" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="showHouses(row)">房屋</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination class="pagination" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize"
        :total="total" layout="total, prev, pager, next" @change="loadData" />
    </el-card>

    <!-- 业主房屋绑定对话框 -->
    <el-dialog v-model="houseDialogVisible" :title="`${currentOwner?.realName} 的房屋`" width="700px">
      <div style="margin-bottom:12px">
        <el-button type="primary" :icon="Plus" @click="openBindDialog">绑定房屋</el-button>
      </div>
      <el-table :data="ownerHouses" stripe border>
        <el-table-column prop="fullAddress" label="房屋地址" />
        <el-table-column prop="area" label="面积(㎡)" width="90" />
        <el-table-column prop="bindTypeName" label="类型" width="80" />
        <el-table-column prop="startDate" label="开始日期" width="110" />
        <el-table-column prop="endDate" label="结束日期" width="110" />
        <el-table-column label="操作" width="80">
          <template #default="{ row }">
            <el-button link type="danger" @click="handleUnbind(row)">解绑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 绑定房屋对话框 -->
    <el-dialog v-model="bindDialogVisible" title="绑定房屋" width="420px" @close="resetBindForm">
      <el-form ref="bindFormRef" :model="bindForm" :rules="bindRules" label-width="80px">
        <el-form-item label="房屋" prop="houseId">
          <el-select v-model="bindForm.houseId" placeholder="请选择房屋" style="width:100%" filterable>
            <el-option v-for="h in allHouses" :key="h.id" :label="h.fullAddress" :value="h.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="绑定类型">
          <el-radio-group v-model="bindForm.bindType">
            <el-radio :value="1">业主</el-radio>
            <el-radio :value="2">租户</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker v-model="bindForm.startDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bindDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleBind">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOwners, getOwnerHouses, bindHouse, unbindHouse } from '@/api/owner'
import { getAllHouses } from '@/api/house'

const loading = ref(false)
const submitting = ref(false)
const houseDialogVisible = ref(false)
const bindDialogVisible = ref(false)
const tableData = ref([])
const total = ref(0)
const ownerHouses = ref([])
const allHouses = ref([])
const currentOwner = ref(null)
const bindFormRef = ref()

const query = reactive({ keyword: '', pageNum: 1, pageSize: 10 })
const bindForm = reactive({ ownerId: null, houseId: null, bindType: 1, startDate: null })
const bindRules = { houseId: [{ required: true, message: '请选择房屋' }] }

onMounted(loadData)

async function loadData() {
  loading.value = true
  try {
    const res = await getOwners(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function resetQuery() {
  Object.assign(query, { keyword: '', pageNum: 1 })
  loadData()
}

async function showHouses(row) {
  currentOwner.value = row
  const res = await getOwnerHouses(row.id)
  ownerHouses.value = res.data || []
  houseDialogVisible.value = true
}

async function openBindDialog() {
  const res = await getAllHouses()
  allHouses.value = res.data || []
  bindForm.ownerId = currentOwner.value.id
  bindDialogVisible.value = true
}

function resetBindForm() {
  Object.assign(bindForm, { ownerId: null, houseId: null, bindType: 1, startDate: null })
  bindFormRef.value?.clearValidate()
}

async function handleBind() {
  await bindFormRef.value.validate()
  submitting.value = true
  try {
    await bindHouse(bindForm)
    ElMessage.success('绑定成功')
    bindDialogVisible.value = false
    showHouses(currentOwner.value)
  } finally { submitting.value = false }
}

async function handleUnbind(row) {
  await ElMessageBox.confirm(`确认解绑 ${row.fullAddress}？`, '提示', { type: 'warning' })
  await unbindHouse(row.relId)
  ElMessage.success('解绑成功')
  showHouses(currentOwner.value)
}
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px; }
.pagination { margin-top: 16px; justify-content: flex-end; display: flex; }
</style>
