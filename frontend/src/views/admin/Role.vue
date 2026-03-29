<template>
  <el-card shadow="never">
    <el-table :data="roles" v-loading="loading" stripe border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="roleName" label="角色名称" width="150" />
      <el-table-column prop="roleCode" label="角色编码" width="160" />
      <el-table-column prop="remark" label="备注" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getRoles } from '@/api/user'

const loading = ref(false)
const roles = ref([])

onMounted(async () => {
  loading.value = true
  try {
    const res = await getRoles()
    roles.value = res.data
  } finally {
    loading.value = false
  }
})
</script>
