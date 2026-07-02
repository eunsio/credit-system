<template>
  <div>
    <div class="page-header animate-in">
      <h2>安全中心</h2>
      <p>保护您的账户安全</p>
    </div>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div style="display: flex; align-items: center; gap: 8px">
              <el-icon color="#409EFF" :size="20"><Iphone /></el-icon>
              <span>绑定手机号</span>
            </div>
          </template>
          <el-form :model="phoneForm" label-width="100px" size="default">
            <el-form-item label="当前手机号">
              <el-input v-model="currentPhone" disabled>
                <template #prefix>
                  <!-- <el-icon><Phone /></el-icon> -->
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="新手机号">
              <el-input v-model="phoneForm.phone" placeholder="请输入新手机号" maxlength="20">
                <template #prefix>
                  <el-icon><Iphone /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="验证码">
              <div style="display: flex; gap: 10px; width: 100%">
                <el-input v-model="phoneForm.code" placeholder="6位验证码" maxlength="6" style="flex: 1">
                  <template #prefix>
                    <el-icon><Message /></el-icon>
                  </template>
                </el-input>
                <el-button
                  :type="codeCooldown > 0 ? 'default' : 'primary'"
                  :disabled="codeCooldown > 0"
                  @click="sendCode"
                  style="min-width: 130px"
                >
                  {{ codeCooldown > 0 ? codeCooldown + 's 后重发' : '获取验证码' }}
                </el-button>
              </div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleBindPhone">确认绑定</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div style="display: flex; align-items: center; gap: 8px">
              <el-icon color="#e6a23c" :size="20"><Lock /></el-icon>
              <span>修改密码</span>
            </div>
          </template>
          <el-form :model="pwdForm" label-width="100px" size="default">
            <el-form-item label="原密码">
              <el-input v-model="pwdForm.oldPassword" type="password" show-password maxlength="100">
                <template #prefix>
                  <el-icon><Unlock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="pwdForm.newPassword" type="password" show-password maxlength="100">
                <template #prefix>
                  <el-icon><CircleCheck /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="确认密码">
              <el-input v-model="pwdForm.confirmPassword" type="password" show-password maxlength="100">
                <template #prefix>
                  <el-icon><CircleCheck /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="warning" @click="handleChangePwd">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Iphone, Phone, Message, Lock, Unlock, CircleCheck } from '@element-plus/icons-vue'
import { updatePassword, updateUserInfo } from '@/api/user'

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
const currentPhone = ref(userInfo.phone || '未绑定')

const phoneForm = reactive({ phone: '', code: '' })
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const codeCooldown = ref(0)

function sendCode() {
  if (!phoneForm.phone) { ElMessage.warning('请先输入手机号'); return }
  if (!/^1[3-9]\d{9}$/.test(phoneForm.phone)) { ElMessage.warning('请输入正确的手机号'); return }
  codeCooldown.value = 60
  const timer = setInterval(() => { codeCooldown.value--; if (codeCooldown.value <= 0) clearInterval(timer) }, 1000)
  ElMessage.success('验证码已发送（模拟：888888）')
}

async function handleBindPhone() {
  if (!phoneForm.phone) { ElMessage.warning('请输入新手机号'); return }
  if (!phoneForm.code) { ElMessage.warning('请输入验证码'); return }
  if (phoneForm.code !== '888888') { ElMessage.error('验证码错误，请重试'); return }
  try {
    await updateUserInfo({ id: userInfo.id, phone: phoneForm.phone })
    userInfo.phone = phoneForm.phone
    localStorage.setItem('userInfo', JSON.stringify(userInfo))
    currentPhone.value = phoneForm.phone
    ElMessage.success('手机号绑定成功')
    phoneForm.phone = ''; phoneForm.code = ''
  } catch { }
}

async function handleChangePwd() {
  if (!pwdForm.oldPassword || !pwdForm.newPassword) { ElMessage.warning('请填写完整'); return }
  if (pwdForm.newPassword !== pwdForm.confirmPassword) { ElMessage.warning('两次密码不一致'); return }
  try {
    await updatePassword(userInfo.id, pwdForm.oldPassword, pwdForm.newPassword)
    ElMessage.success('密码修改成功，请重新登录')
    localStorage.removeItem('token'); localStorage.removeItem('userInfo')
    window.location.href = '/login'
  } catch { }
}
</script>

<style scoped>
</style>
