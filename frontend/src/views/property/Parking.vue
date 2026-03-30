<template>
  <div>
    <el-tabs v-model="activeTab">
      <!-- 车位管理 -->
      <el-tab-pane label="车位管理" name="spaces">
        <el-card shadow="never">
          <div class="toolbar">
            <el-form inline :model="spaceQuery">
              <el-form-item label="类型">
                <el-select v-model="spaceQuery.spaceType" placeholder="全部" clearable style="width:100px">
                  <el-option value="NORMAL" label="普通" /><el-option value="CHARGE" label="充电桩" />
                </el-select>
              </el-form-item>
              <el-form-item label="状态">
                <el-select v-model="spaceQuery.status" placeholder="全部" clearable style="width:100px">
                  <el-option :value="0" label="空闲" /><el-option :value="1" label="已占用" /><el-option :value="2" label="已锁定" />
                </el-select>
              </el-form-item>
              <el-form-item label="搜索">
                <el-input v-model="spaceQuery.keyword" placeholder="编号/位置" clearable style="width:130px" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="loadSpaces">查询</el-button>
                <el-button @click="resetSpaceQuery">重置</el-button>
              </el-form-item>
            </el-form>
            <el-button type="primary" @click="openAddSpace">新增车位</el-button>
          </div>
          <el-table :data="spaceData" v-loading="spaceLoading" stripe border>
            <el-table-column prop="spaceNo" label="车位编号" width="110" />
            <el-table-column prop="spaceTypeName" label="类型" width="80" />
            <el-table-column prop="location" label="位置" />
            <el-table-column prop="statusName" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="spaceStatusType(row.status)">{{ row.statusName }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="ownerName" label="绑定业主" width="90" />
            <el-table-column prop="remark" label="备注" show-overflow-tooltip />
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="openEditSpace(row)">编辑</el-button>
                <el-button link type="danger" @click="handleDeleteSpace(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination class="pagination" v-model:current-page="spaceQuery.pageNum" v-model:page-size="spaceQuery.pageSize"
            :total="spaceTotal" layout="total, prev, pager, next" @change="loadSpaces" />
        </el-card>
      </el-tab-pane>

      <!-- 停车记录 -->
      <el-tab-pane label="停车记录" name="records">
        <el-card shadow="never">
          <div class="toolbar">
            <el-form inline :model="recordQuery">
              <el-form-item label="车牌">
                <el-input v-model="recordQuery.carNo" placeholder="车牌号" clearable style="width:130px" />
              </el-form-item>
              <el-form-item label="缴费">
                <el-select v-model="recordQuery.payStatus" placeholder="全部" clearable style="width:90px">
                  <el-option :value="0" label="未缴费" /><el-option :value="1" label="已缴费" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="loadRecords">查询</el-button>
                <el-button @click="resetRecordQuery">重置</el-button>
              </el-form-item>
            </el-form>
            <el-button type="primary" @click="openAddRecord">新增记录</el-button>
          </div>
          <el-table :data="recordData" v-loading="recordLoading" stripe border>
            <el-table-column prop="spaceNo" label="车位" width="100" />
            <el-table-column prop="carNo" label="车牌号" width="110" />
            <el-table-column prop="ownerName" label="业主" width="90" />
            <el-table-column prop="startTime" label="进入时间" width="155" />
            <el-table-column prop="endTime" label="离开时间" width="155" />
            <el-table-column prop="feeAmount" label="费用(元)" width="90" />
            <el-table-column prop="payStatusName" label="缴费" width="80">
              <template #default="{ row }">
                <el-tag :type="row.payStatus===1 ? 'success' : 'warning'">{{ row.payStatusName }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80" fixed="right">
              <template #default="{ row }">
                <el-button link type="success" v-if="row.payStatus===0" @click="handlePay(row)">标记缴费</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination class="pagination" v-model:current-page="recordQuery.pageNum" v-model:page-size="recordQuery.pageSize"
            :total="recordTotal" layout="total, prev, pager, next" @change="loadRecords" />
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 停车记录表单 -->
    <el-dialog v-model="recordFormVisible" title="新增停车记录" width="500px">
      <el-form ref="recordFormRef" :model="recordForm" :rules="recordRules" label-width="90px">
        <el-form-item label="车位" prop="spaceId">
          <el-select v-model="recordForm.spaceId" placeholder="请选择车位" filterable style="width:100%">
            <el-option v-for="s in spaceOptions" :key="s.id" :value="s.id" :label="s.spaceNo + (s.location ? '（' + s.location + '）' : '')" />
          </el-select>
        </el-form-item>
        <el-form-item label="车牌号" prop="carNo">
          <el-input v-model="recordForm.carNo" placeholder="如：粤A12345" />
        </el-form-item>
        <el-form-item label="关联业主">
          <el-select v-model="recordForm.ownerId" placeholder="不关联" clearable filterable style="width:100%">
            <el-option v-for="o in ownerOptions" :key="o.id" :value="o.id" :label="o.realName + (o.phone ? '（' + o.phone + '）' : '')" />
          </el-select>
        </el-form-item>
        <el-form-item label="进入时间" prop="startTime">
          <el-date-picker v-model="recordForm.startTime" type="datetime" placeholder="请选择" style="width:100%" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="离开时间">
          <el-date-picker v-model="recordForm.endTime" type="datetime" placeholder="请选择（可选）" style="width:100%" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="费用(元)">
          <el-input-number v-model="recordForm.feeAmount" :min="0" :precision="2" :step="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="缴费状态">
          <el-radio-group v-model="recordForm.payStatus">
            <el-radio :value="0">未缴费</el-radio>
            <el-radio :value="1">已缴费</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="recordFormVisible=false">取消</el-button>
        <el-button type="primary" :loading="recordSaving" @click="doSaveRecord">保存</el-button>
      </template>
    </el-dialog>

    <!-- 标记缴费 -->
    <el-dialog v-model="payFormVisible" title="标记缴费" width="420px">
      <el-form ref="payFormRef" :model="payForm" :rules="payRules" label-width="90px">
        <el-form-item label="离开时间" prop="endTime">
          <el-date-picker v-model="payForm.endTime" type="datetime" placeholder="请选择" style="width:100%" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="停车费用" prop="feeAmount">
          <el-input-number v-model="payForm.feeAmount" :min="0" :precision="2" :step="1" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="payFormVisible=false">取消</el-button>
        <el-button type="primary" :loading="paying" @click="doConfirmPay">确认缴费</el-button>
      </template>
    </el-dialog>

    <!-- 车位表单 -->
    <el-dialog v-model="spaceFormVisible" :title="spaceEditId ? '编辑车位' : '新增车位'" width="480px">
      <el-form ref="spaceFormRef" :model="spaceForm" :rules="spaceRules" label-width="90px">
        <el-form-item label="车位编号" prop="spaceNo"><el-input v-model="spaceForm.spaceNo" /></el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="spaceForm.spaceType">
            <el-radio value="NORMAL">普通</el-radio><el-radio value="CHARGE">充电桩</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="位置"><el-input v-model="spaceForm.location" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="spaceForm.status" style="width:100%">
            <el-option :value="0" label="空闲" /><el-option :value="1" label="已占用" /><el-option :value="2" label="已锁定" />
          </el-select>
        </el-form-item>
        <el-form-item label="绑定业主">
          <el-select v-model="spaceForm.ownerId" placeholder="不绑定" clearable filterable style="width:100%">
            <el-option v-for="o in ownerOptions" :key="o.id" :value="o.id" :label="o.realName + (o.phone ? '（' + o.phone + '）' : '')" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="spaceForm.remark" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="spaceFormVisible=false">取消</el-button>
        <el-button type="primary" :loading="spaceSaving" @click="doSaveSpace">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getParkingSpaces, addParkingSpace, updateParkingSpace, deleteParkingSpace, getParkingRecords, addParkingRecord, payParkingRecord } from '@/api/parking'
import { getOwners } from '@/api/owner'

const activeTab = ref('spaces')

// 车位
const spaceLoading = ref(false), spaceData = ref([]), spaceTotal = ref(0)
const spaceQuery = reactive({ keyword: '', spaceType: '', status: null, pageNum: 1, pageSize: 10 })
const spaceFormVisible = ref(false), spaceSaving = ref(false), spaceFormRef = ref()
const spaceForm = reactive({ spaceNo: '', spaceType: 'NORMAL', location: '', status: 0, ownerId: null, remark: '' })
const spaceRules = { spaceNo: [{ required: true, message: '请填写车位编号' }] }
let spaceEditId = null
const ownerOptions = ref([])

// 停车记录
const recordLoading = ref(false), recordData = ref([]), recordTotal = ref(0)
const recordQuery = reactive({ carNo: '', payStatus: null, pageNum: 1, pageSize: 10 })
const recordFormVisible = ref(false), recordSaving = ref(false), recordFormRef = ref()
const recordForm = reactive({ spaceId: null, carNo: '', ownerId: null, startTime: '', endTime: '', feeAmount: null, payStatus: 0 })
const payFormVisible = ref(false), paying = ref(false), payFormRef = ref()
const payForm = reactive({ endTime: '', feeAmount: null })
const payRules = {
  endTime: [{ required: true, message: '请填写离开时间' }],
  feeAmount: [{ required: true, message: '请填写停车费用' }]
}
let payingRow = null
const recordRules = {
  spaceId: [{ required: true, message: '请选择车位' }],
  carNo: [{ required: true, message: '请填写车牌号' }],
  startTime: [{ required: true, message: '请填写进入时间' }]
}
const spaceOptions = ref([])

function spaceStatusType(s) { return s === 0 ? 'success' : s === 1 ? 'warning' : 'danger' }

onMounted(() => { loadSpaces(); loadRecords(); loadOwnerOptions(); loadSpaceOptions() })

async function loadOwnerOptions() {
  const r = await getOwners({ pageNum: 1, pageSize: 500 })
  ownerOptions.value = r.data.records || []
}

async function loadSpaceOptions() {
  const r = await getParkingSpaces({ pageNum: 1, pageSize: 500 })
  spaceOptions.value = r.data.records || []
}

async function loadSpaces() {
  spaceLoading.value = true
  try { const r = await getParkingSpaces(spaceQuery); spaceData.value = r.data.records; spaceTotal.value = r.data.total }
  finally { spaceLoading.value = false }
}
function resetSpaceQuery() { Object.assign(spaceQuery, { keyword: '', spaceType: '', status: null, pageNum: 1 }); loadSpaces() }

async function loadRecords() {
  recordLoading.value = true
  try { const r = await getParkingRecords(recordQuery); recordData.value = r.data.records; recordTotal.value = r.data.total }
  finally { recordLoading.value = false }
}
function resetRecordQuery() { Object.assign(recordQuery, { carNo: '', payStatus: null, pageNum: 1 }); loadRecords() }

function openAddRecord() {
  Object.assign(recordForm, { spaceId: null, carNo: '', ownerId: null, startTime: '', endTime: '', feeAmount: null, payStatus: 0 })
  recordFormVisible.value = true
}

watch(() => recordForm.spaceId, (spaceId) => {
  const space = spaceOptions.value.find(s => s.id === spaceId)
  recordForm.ownerId = space?.ownerId ?? null
})
async function doSaveRecord() {
  await recordFormRef.value.validate()
  recordSaving.value = true
  try {
    await addParkingRecord(recordForm)
    ElMessage.success('记录已添加')
    recordFormVisible.value = false
    loadRecords()
  } finally { recordSaving.value = false }
}

function openAddSpace() {
  spaceEditId = null
  Object.assign(spaceForm, { spaceNo: '', spaceType: 'NORMAL', location: '', status: 0, ownerId: null, remark: '' })
  spaceFormVisible.value = true
}
function openEditSpace(row) {
  spaceEditId = row.id
  Object.assign(spaceForm, { spaceNo: row.spaceNo, spaceType: row.spaceType, location: row.location || '', status: row.status ?? 0, ownerId: row.ownerId ?? null, remark: row.remark || '' })
  spaceFormVisible.value = true
}
async function doSaveSpace() {
  await spaceFormRef.value.validate(); spaceSaving.value = true
  try {
    if (spaceEditId) await updateParkingSpace(spaceEditId, spaceForm); else await addParkingSpace(spaceForm)
    ElMessage.success('保存成功'); spaceFormVisible.value = false; loadSpaces()
  } finally { spaceSaving.value = false }
}
async function handleDeleteSpace(row) {
  await ElMessageBox.confirm(`确认删除车位「${row.spaceNo}」？`, '删除确认', { type: 'error' })
  await deleteParkingSpace(row.id); ElMessage.success('已删除'); loadSpaces()
}
function handlePay(row) {
  payingRow = row
  Object.assign(payForm, { endTime: row.endTime || '', feeAmount: row.feeAmount ?? null })
  payFormVisible.value = true
}
async function doConfirmPay() {
  await payFormRef.value.validate()
  paying.value = true
  try {
    await payParkingRecord(payingRow.id, payForm)
    ElMessage.success('已标记缴费')
    payFormVisible.value = false
    loadRecords()
  } finally { paying.value = false }
}
</script>
<style scoped>
.toolbar { display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:12px; }
.pagination { margin-top:16px; justify-content:flex-end; display:flex; }
</style>
