<template>
  <div>
    <el-card shadow="never">
      <div class="toolbar">
        <el-form inline :model="query" @submit.prevent="loadData">
          <el-form-item label="楼栋">
            <el-input v-model="query.buildingNo" placeholder="楼栋号" clearable style="width:100px" />
          </el-form-item>
          <el-form-item label="单元">
            <el-input v-model="query.unitNo" placeholder="单元号" clearable style="width:100px" />
          </el-form-item>
          <el-form-item label="房间">
            <el-input v-model="query.roomNo" placeholder="房间号" clearable style="width:100px" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="query.houseStatus" placeholder="全部" clearable style="width:100px">
              <el-option label="待售" :value="0" />
              <el-option label="已售" :value="1" />
              <el-option label="已租" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadData">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
        <el-button type="primary" :icon="Plus" @click="openAdd">新增房屋</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="buildingNo" label="楼栋" width="80" />
        <el-table-column prop="unitNo" label="单元" width="80" />
        <el-table-column prop="roomNo" label="房间号" width="90" />
        <el-table-column prop="fullAddress" label="完整地址" />
        <el-table-column prop="area" label="面积(㎡)" width="90" />
        <el-table-column prop="houseType" label="户型" width="90" />
        <el-table-column prop="houseStatusName" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="['info','success','warning'][row.houseStatus ?? 0]">{{ row.houseStatusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="155" />
        <el-table-column label="操作" width="130" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination class="pagination" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize"
        :total="total" layout="total, prev, pager, next" @change="loadData" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑房屋' : '新增房屋'" width="460px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="楼栋号" prop="buildingNo">
          <el-input v-model="form.buildingNo" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="单元号" prop="unitNo">
          <el-input v-model="form.unitNo" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="房间号" prop="roomNo">
          <el-input v-model="form.roomNo" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="建筑面积">
          <el-input-number v-model="form.area" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="户型">
          <el-input v-model="form.houseType" placeholder="如：3室2厅" />
        </el-form-item>
        <el-form-item label="状态" v-if="isEdit">
          <el-select v-model="form.houseStatus" style="width:100%">
            <el-option label="待售" :value="0" />
            <el-option label="已售" :value="1" />
            <el-option label="已租" :value="2" />
          </el-select>
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
import { getHouses, addHouse, updateHouse, deleteHouse } from '@/api/house'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const tableData = ref([])
const total = ref(0)
const formRef = ref()

const query = reactive({ buildingNo: '', unitNo: '', roomNo: '', houseStatus: null, pageNum: 1, pageSize: 10 })
const form = reactive({ id: null, buildingNo: '', unitNo: '', roomNo: '', area: null, houseType: '', houseStatus: 0, remark: '' })
const rules = {
  buildingNo: [{ required: true, message: '请输入楼栋号' }],
  unitNo: [{ required: true, message: '请输入单元号' }],
  roomNo: [{ required: true, message: '请输入房间号' }]
}

onMounted(loadData)

async function loadData() {
  loading.value = true
  try {
    const res = await getHouses(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function resetQuery() {
  Object.assign(query, { buildingNo: '', unitNo: '', roomNo: '', houseStatus: null, pageNum: 1 })
  loadData()
}

function openAdd() { isEdit.value = false; dialogVisible.value = true }

function openEdit(row) {
  isEdit.value = true
  Object.assign(form, { id: row.id, buildingNo: row.buildingNo, unitNo: row.unitNo, roomNo: row.roomNo,
    area: row.area, houseType: row.houseType, houseStatus: row.houseStatus, remark: row.remark })
  dialogVisible.value = true
}

function resetForm() {
  Object.assign(form, { id: null, buildingNo: '', unitNo: '', roomNo: '', area: null, houseType: '', houseStatus: 0, remark: '' })
  formRef.value?.clearValidate()
}

async function handleSubmit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    isEdit.value ? await updateHouse(form.id, form) : await addHouse(form)
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } finally { submitting.value = false }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确认删除 ${row.fullAddress}？`, '提示', { type: 'warning' })
  await deleteHouse(row.id)
  ElMessage.success('删除成功')
  loadData()
}
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px; }
.pagination { margin-top: 16px; justify-content: flex-end; display: flex; }
</style>
