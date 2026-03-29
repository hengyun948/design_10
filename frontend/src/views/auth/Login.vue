<template>
  <div class="login-page">
    <div class="login-box">
      <div class="login-title">
        <h2>智慧物业服务平台</h2>
        <p>请登录您的账号</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" size="large">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :prefix-icon="User" clearable />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" show-password clearable @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">登 录</el-button>
        </el-form-item>
      </el-form>
      <div class="login-hint">
        <el-text size="small" type="info">演示账号：admin / property1 / worker1 / owner1，密码：123456</el-text>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const HOME_MAP = {
  ADMIN: '/admin/home',
  PROPERTY_STAFF: '/property/home',
  REPAIR_WORKER: '/worker/home',
  OWNER: '/owner/home'
}

async function handleLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    const data = await userStore.login(form)
    ElMessage.success(`欢迎回来，${data.realName}`)
    router.push(HOME_MAP[data.roleCode] || '/login')
  } catch {
    // 错误已在 request.js 中统一处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a3a5c 0%, #2d6a9f 50%, #1a3a5c 100%);
}
.login-box {
  width: 420px;
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}
.login-title { text-align: center; margin-bottom: 32px; }
.login-title h2 { font-size: 24px; color: #1a3a5c; margin: 0 0 8px; }
.login-title p { color: #999; font-size: 14px; margin: 0; }
.login-btn { width: 100%; }
.login-hint { text-align: center; margin-top: 12px; }
</style>
