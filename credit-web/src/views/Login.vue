<template>
  <div class="login-wrapper">
    <!-- 动态粒子光斑背景 -->
    <div class="login-bg">
      <div class="orb orb-1" />
      <div class="orb orb-2" />
      <div class="orb orb-3" />
      <div class="grid-overlay" />
    </div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <!-- 顶部 Logo 区 -->
      <div class="login-header">
        <div class="login-logo-ring">
          <el-icon :size="30" color="#409EFF"><School /></el-icon>
        </div>
        <h1 class="login-title">创新学分申领管理平台</h1>
        <p class="login-subtitle">Innovation Credit Application System</p>
      </div>

      <!-- 表单 -->
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="rules"
        size="large"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <div class="input-box">
            <label class="input-label">用户名 / 学号</label>
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名或学号"
              :prefix-icon="User"
              clearable
              maxlength="50"
              class="login-input"
            />
          </div>
        </el-form-item>
        <el-form-item prop="password">
          <div class="input-box">
            <label class="input-label">登录密码</label>
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入登录密码"
              :prefix-icon="Lock"
              show-password
              clearable
              maxlength="100"
              class="login-input"
            />
          </div>
        </el-form-item>
        <el-form-item style="margin-top: 8px">
          <el-button
            type="primary"
            :loading="loading"
            @click="handleLogin"
            class="login-btn"
          >
            <span v-if="!loading">登 录 系 统</span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form-item>
      </el-form>

    </div>

    <!-- 底部版本信息 -->
    <div class="login-footer">
      <span>IN12 Innovation Credit System v1.0.0</span>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, School } from '@element-plus/icons-vue'
import { login as loginApi, getUserInfo } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)
const loading = ref(false)
const loginForm = reactive({ username: '', password: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleLogin() {
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await loginApi(loginForm.username, loginForm.password)
    const userData = res.data
    if (userData.role === 'INSTRUCTOR') {
      ElMessage.warning('您是指导教师，请联系管理员获取系统权限')
      loading.value = false
      return
    }
    localStorage.setItem('token', userData.token)

    const baseUserInfo = {
      id: userData.userId,
      username: userData.username,
      nickname: userData.nickname || '',
      role: userData.role,
      avatar: '',
      phone: '',
      bio: '',
      contactInfo: '',
    }

    try {
      const userRes = await getUserInfo(userData.userId)
      const fullUserInfo = userRes.data
      if (fullUserInfo && fullUserInfo.id) {
        Object.assign(baseUserInfo, {
          nickname: fullUserInfo.nickname || baseUserInfo.nickname,
          avatar: fullUserInfo.avatar || '',
          phone: fullUserInfo.phone || '',
          bio: fullUserInfo.bio || '',
          contactInfo: fullUserInfo.contactInfo || '',
        })
      }
    } catch (err) {
      console.warn('获取完整用户信息失败，使用基本信息', err)
    }

    localStorage.setItem('userInfo', JSON.stringify(baseUserInfo))
    userStore.userInfo = baseUserInfo
    ElMessage.success({ message: '欢迎回来！', duration: 1200 })
    router.push('/')
  } catch {
    ElMessage.error('用户名或密码错误，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-wrapper {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background:
    linear-gradient(135deg, #f1f5f9 0%, #eef2f7 35%, #e8ecf2 70%, #f0f2f5 100%);
  position: relative;
  overflow: hidden;
}

/* 动态光球 */
.login-bg {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}
.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(90px);
}
.orb-1 {
  width: 480px;
  height: 480px;
  background: radial-gradient(circle, rgba(64,158,255,0.12) 0%, transparent 72%);
  top: -120px;
  right: -80px;
  animation: orbFloat1 14s ease-in-out infinite;
}
.orb-2 {
  width: 380px;
  height: 380px;
  background: radial-gradient(circle, rgba(103,194,58,0.08) 0%, transparent 72%);
  bottom: -80px;
  left: -60px;
  animation: orbFloat2 18s ease-in-out infinite;
}
.orb-3 {
  width: 280px;
  height: 280px;
  background: radial-gradient(circle, rgba(230,162,60,0.08) 0%, transparent 72%);
  top: 42%;
  left: 52%;
  animation: orbFloat3 12s ease-in-out infinite;
}
.grid-overlay {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(148,163,184,0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(148,163,184,0.04) 1px, transparent 1px);
  background-size: 44px 44px;
}

@keyframes orbFloat1 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(-25px, 35px) scale(1.04); }
  66% { transform: translate(18px, -18px) scale(0.97); }
}
@keyframes orbFloat2 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(35px, -25px) scale(1.06); }
}
@keyframes orbFloat3 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  40% { transform: translate(-18px, 25px) scale(1.08); }
  70% { transform: translate(12px, -12px) scale(0.96); }
}

/* 登录卡片 */
.login-card {
  width: 432px;
  padding: 42px 38px 34px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(251, 253, 255, 0.93));
  border: 1px solid var(--border-light);
  border-radius: 20px;
  box-shadow:
    0 20px 48px rgba(148, 163, 184, 0.1),
    0 0 0 1px rgba(148, 163, 184, 0.03);
  backdrop-filter: blur(16px);
  position: relative;
  z-index: 1;
  animation: cardEntrance 0.55s cubic-bezier(0.4, 0, 0.2, 1) forwards;
}
@keyframes cardEntrance {
  from { opacity: 0; transform: translateY(28px) scale(0.97); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

/* Logo */
.login-header {
  text-align: center;
  margin-bottom: 32px;
}
.login-logo-ring {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 68px;
  height: 68px;
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.1), rgba(64, 158, 255, 0.05));
  border: 1px solid rgba(64, 158, 255, 0.15);
  margin-bottom: 14px;
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.12);
  transition: box-shadow 0.35s ease;
}
.login-logo-ring:hover {
  box-shadow: 0 6px 24px rgba(64, 158, 255, 0.2);
}
.login-title {
  font-size: 21px;
  font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 5px;
  letter-spacing: 1px;
}
.login-subtitle {
  font-size: 11px;
  color: var(--text-placeholder);
  letter-spacing: 2.5px;
  text-transform: uppercase;
  font-weight: 500;
}

/* 表单 */
.login-form {
  margin-bottom: 8px;
}
.input-box {
  width: 100%;
}
.input-label {
  display: block;
  font-size: 11.5px;
  font-weight: 600;
  color: var(--text-secondary);
  letter-spacing: 0.8px;
  text-transform: uppercase;
  margin-bottom: 7px;
}
.login-input :deep(.el-input__wrapper) {
  background: #fff !important;
  border: 1px solid var(--border-light) !important;
  border-radius: 10px !important;
  box-shadow: none !important;
  padding: 4px 13px !important;
  height: 46px;
  transition: all 0.25s ease !important;
}
.login-input :deep(.el-input__wrapper:hover) {
  border-color: rgba(64, 158, 255, 0.4) !important;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.05) !important;
}
.login-input :deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary) !important;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.08) !important;
}
.login-input :deep(.el-input__inner) {
  color: var(--text-primary) !important;
  font-size: 14px;
}
.login-input :deep(.el-input__inner::placeholder) {
  color: var(--text-placeholder) !important;
}
.login-input :deep(.el-input__prefix .el-icon) {
  color: var(--text-placeholder);
  font-size: 15px;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 48px;
  border-radius: 10px !important;
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 4px;
  background: linear-gradient(135deg, #409EFF, #337ecc) !important;
  border: none !important;
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.28) !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  position: relative;
  overflow: hidden;
}
.login-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.18), transparent);
  transition: left 0.45s ease;
}
.login-btn:hover::before {
  left: 100%;
}
.login-btn:hover {
  box-shadow: 0 6px 24px rgba(64, 158, 255, 0.4) !important;
  transform: translateY(-2px) !important;
}
.login-btn:active {
  transform: translateY(0) !important;
}

/* 底部版本 */
.login-footer {
  position: fixed;
  bottom: 20px;
  font-size: 11px;
  color: var(--text-disabled);
  letter-spacing: 0.8px;
  z-index: 1;
}
</style>
