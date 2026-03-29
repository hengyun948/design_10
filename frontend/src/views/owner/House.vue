<template>
  <div>
    <el-row :gutter="16" v-if="houses.length">
      <el-col :span="8" v-for="h in houses" :key="h.houseId" style="margin-bottom:16px">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="addr">{{ h.fullAddress }}</span>
              <el-tag size="small" :type="['info','success','warning'][h.houseStatus ?? 0]">{{ h.houseStatusName }}</el-tag>
            </div>
          </template>
          <el-descriptions :column="1" size="small">
            <el-descriptions-item label="建筑面积">{{ h.area ? h.area + ' ㎡' : '—' }}</el-descriptions-item>
            <el-descriptions-item label="户型">{{ h.houseType || '—' }}</el-descriptions-item>
            <el-descriptions-item label="绑定类型">{{ h.bindTypeName }}</el-descriptions-item>
            <el-descriptions-item label="开始日期">{{ h.startDate || '—' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
    <el-card shadow="never" v-else>
      <el-empty description="暂无绑定房屋，请联系物业工作人员" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyOwnerInfo, getOwnerHouses } from '@/api/owner'

const houses = ref([])

onMounted(async () => {
  try {
    const infoRes = await getMyOwnerInfo()
    if (infoRes.data?.id) {
      const res = await getOwnerHouses(infoRes.data.id)
      houses.value = res.data || []
    }
  } catch {}
})
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.addr { font-weight: bold; font-size: 15px; }
</style>
