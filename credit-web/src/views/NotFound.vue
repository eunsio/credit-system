<template>
  <div class="not-found">
    <div class="nf-code">404</div>
    <div class="nf-icon">🔭</div>
    <h2>页面未找到</h2>
    <p>您访问的页面不存在或已被移除</p>
    <el-button type="primary" size="large" round @click="$router.push('/')">
      <el-icon><HomeFilled /></el-icon> 返回首页
    </el-button>
  </div>
</template>

<script setup>
import { HomeFilled } from '@element-plus/icons-vue'
</script>

<style scoped>
.not-found {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}
.nf-code {
  font-size: 100px;
  font-weight: 900;
  background: linear-gradient(135deg, #409EFF, #67c23a);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1;
}
.nf-icon { font-size: 56px; margin: 8px 0 16px; }
h2 { font-size: 22px; color: #1a2332; margin-bottom: 8px; }
p { color: #909399; margin-bottom: 28px; font-size: 14px; }
</style>
