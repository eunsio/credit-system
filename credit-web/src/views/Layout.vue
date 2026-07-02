<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-aside">
      <div class="logo-box">
        <div class="logo-icon">
          <el-icon :size="18" color="#409EFF"><School /></el-icon>
        </div>
        <span v-if="!isCollapse" class="logo-text">创新学分管理</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        background-color="transparent"
        text-color="#64748b"
        active-text-color="#409EFF"
        class="side-menu"
      >

        <el-menu-item index="/profile">
          <el-icon><User /></el-icon>
          <span>个人信息</span>
        </el-menu-item>

        <el-menu-item index="/competitions">
          <el-icon><Trophy /></el-icon>
          <span>比赛信息</span>
        </el-menu-item>

        <el-menu-item v-if="userInfo?.role === 'ADMIN'" index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据统计</span>
        </el-menu-item>


        <el-menu-item v-if="userInfo?.role === 'STUDENT'" index="/my-awards">
          <el-icon><Medal /></el-icon>
          <span>我的获奖</span>
        </el-menu-item>
        <el-menu-item v-if="userInfo?.role === 'STUDENT'" index="/apply">
          <el-icon><EditPen /></el-icon>
          <span>学分申领</span>
        </el-menu-item>
        <el-menu-item v-if="userInfo?.role === 'STUDENT'" index="/apply-detail">
          <el-icon><Document /></el-icon>
          <span>申领详情</span>
        </el-menu-item>
        <el-menu-item v-if="userInfo?.role === 'ADMIN'" index="/apply-audit">
          <el-icon><Checked /></el-icon>
          <span>审批管理</span>
        </el-menu-item>
        <el-menu-item index="/community">
          <el-icon><ChatDotRound /></el-icon>
          <span>技术社区</span>
        </el-menu-item>
        <el-menu-item v-if="userInfo?.role === 'STUDENT'" index="/my-posts">
          <el-icon><List /></el-icon>
          <span>我的帖子</span>
        </el-menu-item>
        <el-menu-item v-if="userInfo?.role === 'ADMIN'" index="/instructors">
          <el-icon><School /></el-icon>
          <span>指导教师管理</span>
        </el-menu-item>
        <el-menu-item v-if="userInfo?.role === 'STUDENT'" index="/instructors">
          <el-icon><School /></el-icon>
          <span>指导教师</span>
        </el-menu-item>
        <el-menu-item index="/security">
          <el-icon><Lock /></el-icon>
          <span>安全中心</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container class="layout-right">
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon class="collapse-btn" :size="22" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" /><Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">
              <el-icon :size="14"><HomeFilled /></el-icon>
            </el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <div class="header-avatar-wrap">
            <el-avatar :size="32" :src="userAvatar" class="header-avatar">
              <el-icon :size="16"><UserFilled /></el-icon>
            </el-avatar>
          </div>
          <span class="header-nickname">{{ userInfo?.nickname || userInfo?.username }}</span>
          <el-tag :type="roleTagType[userInfo?.role]" size="small" round>{{ roleMap[userInfo?.role] || '' }}</el-tag>
          <el-button text circle :icon="SwitchButton" @click="handleLogout" title="退出登录" />
        </div>
      </el-header>
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { HomeFilled, UserFilled, SwitchButton, DataLine } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const isCollapse = ref(false)
const userInfo = computed(() => userStore.userInfo)

const activeMenu = computed(() => {
  const path = route.path
  if (path === '/') return '/dashboard'
  return path
})
const currentTitle = computed(() => route.meta?.title || '')
const userAvatar = computed(() => {
  return userInfo.value?.avatar || null
})

const roleMap = { ADMIN: '管理员', STUDENT: '学生', INSTRUCTOR: '指导教师' }
const roleTagType = { ADMIN: 'danger', STUDENT: 'success', INSTRUCTOR: 'warning' }

function handleLogout() {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' }).then(() => {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  overflow: hidden;
}
.layout-aside {
  background:
    linear-gradient(180deg,
      rgba(255, 255, 255, 0.95) 0%,
      rgba(248, 250, 252, 0.92) 50%,
      rgba(241, 245, 249, 0.88) 100%
    );
  transition: width 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  border-right: 1px solid var(--border-light);
  box-shadow: 3px 0 18px rgba(148, 163, 184, 0.06);
  position: relative;
}
.layout-aside::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 180px;
  background: radial-gradient(ellipse at top center, rgba(64, 158, 255, 0.06) 0%, transparent 70%);
  pointer-events: none;
}
.logo-box {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border-bottom: 1px solid var(--border-light);
  padding: 0 16px;
}
.logo-icon {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--primary-alpha-15), rgba(64, 158, 255, 0.08));
  border: 1px solid rgba(64, 158, 255, 0.12);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.logo-text {
  color: var(--text-primary);
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 1px;
  white-space: nowrap;
}
.side-menu {
  border-right: none !important;
  padding: 8px 0;
}
.side-menu .el-menu-item {
  margin: 3px 10px;
  border-radius: 8px;
  height: 42px;
  line-height: 42px;
  transition: all 0.25s ease;
  position: relative;
  overflow: hidden;
  color: #64748b;
}
.side-menu .el-menu-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 0;
  background: var(--primary);
  border-radius: 0 3px 3px 0;
  transition: height 0.25s ease;
}
.side-menu .el-menu-item:hover {
  background-color: var(--primary-alpha-10) !important;
  color: var(--primary) !important;
}
.side-menu .el-menu-item:hover::before {
  height: 16px;
}
.side-menu .el-menu-item.is-active {
  background: linear-gradient(90deg, rgba(64, 158, 255, 0.1), rgba(64, 158, 255, 0.03)) !important;
  color: var(--primary) !important;
  font-weight: 600;
}
.side-menu .el-menu-item.is-active::before {
  height: 20px;
}

.layout-right {
  flex-direction: column;
}
.layout-header {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.95), rgba(251, 253, 255, 0.92));
  backdrop-filter: blur(12px);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 58px;
  box-shadow: 0 1px 6px rgba(148, 163, 184, 0.07);
  z-index: 10;
  border-bottom: 1px solid var(--border-light);
}
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.collapse-btn {
  cursor: pointer;
  color: #94a3b8;
  padding: 6px;
  border-radius: 8px;
  transition: all 0.2s;
}
.collapse-btn:hover {
  background: var(--primary-alpha-10);
  color: var(--primary);
}
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.header-avatar-wrap {
  position: relative;
  cursor: pointer;
}
.header-avatar {
  border: 2px solid transparent;
  transition: all 0.2s;
  background: linear-gradient(#fff, #fff) padding-box,
              linear-gradient(135deg, var(--primary), #67c23a) border-box;
}
.header-avatar:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.22);
}
.header-nickname {
  font-size: 13px;
  color: var(--text-regular);
  font-weight: 500;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.layout-main {
  background: transparent;
  min-height: calc(100vh - 58px);
  padding: 24px 28px;
}
</style>
