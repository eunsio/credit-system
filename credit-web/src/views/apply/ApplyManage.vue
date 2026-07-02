<template>
  <div>
    <template v-if="!isAuditMode">
      <el-card shadow="never" class="apply-card animate-in">
        <template #header>
          <div style="display: flex; align-items: center; gap: 8px">
            <el-icon :size="18" color="#409EFF"><Checked /></el-icon>
            <span>提交学分申领</span>
          </div>
        </template>

        <el-form :model="form" label-width="120px" style="max-width: 650px">
          <el-form-item label="获奖名称">
            <el-input v-model="form.awardTitle" placeholder="如：蓝桥杯Java组省赛一等奖" maxlength="200" />
          </el-form-item>
          <el-form-item label="证明图片">
            <div class="upload-wrapper">
              <div class="upload-area">
                <el-upload
                  :action="uploadAction"
                  :headers="uploadHeaders"
                  :show-file-list="false"
                  :on-success="handleImageSuccess"
                  :on-error="handleUploadError"
                  :before-upload="beforeUpload"
                  accept="image/*"
                  :disabled="imageUrls.length >= 5"
                  style="display: inline-block"
                >
                  <button type="button" class="upload-btn" :class="{ disabled: imageUrls.length >= 5 }">
                    <el-icon><UploadFilled /></el-icon>
                    上传图片（{{ imageUrls.length }}/5）
                  </button>
                </el-upload>
                <span class="upload-tip">最多上传5张，每张不超过10MB</span>
              </div>
              <div v-if="imageUrls.length > 0" class="image-grid">
                <div v-for="(url, idx) in imageUrls" :key="idx" class="image-item">
                  <el-image :src="url" class="image-preview" fit="cover">
                    <template #error>
                      <div class="image-error">加载失败</div>
                    </template>
                  </el-image>
                  <el-icon class="image-delete" @click="removeImage(idx)"><CircleCloseFilled /></el-icon>
                </div>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="申请学分">
            <el-input-number v-model="form.creditScore" :min="0" :max="10" :precision="1" :step="0.5" />
          </el-form-item>
          <el-form-item label="所属学年">
            <el-select v-model="form.academicYear" placeholder="请选择学年" style="width: 100%">
              <el-option v-for="y in yearOptions" :key="y" :label="y" :value="y" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <button type="button" class="submit-btn" @click="handleSubmit">
              <el-icon><Check /></el-icon> 提交申领
            </button>
          </el-form-item>
        </el-form>
      </el-card>
    </template>

    <template v-else>
      <div class="page-header animate-in">
        <h2>审批管理</h2>
        <p>学分申领审批管理</p>
      </div>

      <el-card shadow="never" class="audit-card animate-in">
        <template #header>
          <div class="card-header">
            <div class="header-left">
              <el-icon color="#409EFF" :size="18"><Checked /></el-icon>
              <span>申领列表</span>
            </div>
            <div class="tab-nav">
              <button
                type="button"
                class="tab-btn"
                :class="{ active: activeTab === 'pending' }"
                @click="switchTab('pending')"
              >
                <span class="tab-dot tab-dot-pending" /> 待审核
                <span class="tab-count">{{ counts.pending }}</span>
              </button>
              <button
                type="button"
                class="tab-btn"
                :class="{ active: activeTab === 'approved' }"
                @click="switchTab('approved')"
              >
                <span class="tab-dot tab-dot-approved" /> 已批准
                <span class="tab-count">{{ counts.approved }}</span>
              </button>
              <button
                type="button"
                class="tab-btn"
                :class="{ active: activeTab === 'rejected' }"
                @click="switchTab('rejected')"
              >
                <span class="tab-dot tab-dot-rejected" /> 已驳回
                <span class="tab-count">{{ counts.rejected }}</span>
              </button>
            </div>
          </div>
        </template>

        <el-table :data="pagedList" stripe v-loading="loading" style="width: 100%" class="clickable-table" @row-click="handleDetail">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="userId" label="学生ID" width="80" />
          <el-table-column prop="awardTitle" label="获奖名称" min-width="200" show-overflow-tooltip />
          <el-table-column prop="creditScore" label="申请学分" width="95" />
          <el-table-column prop="academicYear" label="学年" width="105" />
          <el-table-column label="状态" width="90" align="center">
            <template #default="{ row }">
              <span class="apply-status-badge" :class="getStatusClass(row.status)">
                {{ statusMap[row.status] }}
              </span>
            </template>
          </el-table-column>
          <el-table-column :label="activeTab === 'pending' ? '审批' : '审批备注'" min-width="160" align="center">
            <template #default="{ row }">
              <template v-if="activeTab === 'pending' && row.status === 0">
                <div class="audit-action-group">
                  <button type="button" class="action-btn action-pass" @click.stop="handleAudit(row, 1)">
                    <el-icon><Select /></el-icon> 通过
                  </button>
                  <button type="button" class="action-btn action-reject" @click.stop="handleAudit(row, 2)">
                    <el-icon><CloseBold /></el-icon> 驳回
                  </button>
                </div>
              </template>
              <template v-else>
                <span v-if="row.auditRemark" :style="{ color: row.status === 1 ? '#67c23a' : row.status === 2 ? '#f56c6c' : '#94a3b8', fontSize: '12px' }">{{ row.auditRemark }}</span>
                <span v-else class="text-muted">-</span>
              </template>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" align="center" fixed="right">
            <template #default="{ row }">
              <div class="action-row action-row-primary">
                <button type="button" class="action-btn action-view" @click.stop="handleDetail(row)">
                  <el-icon><View /></el-icon> 查看
                </button>
                <button type="button" class="action-btn action-delete" @click.stop="handleDelete(row)">
                  <el-icon><Delete /></el-icon> 删除
                </button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrap">
          <el-pagination
            v-model:current-page="pageNum"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50]"
            :total="filteredList.length"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            background
          />
        </div>
      </el-card>
    </template>

    <el-dialog v-model="auditDialogVisible" title="审批备注" width="420px" destroy-on-close>
      <el-input v-model="auditRemark" type="textarea" :rows="3" placeholder="请输入审批意见" maxlength="500" show-word-limit />
      <template #footer>
        <button type="button" class="dialog-cancel-btn" @click="auditDialogVisible = false">取消</button>
        <button type="button" class="dialog-confirm-btn" @click="confirmAudit">确认审批</button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="申请详情" width="750px" destroy-on-close>
      <template v-if="detailRow">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="申请ID">{{ detailRow.id }}</el-descriptions-item>
          <el-descriptions-item label="学生ID">{{ detailRow.userId }}</el-descriptions-item>
          <el-descriptions-item label="获奖名称" :span="2">{{ detailRow.awardTitle }}</el-descriptions-item>
          <el-descriptions-item label="申请学分">{{ detailRow.creditScore }} 分</el-descriptions-item>
          <el-descriptions-item label="学年">{{ detailRow.academicYear }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <span class="apply-status-badge" :class="getStatusClass(detailRow.status)">
              {{ statusMap[detailRow.status] }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="审批人ID">{{ detailRow.auditorId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ detailRow.createTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="审批时间">{{ detailRow.auditTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="审批备注" :span="2">{{ detailRow.auditRemark || '暂无' }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="detailRow.imageUrl" class="detail-images-section">
          <div class="section-label">证明图片</div>
          <div class="image-grid">
            <el-image
              v-for="(url, i) in detailRow.imageUrl.split(',')"
              :key="i"
              :src="url"
              :preview-src-list="detailRow.imageUrl.split(',')"
              :initial-index="i"
              class="image-preview-lg"
              fit="cover"
            >
              <template #error>
                <div class="image-error">加载失败</div>
              </template>
            </el-image>
          </div>
        </div>
      </template>
      <template #footer>
        <button type="button" class="dialog-close-btn" @click="detailVisible = false">关闭</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, onBeforeRouteLeave } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { CircleCloseFilled, Checked, Check, View, Select, CloseBold, Delete, UploadFilled } from '@element-plus/icons-vue'
import { submitApply, getAllApplies, auditApply, deleteApply } from '@/api/apply'
import { deleteFile } from '@/api/user'

const route = useRoute()
const isAuditMode = computed(() => route.path === '/apply-audit')
const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')

const statusMap = { 0: '待审核', 1: '已批准', 2: '已驳回' }

function getStatusClass(status) {
  if (status === 0) return 'status-pending'
  if (status === 1) return 'status-approved'
  return 'status-rejected'
}

const form = reactive({ awardTitle: '', creditScore: 2, academicYear: '' })
const imageUrls = ref([])

function getCurrentAcademicYear() {
  const now = new Date()
  const y = now.getFullYear()
  const m = now.getMonth() + 1
  return m >= 9 ? y + '-' + (y + 1) : (y - 1) + '-' + y
}

function buildYearOptions() {
  const current = getCurrentAcademicYear()
  const [base] = current.split('-').map(Number)
  const options = [current]
  for (let i = 1; i <= 3; i++) {
    options.push((base - i) + '-' + (base - i + 1))
  }
  return options
}

const yearOptions = buildYearOptions()
form.academicYear = getCurrentAcademicYear()

const list = ref([])
const loading = ref(false)
const auditDialogVisible = ref(false)
const auditRemark = ref('')
const currentAuditRow = ref(null)
const currentAuditStatus = ref(null)
const detailVisible = ref(false)
const detailRow = ref(null)
const submitted = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)

const uploadAction = '/api/user/upload'
const uploadHeaders = { Authorization: 'Bearer ' + localStorage.getItem('token') }

const activeTab = ref(['pending', 'approved', 'rejected'].includes(route.query.status) ? route.query.status : 'pending')
const counts = computed(() => ({
  pending: list.value.filter(r => r.status === 0).length,
  approved: list.value.filter(r => r.status === 1).length,
  rejected: list.value.filter(r => r.status === 2).length,
}))
const filteredList = computed(() => {
  const statusMapTab = { pending: 0, approved: 1, rejected: 2 }
  return list.value.filter(r => r.status === statusMapTab[activeTab.value])
})
const pagedList = computed(() => {
  const start = (pageNum.value - 1) * pageSize.value
  return filteredList.value.slice(start, start + pageSize.value)
})

function switchTab(tab) {
  activeTab.value = tab
  pageNum.value = 1
}

const hasFormData = computed(() => {
  return form.awardTitle || imageUrls.value.length > 0
})

onBeforeRouteLeave((to, from, next) => {
  if (!isAuditMode.value && hasFormData.value && !submitted.value) {
    ElMessageBox.confirm('当前填写的信息尚未提交，确定要离开吗？', '提示', {
      confirmButtonText: '确定离开',
      cancelButtonText: '继续填写',
      type: 'warning',
    }).then(() => next()).catch(() => next(false))
  } else {
    next()
  }
})

function handleImageSuccess(res) {
  const data = typeof res === 'string' ? JSON.parse(res) : res
  if (data.code === 200) {
    imageUrls.value.push(data.data.url)
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(data.message || '上传失败')
  }
}
function handleUploadError() { ElMessage.error('图片上传失败，请重试') }

function beforeUpload(file) {
  const isImage = file.type.startsWith('image/')
  if (!isImage) { ElMessage.error('只能上传图片文件'); return false }
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) { ElMessage.error('图片大小不能超过 10MB'); return false }
  return true
}

async function removeImage(idx) {
  const url = imageUrls.value[idx]
  const filename = url.split('/').pop()
  try {
    await deleteFile(filename)
    imageUrls.value.splice(idx, 1)
  } catch {
    imageUrls.value.splice(idx, 1)
  }
}

function resetForm() {
  form.awardTitle = ''
  form.academicYear = ''
  imageUrls.value = []
  submitted.value = false
}

async function handleSubmit() {
  if (!form.awardTitle || !form.academicYear) { ElMessage.warning('请填写获奖名称和所属学年'); return }
  if (imageUrls.value.length === 0) { ElMessage.warning('请至少上传一张证明图片'); return }
  try {
    await submitApply({
      userId: userInfo.id,
      awardTitle: form.awardTitle,
      imageUrl: imageUrls.value.join(','),
      creditScore: form.creditScore,
      academicYear: form.academicYear,
    })
    submitted.value = true
    ElMessage.success('申领已提交')
    resetForm()
  } catch { }
}

async function fetchList() {
  if (!isAuditMode.value) return
  loading.value = true
  try {
    const res = await getAllApplies(pageNum.value, pageSize.value)
    list.value = res.data.records || []
  } finally { loading.value = false }
}

function handleSizeChange(val) {
  pageSize.value = val
  const maxPage = Math.ceil(filteredList.value.length / pageSize.value) || 1
  if (pageNum.value > maxPage) {
    pageNum.value = maxPage
  }
  fetchList()
}

function handleCurrentChange(val) {
  pageNum.value = val
  fetchList()
}

function handleAudit(row, status) {
  currentAuditRow.value = row
  currentAuditStatus.value = status
  auditRemark.value = status === 1 ? '证书核验无误，予以认定' : '材料不符合认定标准'
  auditDialogVisible.value = true
}

function handleDetail(row) {
  detailRow.value = row
  detailVisible.value = true
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除该申领记录吗？', '警告', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
    })
  } catch { return }
  try {
    await deleteApply(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch { }
}

async function confirmAudit() {
  try {
    await auditApply(currentAuditRow.value.id, currentAuditStatus.value, auditRemark.value, userInfo.id)
    ElMessage.success(currentAuditStatus.value === 1 ? '已批准' : '已驳回')
    auditDialogVisible.value = false
    fetchList()
  } catch { }
}

onMounted(fetchList)
</script>

<style scoped>
.page-header h2 { font-size: 22px; font-weight: 800; color: var(--text-primary); margin-bottom: 4px; }
.page-header p { font-size: 14px; color: var(--text-placeholder); }

.apply-card :deep(.el-card__header) {
  font-weight: 700;
  font-size: 16px;
  color: var(--text-primary);
}
.text-muted { color: var(--text-disabled); }

/* ===== 卡片头部 ===== */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 700;
  color: var(--text-primary);
}

/* ===== 标签导航 ===== */
.tab-nav {
  display: flex;
  align-items: center;
  gap: 6px;
  background: #f1f5f9;
  padding: 3px;
  border-radius: 10px;
}
.tab-btn {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 6px 16px;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
  background: transparent;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.25s ease;
  white-space: nowrap;
}
.tab-btn:hover:not(.active) {
  color: var(--text-primary);
  background: rgba(255, 255, 255, 0.7);
}
.tab-btn.active {
  color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}
.tab-btn.active:nth-child(1) { background: linear-gradient(135deg, #e6a23c, #d4941e); }
.tab-btn.active:nth-child(2) { background: linear-gradient(135deg, #67c23a, #529b2e); }
.tab-btn.active:nth-child(3) { background: linear-gradient(135deg, #f56c6c, #d94444); }
.tab-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  display: inline-block;
}
.tab-dot-pending { background: #e6a23c; animation: pulse-dot 2s ease-in-out infinite; }
.tab-dot-approved { background: #67c23a; }
.tab-dot-rejected { background: #f56c6c; }
.tab-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  font-size: 11px;
  font-weight: 700;
  border-radius: 9px;
  background: rgba(0, 0, 0, 0.06);
  color: var(--text-secondary);
}
.tab-btn.active .tab-count {
  background: rgba(255, 255, 255, 0.25);
  color: #fff;
}

@keyframes pulse-dot {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.45; transform: scale(0.75); }
}

/* ===== 上传区域 ===== */
.upload-wrapper {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.upload-area {
  display: flex;
  align-items: center;
  gap: 12px;
}
.upload-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 7px 16px;
  font-size: 13px;
  font-weight: 600;
  color: var(--primary);
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.08), rgba(64, 158, 255, 0.04));
  border: 1px solid rgba(64, 158, 255, 0.2);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.25s ease;
}
.upload-btn:hover:not(.disabled) {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.14), rgba(64, 158, 255, 0.07));
  border-color: rgba(64, 158, 255, 0.4);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.18);
  transform: translateY(-1px);
}
.upload-btn.disabled { opacity: 0.45; cursor: not-allowed; }
.upload-tip { font-size: 12px; color: var(--text-placeholder); }

/* ===== 图片网格 ===== */
.image-grid { display: flex; flex-wrap: wrap; gap: 10px; }
.image-item {
  position: relative;
  width: 140px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
}
.image-preview {
  width: 140px;
  height: 100px;
  border-radius: 8px;
  border: 1px solid var(--border-light);
}
.image-preview-lg {
  width: 160px;
  height: 120px;
  border-radius: 8px;
  border: 1px solid var(--border-light);
}
.image-error {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  background: #f1f5f9;
  color: var(--text-placeholder);
  font-size: 12px;
}
.image-delete {
  position: absolute;
  top: -5px;
  right: -5px;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f56c6c, #e85d5d);
  color: #fff;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 6px rgba(245, 108, 108, 0.35);
  transition: transform 0.2s ease;
  z-index: 1;
}
.image-delete:hover { transform: scale(1.15); }

/* ===== 提交按钮 ===== */
.submit-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 28px;
  font-size: 14px;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(135deg, #409EFF, #337ecc);
  border: none;
  border-radius: 10px;
  cursor: pointer;
  box-shadow: 0 3px 14px rgba(64, 158, 255, 0.32);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.submit-btn:hover {
  box-shadow: 0 5px 22px rgba(64, 158, 255, 0.45);
  transform: translateY(-2px);
}
.submit-btn:active { transform: translateY(0); }

/* ===== 操作按钮组 ===== */
.action-group-wrap {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}
.audit-action-group {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}
.action-row { display: inline-flex; align-items: center; gap: 5px; }
.action-divider {
  width: 70%;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--border-light), transparent);
  margin: 2px 0;
}
.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 11px;
  font-size: 12px;
  font-weight: 600;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.22s ease;
  white-space: nowrap;
}
.action-btn:hover { transform: translateY(-1px); }
.action-view {
  color: var(--primary);
  background: rgba(64, 158, 255, 0.06);
}
.action-view:hover {
  background: rgba(64, 158, 255, 0.14);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.18);
}
.action-pass {
  color: #67c23a;
  background: rgba(103, 194, 58, 0.06);
}
.action-pass:hover {
  background: rgba(103, 194, 58, 0.14);
  box-shadow: 0 2px 8px rgba(103, 194, 58, 0.18);
}
.action-reject {
  color: #e6a23c;
  background: rgba(230, 162, 60, 0.06);
}
.action-reject:hover {
  background: rgba(230, 162, 60, 0.14);
  box-shadow: 0 2px 8px rgba(230, 162, 60, 0.18);
}
.action-delete {
  color: #f56c6c;
  background: rgba(245, 108, 108, 0.06);
}
.action-delete:hover {
  background: rgba(245, 108, 108, 0.14);
  box-shadow: 0 2px 8px rgba(245, 108, 108, 0.18);
}

/* ===== 状态徽章 ===== */
.apply-status-badge {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 2px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}
.status-pending {
  background: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
  border: 1px solid rgba(230, 162, 60, 0.2);
}
.status-approved {
  background: rgba(103, 194, 58, 0.1);
  color: #67c23a;
  border: 1px solid rgba(103, 194, 58, 0.2);
}
.status-rejected {
  background: rgba(245, 108, 108, 0.1);
  color: #f56c6c;
  border: 1px solid rgba(245, 108, 108, 0.2);
}

/* ===== 分页 ===== */
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

/* ===== 对话框按钮 ===== */
.dialog-cancel-btn {
  padding: 8px 20px;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
  background: #f1f5f9;
  border: 1px solid var(--border-light);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}
.dialog-cancel-btn:hover { background: #e8ecf2; color: var(--text-primary); }
.dialog-confirm-btn {
  padding: 8px 20px;
  font-size: 13px;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(135deg, #409EFF, #337ecc);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  box-shadow: 0 2px 10px rgba(64, 158, 255, 0.28);
  transition: all 0.25s ease;
}
.dialog-confirm-btn:hover {
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.38);
  transform: translateY(-1px);
}
.dialog-close-btn {
  padding: 8px 24px;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
  background: #f1f5f9;
  border: 1px solid var(--border-light);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}
.dialog-close-btn:hover { background: #e8ecf2; color: var(--text-primary); }

/* ===== 详情图片区 ===== */
.detail-images-section { margin-top: 16px; }
.section-label {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 10px;
}

.clickable-table :deep(.el-table__body tr) {
  cursor: pointer;
  transition: background-color 0.2s ease;
}
</style>
