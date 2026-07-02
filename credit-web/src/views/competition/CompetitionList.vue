<template>
  <div>
    <div class="page-header animate-in">
      <h2>比赛信息</h2>
      <p>{{ isAdmin ? '管理学科竞赛信息' : '浏览当前可参加的比赛' }}</p>
    </div>

    <el-card shadow="never">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <div style="display: flex; align-items: center; gap: 8px">
            <el-icon color="#409EFF" :size="18"><Trophy /></el-icon>
            <span>比赛列表</span>
          </div>
          <el-button v-if="isAdmin" type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon> 新增比赛
          </el-button>
        </div>
      </template>

      <div class="toolbar">
        <el-input v-model="searchKeyword" placeholder="搜索比赛名称..." clearable maxlength="100" style="width: 280px" :prefix-icon="Search" />
        <el-checkbox v-model="showActiveOnly">仅显示进行中和即将开始的</el-checkbox>
      </div>

      <el-table :data="displayList" stripe v-loading="loading" style="width: 100%" class="clickable-table" @row-click="handleViewDetail">
        <el-table-column prop="id" label="#" width="55" align="center" />
        <el-table-column prop="title" label="比赛名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="content" label="比赛详情" min-width="220" />
        <el-table-column label="状态" width="110" align="center">
          <template #default="{ row }">
            <span class="status-tag" :class="getStatusTag(row).class">
              <span class="status-dot" :class="getStatusTag(row).dotClass" />
              {{ getStatusTag(row).text }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="报名开始" width="120" />
        <el-table-column prop="endTime" label="报名截止" width="120" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <template v-if="row.registrationUrl">
              <el-button class="register-btn" @click.stop="openUrl(row.registrationUrl)">
                <el-icon><Link /></el-icon> 报名
              </el-button>
            </template>
            <template v-if="isAdmin">
              <el-button link class="action-btn-edit" @click.stop="handleEdit(row)">
                <el-icon><Edit /></el-icon>
              </el-button>
              <el-button link class="action-btn-delete" @click.stop="handleDelete(row.id)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>

      <div style="display: flex; justify-content: flex-end; margin-top: 16px">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="displayTotal"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchList"
          @current-change="fetchList"
          background
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="550px" destroy-on-close>
      <el-form :model="form" label-width="100px">
        <el-form-item label="比赛名称">
          <el-input v-model="form.title" placeholder="请输入比赛名称" maxlength="200" />
        </el-form-item>
        <el-form-item label="比赛详情">
          <el-input v-model="form.content" type="textarea" :rows="3" placeholder="请输入比赛详情" maxlength="5000" show-word-limit />
        </el-form-item>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="报名开始">
              <el-date-picker v-model="form.startTimeStr" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="报名截止">
              <el-date-picker v-model="form.endTimeStr" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="报名链接">
          <el-input v-model="form.registrationUrl" placeholder="请输入报名链接" maxlength="500" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="比赛详细信息" width="580px" destroy-on-close>
      <div class="detail-content">
        <div class="detail-header">
          <div class="detail-icon">
            <el-icon :size="32"><Trophy /></el-icon>
          </div>
          <div class="detail-info">
            <h3 class="detail-title">{{ detailData.title || '未知比赛' }}</h3>
            <span class="detail-status-tag" :class="getStatusTag(detailData).class">
              {{ getStatusTag(detailData).text }}
            </span>
          </div>
        </div>

        <el-divider />

        <div class="detail-section">
          <div class="detail-row">
            <span class="detail-label"><el-icon><Calendar /></el-icon> 报名时间</span>
            <span class="detail-value">{{ detailData.startTime || '未设置' }} ~ {{ detailData.endTime || '未设置' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label"><el-icon><Document /></el-icon> 比赛详情</span>
            <span class="detail-value detail-bio">{{ detailData.content || '暂无详情' }}</span>
          </div>
          <div class="detail-row" v-if="detailData.registrationUrl">
            <span class="detail-label"><el-icon><Link /></el-icon> 报名链接</span>
            <a :href="detailData.registrationUrl" target="_blank" class="detail-link">{{ detailData.registrationUrl }}</a>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button v-if="isAdmin" type="primary" @click="handleEditFromDetail">
          <el-icon><Edit /></el-icon> 编辑信息
        </el-button>
        <el-button v-if="detailData.registrationUrl" type="success" @click.stop="openUrl(detailData.registrationUrl)">
          <el-icon><Link /></el-icon> 前往报名
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Trophy, Plus, Link, Edit, Delete, Calendar, Document } from '@element-plus/icons-vue'
import { getCompetitions, addCompetition, updateCompetition, deleteCompetition } from '@/api/competition'

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
const isAdmin = computed(() => userInfo.role === 'ADMIN')

const list = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const editId = ref(null)
const searchKeyword = ref('')
const showActiveOnly = ref(true)
const form = reactive({ title: '', content: '', startTimeStr: '', endTimeStr: '', registrationUrl: '' })

const detailVisible = ref(false)
const detailData = reactive({ id: null, title: '', content: '', startTime: '', endTime: '', registrationUrl: '' })

function handleViewDetail(row) {
  Object.assign(detailData, {
    id: row.id,
    title: row.title || '',
    content: row.content || '',
    startTime: extractDate(row.startTime),
    endTime: extractDate(row.endTime),
    registrationUrl: row.registrationUrl || ''
  })
  detailVisible.value = true
}

function handleEditFromDetail() {
  detailVisible.value = false
  const data = { ...detailData }
  setTimeout(() => {
    handleEdit(data)
  }, 100)
}

function getStatusTag(row) {
  const now = Date.now()
  const start = row.startTime ? new Date(row.startTime).getTime() : 0
  const end = row.endTime ? new Date(row.endTime).getTime() : 0
  if (now < start) return { text: '即将开始', class: 'status-upcoming', dotClass: 'dot-upcoming' }
  if (now > end) return { text: '已结束', class: 'status-ended', dotClass: 'dot-ended' }
  return { text: '进行中', class: 'status-active', dotClass: 'dot-active' }
}

function openUrl(url) {
  if (url) window.open(url, '_blank')
}

const displayList = computed(() => {
  let result = list.value
  if (showActiveOnly.value) {
    const now = Date.now()
    result = result.filter((item) => {
      const end = item.endTime ? new Date(item.endTime).getTime() : Infinity
      return end > now
    })
  }
  if (searchKeyword.value) {
    result = result.filter((item) => item.title && item.title.includes(searchKeyword.value))
  }
  const start = (pageNum.value - 1) * pageSize.value
  return result.slice(start, start + pageSize.value)
})
const displayTotal = computed(() => {
  let result = list.value
  if (showActiveOnly.value) {
    const now = Date.now()
    result = result.filter((item) => {
      const end = item.endTime ? new Date(item.endTime).getTime() : Infinity
      return end > now
    })
  }
  if (searchKeyword.value) {
    result = result.filter((item) => item.title && item.title.includes(searchKeyword.value))
  }
  return result.length
})

function resetForm() {
  form.title = ''; form.content = ''; form.startTimeStr = ''; form.endTimeStr = ''; form.registrationUrl = ''
  editId.value = null
}

function handleAdd() {
  dialogTitle.value = '新增比赛'
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑比赛'
  editId.value = row.id
  form.title = row.title; form.content = row.content || ''
  form.startTimeStr = extractDate(row.startTime)
  form.endTimeStr = extractDate(row.endTime)
  form.registrationUrl = row.registrationUrl || ''
  dialogVisible.value = true
}

function extractDate(val) {
  if (!val) return ''
  if (Array.isArray(val)) {
    const [y, m, d] = val
    return y + '-' + String(m).padStart(2, '0') + '-' + String(d).padStart(2, '0')
  }
  const s = String(val)
  return s.includes('T') ? s.substring(0, s.indexOf('T')) : s.substring(0, 10)
}

async function handleSubmit() {
  if (!form.title) { ElMessage.warning('请输入比赛名称'); return }
  const payload = {
    title: form.title,
    content: form.content,
    startTime: form.startTimeStr ? form.startTimeStr + 'T00:00:00' : null,
    endTime: form.endTimeStr ? form.endTimeStr + 'T23:59:59' : null,
    registrationUrl: form.registrationUrl,
  }
  try {
    if (editId.value) {
      await updateCompetition({ id: editId.value, ...payload })
      ElMessage.success('修改成功')
    } else {
      await addCompetition(payload)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchList()
  } catch { }
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除该比赛信息？', '警告', { type: 'warning' })
  try {
    await deleteCompetition(id)
    ElMessage.success('删除成功')
    fetchList()
  } catch { }
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getCompetitions(pageNum.value, pageSize.value)
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } finally { loading.value = false }
}

onMounted(fetchList)
</script>

<style scoped>
.toolbar {
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.status-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}
.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  flex-shrink: 0;
}
.dot-active { background: #67c23a; animation: pulse-dot 2s ease-in-out infinite; }
.dot-upcoming { background: #e6a23c; animation: pulse-dot 2s ease-in-out infinite 0.5s; }
.dot-ended { background: #c0c4cc; }
@keyframes pulse-dot {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(0.8); }
}
.status-active {
  background: rgba(103, 194, 58, 0.12);
  color: #67c23a;
  border: 1px solid rgba(103, 194, 58, 0.25);
}
.status-upcoming {
  background: rgba(230, 162, 60, 0.12);
  color: #e6a23c;
  border: 1px solid rgba(230, 162, 60, 0.25);
}
.status-ended {
  background: rgba(144, 147, 153, 0.1);
  color: #909399;
  border: 1px solid rgba(144, 147, 153, 0.2);
}

.competition-name {
  font-weight: 600;
  color: var(--text-regular);
}
.competition-desc {
  font-size: 12px;
  color: var(--text-placeholder);
  margin-top: 2px;
}

.register-btn {
  background: linear-gradient(135deg, #409EFF, #337ecc);
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 12px;
  height: 28px;
  transition: all 0.2s;
}
.register-btn:hover {
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.35);
  transform: translateY(-1px);
}

.action-btn-edit {
  color: #67c23a;
  font-size: 16px;
}
.action-btn-delete {
  color: #f56c6c;
  font-size: 16px;
}

.clickable-table :deep(.el-table__body tr) {
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.detail-content {
  padding: 0 10px;
}
.detail-header {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 16px 0;
}
.detail-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: linear-gradient(135deg, #e6a23c, #f56c6c);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.detail-info {
  flex: 1;
}
.detail-title {
  margin: 0 0 8px;
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
}
.detail-status-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 3px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
}
.detail-section {
  padding: 4px 0;
}
.detail-row {
  display: flex;
  align-items: flex-start;
  padding: 12px 0;
  border-bottom: 1px solid var(--border-lighter);
}
.detail-row:last-child {
  border-bottom: none;
}
.detail-label {
  width: 90px;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-regular);
  display: flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
}
.detail-value {
  flex: 1;
  font-size: 14px;
  color: var(--text-primary);
  line-height: 1.6;
}
.detail-bio {
  white-space: pre-wrap;
  word-break: break-all;
}
.detail-link {
  flex: 1;
  font-size: 14px;
  color: var(--primary, #409EFF);
  text-decoration: none;
  word-break: break-all;
}
.detail-link:hover {
  text-decoration: underline;
}
</style>
