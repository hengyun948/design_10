<template>
  <div class="error-page">
    <el-result icon="warning" title="403" sub-title="抱歉，您没有权限访问此页面">
      <template #extra>
        <el-button type="primary" @click="$router.go(-1)">返回上一页</el-button>
      </template>
    </el-result>
  </div>
</template>

<style scoped>
.error-page { display: flex; justify-content: center; align-items: center; height: 100vh; }
</style>
