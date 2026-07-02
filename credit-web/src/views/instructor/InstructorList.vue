<template>
  <div>
    <div class="page-header animate-in">
      <h2>{{ isAdmin ? '指导教师管理' : '指导教师' }}</h2>
      <p>{{ isAdmin ? '管理全校指导教师信息' : '查看全校教师信息及联系方式' }}</p>
    </div>

    <el-card shadow="never">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <div style="display: flex; align-items: center; gap: 8px">
            <el-icon color="#409EFF" :size="18"><School /></el-icon>
            <span>教师列表</span>
          </div>
          <el-button v-if="isAdmin" type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon> 新增教师
          </el-button>
        </div>
      </template>
      <el-table :data="pagedList" stripe v-loading="loading" class="clickable-table" @row-click="handleViewDetail">
        <el-table-column prop="id" label="#" width="55" align="center" />
        <el-table-column label="姓名" width="140">
          <template #default="{ row }">
            <div class="teacher-cell">
              <div class="teacher-avatar">{{ row.nickname?.charAt(0) || '?' }}</div>
              <span class="teacher-name">{{ row.nickname }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="工号" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="bio" label="简介" min-width="180" show-overflow-tooltip />
        <el-table-column prop="contactInfo" label="联系方式" width="200" show-overflow-tooltip />
        <el-table-column v-if="isAdmin" label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <button type="button" class="action-btn action-edit" @click.stop="handleEdit(row)">
              <el-icon><Edit /></el-icon> 编辑
            </button>
            <button type="button" class="action-btn action-delete" @click.stop="handleDelete(row.id)">
              <el-icon><Delete /></el-icon> 删除
            </button>
          </template>
        </el-table-column>
      </el-table>

      <div style="display: flex; justify-content: flex-end; margin-top: 16px">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="list.length"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form :model="form" label-width="80px">
        <el-form-item label="工号">
          <el-input v-model="form.username" placeholder="请输入工号/用户名" maxlength="50" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.nickname" placeholder="请输入姓名" maxlength="30" />
        </el-form-item>
        <el-form-item label="密码" v-if="!editId">
          <el-input v-model="form.password" placeholder="请输入初始密码" maxlength="100" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="20" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.bio" type="textarea" :rows="2" placeholder="教师简介..." maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="form.contactInfo" placeholder="邮箱/办公室" maxlength="100" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="教师详细信息" width="560px" destroy-on-close>
      <div class="detail-content">
        <div class="detail-header">
          <div class="detail-avatar">{{ detailData.nickname?.charAt(0) || '?' }}</div>
          <div class="detail-info">
            <h3 class="detail-name">{{ detailData.nickname || '未知' }}</h3>
            <p class="detail-id">工号：{{ detailData.username || '-' }}</p>
          </div>
        </div>

        <el-divider />

        <div class="detail-section">
          <div class="detail-row">
            <span class="detail-label"><el-icon><Phone /></el-icon> 手机号码</span>
            <span class="detail-value">{{ detailData.phone || '未填写' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label"><el-icon><Message /></el-icon> 联系方式</span>
            <span class="detail-value">{{ detailData.contactInfo || '未填写' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label"><el-icon><Document /></el-icon> 教师简介</span>
            <span class="detail-value detail-bio">{{ detailData.bio || '暂无简介' }}</span>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button v-if="isAdmin" type="primary" @click="handleEditFromDetail">
          <el-icon><Edit /></el-icon> 编辑信息
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { School, Plus, Edit, Delete, UserFilled, Phone, Message, Document } from '@element-plus/icons-vue'
import { getInstructors, addInstructor, updateInstructor, deleteInstructor } from '@/api/user'

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
const isAdmin = computed(() => userInfo.role === 'ADMIN')

const list = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const pagedList = computed(() => {
  const start = (pageNum.value - 1) * pageSize.value
  return list.value.slice(start, start + pageSize.value)
})
const dialogVisible = ref(false)
const dialogTitle = ref('')
const editId = ref(null)
const form = reactive({ username: '', nickname: '', password: '', phone: '', bio: '', contactInfo: '' })

const detailVisible = ref(false)
const detailData = reactive({ id: null, nickname: '', username: '', phone: '', bio: '', contactInfo: '' })

function handleViewDetail(row) {
  Object.assign(detailData, {
    id: row.id,
    nickname: row.nickname || '',
    username: row.username || '',
    phone: row.phone || '',
    bio: row.bio || '',
    contactInfo: row.contactInfo || ''
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

function handleAdd() {
  dialogTitle.value = '新增指导教师'
  editId.value = null
  form.username = ''; form.nickname = ''; form.password = ''; form.phone = ''; form.bio = ''; form.contactInfo = ''
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑教师信息'
  editId.value = row.id
  form.username = row.username; form.nickname = row.nickname; form.password = ''
  form.phone = row.phone || ''; form.bio = row.bio || ''; form.contactInfo = row.contactInfo || ''
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!form.username || !form.nickname) { ElMessage.warning('请填写工号和姓名'); return }
  try {
    const payload = { ...form }
    if (editId.value) {
      payload.id = editId.value
      if (!payload.password) delete payload.password
      await updateInstructor(payload)
      ElMessage.success('修改成功')
    } else {
      await addInstructor(payload)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchList()
  } catch { }
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除该教师信息？', '警告', { type: 'warning' })
  try {
    await deleteInstructor(id)
    ElMessage.success('删除成功')
    fetchList()
  } catch { }
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getInstructors(pageNum.value, pageSize.value)
    list.value = res.data.records || []
  } finally { loading.value = false }
}

function handleSizeChange(val) {
  pageSize.value = val
  const maxPage = Math.ceil(list.value.length / pageSize.value) || 1
  if (pageNum.value > maxPage) {
    pageNum.value = maxPage
  }
  fetchList()
}

function handleCurrentChange(val) {
  pageNum.value = val
  fetchList()
}

onMounted(fetchList)
</script>

<style scoped>
.teacher-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}
.clickable-table :deep(.el-table__body tr) {
  cursor: pointer;
  transition: background-color 0.2s ease;
}
.teacher-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary), var(--info));
  color: #fff;
  font-size: 13px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.teacher-name {
  font-weight: 600;
  color: var(--text-regular);
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
.action-btn:hover {
  transform: translateY(-1px);
}
.action-edit {
  color: #67c23a;
  background: rgba(103, 194, 58, 0.06);
}
.action-edit:hover {
  background: rgba(103, 194, 58, 0.14);
  box-shadow: 0 2px 8px rgba(103, 194, 58, 0.18);
}
.action-delete {
  color: #f56c6c;
  background: rgba(245, 108, 108, 0.06);
}
.action-delete:hover {
  background: rgba(245, 108, 108, 0.14);
  box-shadow: 0 2px 8px rgba(245, 108, 108, 0.18);
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
.detail-avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary), var(--info));
  color: #fff;
  font-size: 28px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.detail-info {
  flex: 1;
}
.detail-name {
  margin: 0 0 6px;
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
}
.detail-id {
  margin: 0;
  font-size: 13px;
  color: var(--text-secondary);
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
  width: 100px;
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
</style>
