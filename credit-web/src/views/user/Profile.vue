<template>
  <div>
    <div class="page-header animate-in">
      <h2>个人信息</h2>
      <p>管理您的个人资料和头像</p>
    </div>

    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="avatar-card" shadow="never">
          <div class="avatar-section">
            <div class="avatar-ring">
              <el-avatar :size="120" :src="avatarSrc">
                <el-icon :size="56"><UserFilled /></el-icon>
              </el-avatar>
            </div>
            <h3 class="avatar-name">{{ form.nickname || form.username }}</h3>
            <el-tag size="small" round>{{ form.username }}</el-tag>
            <el-dropdown trigger="click" @command="handleAvatarCommand" style="margin-top: 20px">
              <el-button round size="small">
                <el-icon><Camera /></el-icon> 更换头像
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="cdn">
                    <el-icon><Link /></el-icon> CDN 直链（推荐）
                  </el-dropdown-item>
                  <el-dropdown-item command="upload">
                    <el-icon><Upload /></el-icon> 上传图片
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>

            <el-upload
              ref="uploadRef"
              :action="uploadAction"
              :headers="uploadHeaders"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :on-error="handleUploadError"
              :before-upload="beforeUpload"
              accept="image/*"
              style="display: none"
            />
          </div>
        </el-card>
      </el-col>

      <el-dialog v-model="cdnDialogVisible" title="设置 CDN 头像" width="450px" :close-on-click-modal="false">
        <div class="cdn-dialog-content">
          <p class="cdn-tip">请输入图片的直链地址（支持 http/https）</p>
          <el-input
            v-model="cdnUrl"
            placeholder="例如：https://example.com/avatar.jpg"
            maxlength="500"
            clearable
            size="large"
          >
            <template #prefix>
              <el-icon><Link /></el-icon>
            </template>
          </el-input>
          <div v-if="cdnUrl" class="cdn-preview">
            <span class="preview-label">预览：</span>
            <el-avatar :size="60" :src="cdnUrl">
              <el-icon :size="28"><UserFilled /></el-icon>
            </el-avatar>
          </div>
        </div>
        <template #footer>
          <el-button @click="cdnDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleCdnConfirm" :disabled="!cdnUrl">确认设置</el-button>
        </template>
      </el-dialog>

      <el-col :span="16">
        <el-card shadow="never">
          <template #header>
            <div style="display: flex; align-items: center; gap: 8px">
              <el-icon color="#409EFF"><Edit /></el-icon>
              <span>基本资料</span>
            </div>
          </template>
          <el-form :model="form" label-width="100px" size="default">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="用户名">
                  <el-input v-model="form.username" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="昵称">
                  <el-input v-model="form.nickname" placeholder="请输入昵称" maxlength="30" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="手机号">
                  <div class="field-with-edit">
                    <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="20" :disabled="!editingFields.phone" />
                    <button type="button" class="edit-btn" @click="handleEditPhone" v-if="!editingFields.phone">
                      <el-icon><Edit /></el-icon> 修改
                    </button>
                    <button type="button" class="edit-btn edit-btn-save" @click="handleSaveField('phone')" v-else>
                      <el-icon><Check /></el-icon> 完成
                    </button>
                  </div>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="联系方式">
                  <div class="field-with-edit">
                    <el-input v-model="form.contactInfo" placeholder="邮箱/办公室地址" maxlength="100" :disabled="!editingFields.contactInfo" />
                    <button type="button" class="edit-btn" @click="toggleEdit('contactInfo')" v-if="!editingFields.contactInfo">
                      <el-icon><Edit /></el-icon> 修改
                    </button>
                    <button type="button" class="edit-btn edit-btn-save" @click="handleSaveField('contactInfo')" v-else>
                      <el-icon><Check /></el-icon> 完成
                    </button>
                  </div>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="个人简介">
              <el-input v-model="form.bio" type="textarea" :rows="4" placeholder="简短的自我介绍..." maxlength="200" show-word-limit />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="large" @click="handleSave">
                <el-icon><Check /></el-icon> 保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UserFilled, Camera, Edit, Check, Upload, Link, ArrowDown } from '@element-plus/icons-vue'
import { updateUserInfo } from '@/api/user'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const router = useRouter()
const userInfo = userStore.userInfo || {}
const form = reactive({
  id: userInfo.id,
  username: userInfo.username,
  nickname: userInfo.nickname || '',
  avatar: userInfo.avatar || '',
  phone: userInfo.phone || '',
  bio: userInfo.bio || '',
  contactInfo: userInfo.contactInfo || '',
})

const avatarSrc = computed(() => form.avatar || null)

const uploadAction = '/api/user/upload'
const uploadHeaders = { Authorization: 'Bearer ' + localStorage.getItem('token') }
const uploadRef = ref(null)
const cdnDialogVisible = ref(false)
const cdnUrl = ref('')
const editingFields = reactive({
  phone: false,
  contactInfo: false,
  bio: false,
})

function toggleEdit(field) {
  editingFields[field] = !editingFields[field]
}

function handleEditPhone() {
  ElMessageBox.confirm('编辑手机号需要前往安全中心进行操作，是否跳转？', '提示', {
    confirmButtonText: '跳转',
    cancelButtonText: '取消',
    type: 'info',
  }).then(() => {
    router.push('/security')
  }).catch(() => {})
}

async function handleSaveField(field) {
  try {
    await updateUserInfo(form)
    Object.assign(userStore.userInfo, form)
    localStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))
    editingFields[field] = false
    ElMessage.success('保存成功')
  } catch {
    ElMessage.error('保存失败')
  }
}

function handleAvatarCommand(command) {
  if (command === 'upload') {
    uploadRef.value?.$el.querySelector('input')?.click()
  } else if (command === 'cdn') {
    cdnUrl.value = ''
    cdnDialogVisible.value = true
  }
}

async function handleCdnConfirm() {
  if (!cdnUrl.value.trim()) {
    ElMessage.warning('请输入图片地址')
    return
  }

  const trimmedUrl = cdnUrl.value.trim()
  if (!trimmedUrl.startsWith('http://') && !trimmedUrl.startsWith('https://')) {
    ElMessage.warning('请输入有效的 URL 地址（需以 http:// 或 https:// 开头）')
    return
  }

  try {
    form.avatar = trimmedUrl
    await updateUserInfo(form)
    Object.assign(userStore.userInfo, form)
    localStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))
    cdnDialogVisible.value = false
    ElMessage.success('CDN 头像设置成功')
  } catch {
    ElMessage.error('头像设置失败')
  }
}

async function handleAvatarSuccess(res) {
  const data = typeof res === 'string' ? JSON.parse(res) : res
  if (data.code === 200) {
    form.avatar = data.data.url
    try {
      await updateUserInfo(form)
      Object.assign(userStore.userInfo, form)
      localStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))
      ElMessage.success('头像上传成功')
    } catch {
      ElMessage.error('头像保存失败')
    }
  } else {
    ElMessage.error(data.message || '上传失败')
  }
}

function handleUploadError() {
  ElMessage.error('图片上传失败，请重试')
}

function beforeUpload(file) {
  const isImage = file.type.startsWith('image/')
  if (!isImage) { ElMessage.error('只能上传图片文件'); return false }
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) { ElMessage.error('图片大小不能超过 10MB'); return false }
  return true
}

async function handleSave() {
  try {
    await updateUserInfo(form)
    Object.assign(userStore.userInfo, form)
    localStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))
    ElMessage.success('修改成功')
  } catch { }
}
</script>

<style scoped>
.avatar-card { text-align: center; }
.avatar-card :deep(.el-card__body) { padding: 40px 24px; }
.avatar-section { display: flex; flex-direction: column; align-items: center; }
.avatar-ring {
  padding: 4px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409EFF, #67c23a);
  margin-bottom: 16px;
  transition: transform 0.3s ease;
}
.avatar-ring:hover { transform: scale(1.05); }
.avatar-name { font-size: 18px; font-weight: 700; color: var(--text-primary); margin-bottom: 8px; }
.avatar-card .el-avatar { box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15); }

.field-with-edit {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
}
.field-with-edit .el-input { flex: 1; }
.field-with-edit-vertical {
  align-items: flex-start;
}
.field-with-edit-vertical .el-input { flex: 1; }
.edit-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 14px;
  font-size: 12px;
  font-weight: 600;
  color: var(--primary);
  background: rgba(64, 158, 255, 0.06);
  border: 1px solid rgba(64, 158, 255, 0.2);
  border-radius: 7px;
  cursor: pointer;
  transition: all 0.22s ease;
  white-space: nowrap;
  flex-shrink: 0;
}
.edit-btn:hover {
  background: rgba(64, 158, 255, 0.14);
  border-color: rgba(64, 158, 255, 0.35);
  transform: translateY(-1px);
}
.edit-btn-save {
  color: #67c23a;
  background: rgba(103, 194, 58, 0.06);
  border-color: rgba(103, 194, 58, 0.2);
}
.edit-btn-save:hover {
  background: rgba(103, 194, 58, 0.14);
  border-color: rgba(103, 194, 58, 0.35);
}

.cdn-dialog-content { padding: 10px 0; }
.cdn-tip {
  font-size: 13px;
  color: var(--text-placeholder);
  margin-bottom: 16px;
  line-height: 1.6;
}
.cdn-preview {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 20px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid var(--border-light);
}
.preview-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
}
</style>
