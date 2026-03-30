<template>
  <div>
    <el-card shadow="never">
      <div class="toolbar">
        <el-select v-model="query.result" placeholder="全部结果" clearable style="width:130px" @change="loadData">
          <el-option value="NORMAL" label="正常" />
          <el-option value="ABNORMAL" label="异常" />
        </el-select>
        <el-button type="primary" @click="openAdd">新增巡检记录</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="facilityName" label="设施名称" />
        <el-table-column prop="facilityTypeName" label="设施类型" width="80" />
        <el-table-column prop="facilityLocation" label="位置" width="160" />
        <el-table-column prop="inspectionTime" label="巡检时间" width="155" />
        <el-table-column prop="resultName" label="结果" width="75">
          <template #default="{ row }">
            <el-tag :type="row.result === 'NORMAL' ? 'success' : 'danger'">{{ row.resultName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      </el-table>

      <el-pagination class="pagination" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize"
        :total="total" layout="total, prev, pager, next" @change="loadData" />
    </el-card>

    <!-- 新增巡检记录对话框 -->
    <el-dialog v-model="addVisible" title="新增巡检记录" width="480px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="90px">
        <el-form-item label="巡检设施" prop="facilityId">
          <el-select v-model="form.facilityId" placeholder="请选择设施" style="width:100%" filterable>
            <el-option v-for="f in facilities" :key="f.id"
              :label="f.facilityName + (f.location ? '（' + f.location + '）' : '')"
              :value="f.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="巡检时间" prop="inspectionTime">
          <el-date-picker v-model="form.inspectionTime" type="datetime" placeholder="请选择"
            style="width:100%" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="巡检结果">
          <el-radio-group v-model="form.result">
            <el-radio value="NORMAL">正常</el-radio>
            <el-radio value="ABNORMAL">异常</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="异常情况请详细说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="doSave">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyInspections, addInspection, getFacilities } from '@/api/inspection'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ result: '', pageNum: 1, pageSize: 10 })

const addVisible = ref(false)
const saving = ref(false)
const formRef = ref()
const form = reactive({ facilityId: null, inspectionTime: '', result: 'NORMAL', remark: '' })
const formRules = {
  facilityId: [{ required: true, message: '请选择设施' }],
  inspectionTime: [{ required: true, message: '请选择巡检时间' }]
}
const facilities = ref([])

onMounted(async () => {
  loadData()
  try {
    const res = await getFacilities()
    facilities.value = res.data || []
  } catch {}
})

async function loadData() {
  loading.value = true
  try {
    const res = await getMyInspections(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function openAdd() {
  Object.assign(form, { facilityId: null, inspectionTime: '', result: 'NORMAL', remark: '' })
  addVisible.value = true
}

async function doSave() {
  await formRef.value.validate()
  saving.value = true
  try {
    await addInspection(form)
    ElMessage.success('巡检记录已提交')
    addVisible.value = false
    loadData()
  } finally { saving.value = false }
}
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; margin-bottom: 12px; }
.pagination { margin-top: 16px; justify-content: flex-end; display: flex; }
</style>
