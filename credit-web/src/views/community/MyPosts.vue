<template>
  <div>
    <div class="page-header animate-in">
      <h2>我的帖子</h2>
      <p>管理您发布的帖子</p>
    </div>

    <el-card shadow="never">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <div style="display: flex; align-items: center; gap: 8px">
            <el-icon color="#409EFF" :size="18"><List /></el-icon>
            <span>我的帖子</span>
          </div>
          <el-button type="primary" @click="handleAdd">
            <el-icon><EditPen /></el-icon> 发布帖子
          </el-button>
        </div>
      </template>

      <el-table :data="list" stripe v-loading="loading" class="clickable-table" @row-click="handleView">
        <el-table-column prop="id" label="#" width="55" align="center" />
        <el-table-column label="类型" width="100" align="center">
          <template #default="{ row }">
            <span class="post-type-tag" :class="row.postType === 1 ? 'tag-announce' : 'tag-help'">
              {{ row.postType === 1 ? '公告' : '求助' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="250" show-overflow-tooltip />
        <el-table-column prop="createTime" label="发布时间" width="170" />
        <el-table-column label="操作" width="220" align="center">
          <template #default="{ row }">
            <button type="button" class="action-btn action-view" @click.stop="handleView(row)">
              <el-icon><View /></el-icon> 查看
            </button>
            <el-button link type="success" :icon="Edit" @click.stop="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" :icon="Delete" @click.stop="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="detailVisible" title="帖子详情" width="650px" destroy-on-close>
      <div v-if="detail" class="post-detail">
        <div class="post-header">
          <span class="post-type-tag" :class="detail.postType === 1 ? 'tag-announce' : 'tag-help'">
            {{ detail.postType === 1 ? '公告' : '求助' }}
          </span>
          <span class="post-time">{{ detail.createTime }}</span>
        </div>
        <h3 class="post-title">{{ detail.title }}</h3>
        <div class="post-body">{{ detail.content }}</div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="550px" destroy-on-close>
      <el-form :model="form" label-width="80px">
        <el-form-item v-if="!editId" label="类型">
          <el-radio-group v-model="form.postType">
            <el-radio-button :value="0">技术求助</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入标题" maxlength="200" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入内容..." maxlength="5000" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { List, EditPen, View, Edit, Delete } from '@element-plus/icons-vue'
import { getMyPosts, addPost, updatePost, deletePost } from '@/api/community'

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const editId = ref(null)
const detailVisible = ref(false)
const detail = ref({})
const form = reactive({ title: '', content: '', postType: 0 })

async function fetchList() {
  loading.value = true
  try {
    const res = await getMyPosts(userInfo.id)
    list.value = res.data || []
  } finally { loading.value = false }
}

function handleView(row) { detail.value = row; detailVisible.value = true }

function handleAdd() {
  dialogTitle.value = '发布帖子'
  editId.value = null
  form.title = ''; form.content = ''; form.postType = 0
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑帖子'
  editId.value = row.id
  form.title = row.title; form.content = row.content; form.postType = row.postType
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!form.title || !form.content) { ElMessage.warning('请填写完整'); return }
  try {
    if (editId.value) {
      await updatePost({ id: editId.value, title: form.title, content: form.content })
      ElMessage.success('修改成功')
    } else {
      await addPost({ title: form.title, content: form.content, postType: form.postType, userId: userInfo.id })
      ElMessage.success('发布成功')
    }
    dialogVisible.value = false
    fetchList()
  } catch { }
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  try {
    await deletePost(id)
    ElMessage.success('删除成功')
    fetchList()
  } catch { }
}

onMounted(fetchList)
</script>

<style scoped>
.post-type-tag {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
}
.tag-announce {
  background: rgba(245, 108, 108, 0.12);
  color: #f56c6c;
  border: 1px solid rgba(245, 108, 108, 0.25);
}
.tag-help {
  background: rgba(64, 158, 255, 0.12);
  color: #409EFF;
  border: 1px solid rgba(64, 158, 255, 0.25);
}
.post-detail { padding: 0; }
.post-header { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
.post-time { font-size: 13px; color: var(--text-placeholder); }
.post-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 12px 0 16px;
  line-height: 1.5;
}
.post-body {
  color: var(--text-regular);
  font-size: 15px;
  line-height: 1.8;
  white-space: pre-wrap;
  padding: 16px;
  background: var(--bg-page);
  border-radius: var(--radius-md);
}

.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 5px 12px;
  font-size: 12px;
  font-weight: 600;
  border: none;
  border-radius: 7px;
  cursor: pointer;
  transition: all 0.22s ease;
  white-space: nowrap;
}
.action-btn:hover {
  transform: translateY(-1px);
}
.action-view {
  color: var(--primary);
  background: rgba(64, 158, 255, 0.06);
}
.action-view:hover {
  background: rgba(64, 158, 255, 0.15);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.18);
}

.clickable-table :deep(.el-table__body tr) {
  cursor: pointer;
  transition: background-color 0.2s ease;
}
</style>
