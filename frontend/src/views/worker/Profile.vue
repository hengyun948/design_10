<template>
  <div>
    <el-tabs v-model="activeTab" type="border-card">
      <!-- 基本信息 -->
      <el-tab-pane label="基本信息" name="info">
        <el-form ref="infoFormRef" :model="infoForm" :rules="infoRules" label-width="90px"
                 style="max-width:460px;margin-top:12px" v-loading="loading">
          <el-form-item label="用户名">
            <el-input :value="infoForm.username" disabled />
          </el-form-item>
          <el-form-item label="真实姓名" prop="realName">
            <el-input v-model="infoForm.realName" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="infoForm.phone" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="infoForm.email" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="infoSaving" @click="saveInfo">保存信息</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <!-- 修改密码 -->
      <el-tab-pane label="修改密码" name="pwd">
        <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="100px"
                 style="max-width:440px;margin-top:12px">
          <el-form-item label="原密码" prop="oldPassword">
            <el-input v-model="pwdForm.oldPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="pwdForm.newPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="确认新密码" prop="confirmPassword">
            <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="pwdSaving" @click="savePwd">修改密码</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCurrentUser, updateProfile, changePassword } from '@/api/auth'

const activeTab = ref('info')
const loading = ref(false)
const infoSaving = ref(false)
const pwdSaving = ref(false)
const infoFormRef = ref()
const pwdFormRef = ref()

const infoForm = reactive({ username: '', realName: '', phone: '', email: '' })
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const infoRules = {
  realName: [{ required: true, message: '请输入真实姓名' }],
  phone: [{ pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }]
}

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码' }],
  newPassword: [
    { required: true, message: '请输入新密码' },
    { min: 6, message: '密码长度不少于6位' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.newPassword) callback(new Error('两次密码不一致'))
        else callback()
      }
    }
  ]
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await getCurrentUser()
    const d = res.data
    Object.assign(infoForm, {
      username: d.username || '',
      realName: d.realName || '',
      phone: d.phone || '',
      email: d.email || ''
    })
  } finally {
    loading.value = false
  }
})

async function saveInfo() {
  await infoFormRef.value.validate()
  infoSaving.value = true
  try {
    await updateProfile({ realName: infoForm.realName, phone: infoForm.phone, email: infoForm.email })
    ElMessage.success('保存成功')
  } finally {
    infoSaving.value = false
  }
}

async function savePwd() {
  await pwdFormRef.value.validate()
  pwdSaving.value = true
  try {
    await changePassword({ oldPassword: pwdForm.oldPassword, newPassword: pwdForm.newPassword })
    ElMessage.success('密码修改成功，请重新登录')
    Object.assign(pwdForm, { oldPassword: '', newPassword: '', confirmPassword: '' })
    pwdFormRef.value.clearValidate()
  } finally {
    pwdSaving.value = false
  }
}
</script>
