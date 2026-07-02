<template>
  <div>
    <div class="page-header animate-in">
      <h2>技术社区</h2>
      <p>{{ isAdmin ? '管理社区帖子和公告' : '技术交流，互帮互助' }}</p>
    </div>

    <el-card shadow="never" class="community-card animate-in">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon color="#409EFF" :size="18"><ChatDotRound /></el-icon>
            <span>帖子列表</span>
          </div>
          <div class="header-right">
            <div class="tab-group">
              <button type="button" class="tab-btn" :class="{ active: activeTab === 'all' }" @click="switchTab('all')">
                全部
              </button>
              <button type="button" class="tab-btn" :class="{ active: activeTab === 'help', 'tab-help': activeTab === 'help' }" @click="switchTab('help')">
                <el-icon><ChatDotRound /></el-icon> 求助
              </button>
              <button type="button" class="tab-btn" :class="{ active: activeTab === 'announce', 'tab-announce': activeTab === 'announce' }" @click="switchTab('announce')">
                <el-icon><Bell /></el-icon> 公告
              </button>
            </div>
            <button type="button" class="add-post-btn" @click="handleAdd">
              <el-icon><EditPen /></el-icon> 发布帖子
            </button>
          </div>
        </div>
      </template>

      <el-table :key="activeTab" :data="pagedList" stripe v-loading="loading" style="width: 100%" class="clickable-table" @row-click="handleDetail">
        <el-table-column prop="id" label="#" width="55" align="center" />
        <el-table-column v-if="activeTab === 'all'" label="类型" width="100" align="center">
          <template #default="{ row }">
            <span class="post-type-tag" :class="row.postType === 1 ? 'tag-announce' : 'tag-help'">
              {{ row.postType === 1 ? '公告' : '求助' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="250" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="post-title-cell">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="170" />
        <el-table-column label="操作" width="170" align="center" fixed="right">
          <template #default="{ row }">
            <button type="button" class="action-btn action-view" @click.stop="handleDetail(row)">
              <el-icon><View /></el-icon> 查看
            </button>
            <button v-if="isAdmin" type="button" class="action-btn action-delete" @click.stop="handleDelete(row.id)">
              <el-icon><Delete /></el-icon> 删除
            </button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="550px" destroy-on-close>
      <el-form :model="form" label-width="80px">
        <el-form-item label="类型">
          <el-radio-group v-model="form.postType">
            <el-radio-button v-if="isAdmin" :value="1">公告</el-radio-button>
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
        <button type="button" class="dialog-cancel-btn" @click="dialogVisible = false">取消</button>
        <button type="button" class="dialog-submit-btn" @click="handleSubmit">发布</button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="帖子详情" width="680px" destroy-on-close @open="fetchReplies">
      <div v-if="detail" class="post-detail">
        <div class="post-header">
          <span class="post-type-tag" :class="detail.postType === 1 ? 'tag-announce' : 'tag-help'">
            {{ detail.postType === 1 ? '公告' : '求助' }}
          </span>
          <span class="post-time">{{ detail.createTime }}</span>
        </div>
        <h3 class="post-title">{{ detail.title }}</h3>
        <div class="post-body">{{ detail.content }}</div>

        <template v-if="detail.postType !== 1">
          <el-divider content-position="left">
            <el-icon><ChatDotRound /></el-icon> 回复 ({{ replies.length }})
          </el-divider>

          <div class="reply-input-area">
            <el-input
              v-model="replyContent"
              type="textarea"
              :rows="2"
              placeholder="写下你的回复..."
              maxlength="1000"
              show-word-limit
            />
            <button type="button" class="reply-submit-btn" @click="handleSubmitReply" :disabled="!replyContent.trim()">
              <el-icon><Promotion /></el-icon> 发表回复
            </button>
          </div>

          <div class="replies-list">
            <div v-if="replies.length === 0" class="no-reply">
              <el-icon :size="40" color="#c0c4cc"><ChatDotRound /></el-icon>
              <p>暂无回复，快来抢沙发吧~</p>
            </div>
            <div v-for="r in replies" :key="r.id" class="reply-item">
              <div class="reply-avatar">{{ r.nickname?.charAt(0) || '?' }}</div>
              <div class="reply-content-wrap">
                <div class="reply-meta">
                  <span class="reply-author">{{ r.nickname || '匿名用户' }}</span>
                  <span class="reply-time">{{ r.createTime }}</span>
                </div>
                <div class="reply-text">{{ r.content }}</div>
                <button v-if="isAdmin || r.userId === userInfo.id" type="button" class="reply-delete-btn" @click="handleDeleteReply(r.id)">
                  删除
                </button>
              </div>
            </div>
          </div>
        </template>
      </div>
      <template #footer>
        <button type="button" class="dialog-close-btn" @click="detailVisible = false">关闭</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ChatDotRound, EditPen, View, Delete, Promotion, Bell } from '@element-plus/icons-vue'
import { getPosts, addPost, deletePost, getReplies, addReply, deleteReply } from '@/api/community'

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
const isAdmin = computed(() => userInfo.role === 'ADMIN')

const list = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const activeTab = ref('all')

const filteredList = computed(() => {
  if (activeTab.value === 'help') return list.value.filter(item => item.postType === 0)
  if (activeTab.value === 'announce') return list.value.filter(item => item.postType === 1)
  return list.value
})

const pagedList = computed(() => {
  const start = (pageNum.value - 1) * pageSize.value
  return filteredList.value.slice(start, start + pageSize.value)
})
const dialogVisible = ref(false)
const dialogTitle = ref('')
const detailVisible = ref(false)
const detail = ref(null)
const form = reactive({ title: '', content: '', postType: 0 })

const replies = ref([])
const replyContent = ref('')

function handleAdd() {
  dialogTitle.value = '发布帖子'
  form.title = ''; form.content = ''
  if (activeTab.value === 'help') {
    form.postType = 0
  } else if (activeTab.value === 'announce') {
    form.postType = isAdmin.value ? 1 : 0
  } else {
    form.postType = isAdmin.value ? null : 0
  }
  dialogVisible.value = true
}

function switchTab(tab) {
  if (activeTab.value !== tab) {
    activeTab.value = tab
    pageNum.value = 1
  }
}

async function handleSubmit() {
  if (!form.title || !form.content) { ElMessage.warning('请填写完整'); return }
  if (form.postType === null || form.postType === undefined) { ElMessage.warning('请选择帖子类型'); return }
  try {
    await addPost({ title: form.title, content: form.content, postType: form.postType, userId: userInfo.id })
    ElMessage.success('发布成功')
    dialogVisible.value = false
    fetchList()
  } catch { }
}

function handleDetail(row) {
  detail.value = row
  detailVisible.value = true
}

async function fetchReplies() {
  if (!detail.value?.id) return
  try {
    const res = await getReplies(detail.value.id)
    replies.value = res.data || []
  } catch {
    replies.value = []
  }
}

async function handleSubmitReply() {
  if (!replyContent.value.trim()) return
  try {
    await addReply({
      postId: detail.value.id,
      userId: userInfo.id,
      nickname: userInfo.nickname || '匿名用户',
      content: replyContent.value.trim()
    })
    ElMessage.success('回复成功')
    replyContent.value = ''
    await fetchReplies()
  } catch { }
}

async function handleDeleteReply(id) {
  try {
    await deleteReply(id)
    ElMessage.success('删除成功')
    await fetchReplies()
  } catch { }
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除该帖子？', '警告', { type: 'warning' })
  try {
    await deletePost(id)
    ElMessage.success('删除成功')
    fetchList()
  } catch { }
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getPosts(pageNum.value, pageSize.value)
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
.page-header h2 { font-size: 22px; font-weight: 800; color: var(--text-primary); margin-bottom: 4px; }
.page-header p { font-size: 14px; color: var(--text-placeholder); }

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
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* ===== Tab 切换按钮 ===== */
.tab-group {
  display: inline-flex;
  background: var(--bg-page);
  border-radius: 10px;
  padding: 3px;
  gap: 2px;
}
.tab-btn {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 7px 18px;
  border: none;
  border-radius: 8px;
  font-size: 13.5px;
  font-weight: 600;
  color: var(--text-regular);
  background: transparent;
  cursor: pointer;
  transition: all 0.25s ease;
}
.tab-btn:hover { color: #409EFF; }
.tab-btn.active {
  color: #fff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.28);
}
.tab-btn.tab-help.active,
.tab-btn:not(.tab-help):not(.tab-announce).active {
  background: linear-gradient(135deg, #409EFF, #337ecc);
}
.tab-btn.tab-announce.active {
  background: linear-gradient(135deg, #E6A23C, #cf9236);
}

/* ===== 发布按钮（主按钮）===== */
.add-post-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 20px;
  font-size: 13px;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(135deg, #409EFF, #337ecc);
  border: none;
  border-radius: 9px;
  cursor: pointer;
  box-shadow: 0 3px 12px rgba(64, 158, 255, 0.3);
  transition: all 0.28s cubic-bezier(0.4, 0, 0.2, 1);
}
.add-post-btn:hover {
  box-shadow: 0 5px 20px rgba(64, 158, 255, 0.42);
  transform: translateY(-2px);
}
.add-post-btn:active {
  transform: translateY(0);
}

/* ===== 操作按钮组 ===== */
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
.action-delete {
  color: #f56c6c;
  background: rgba(245, 108, 108, 0.06);
}
.action-delete:hover {
  background: rgba(245, 108, 108, 0.15);
  box-shadow: 0 2px 8px rgba(245, 108, 108, 0.18);
}

/* ===== 类型标签 ===== */
.post-type-tag {
  display: inline-block;
  padding: 3px 11px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
}
.tag-announce {
  background: linear-gradient(135deg, rgba(245, 108, 108, 0.1), rgba(245, 108, 108, 0.05));
  color: #f56c6c;
  border: 1px solid rgba(245, 108, 108, 0.22);
}
.tag-help {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.1), rgba(64, 158, 255, 0.05));
  color: #409EFF;
  border: 1px solid rgba(64, 158, 255, 0.22);
}
.post-title-cell {
  font-weight: 650;
  color: var(--text-primary);
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
.dialog-cancel-btn:hover {
  background: #e8ecf2;
  color: var(--text-primary);
}
.dialog-submit-btn {
  padding: 8px 24px;
  font-size: 13px;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(135deg, #409EFF, #337ecc);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  box-shadow: 0 2px 10px rgba(64, 158, 255, 0.3);
  transition: all 0.25s ease;
}
.dialog-submit-btn:hover {
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.4);
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
.dialog-close-btn:hover {
  background: #e8ecf2;
  color: var(--text-primary);
}

/* ===== 帖子详情 ===== */
.post-detail { padding: 0; }
.post-header { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
.post-time { font-size: 13px; color: var(--text-placeholder); }
.post-title {
  font-size: 20px;
  font-weight: 750;
  color: var(--text-primary);
  margin: 12px 0 16px;
  line-height: 1.45;
}
.post-body {
  color: var(--text-regular);
  font-size: 15px;
  line-height: 1.85;
  white-space: pre-wrap;
  padding: 18px;
  background: linear-gradient(135deg, #f8fafc, #f1f5f9);
  border-radius: 10px;
  border: 1px solid var(--border-light);
}

/* ===== 整行可点击 ===== */
.clickable-table :deep(.el-table__body tr) {
  cursor: pointer;
  transition: background-color 0.2s ease;
}

/* ===== 回复区域 ===== */
.reply-input-area {
  margin-bottom: 16px;
}
.reply-submit-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  padding: 7px 18px;
  font-size: 13px;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(135deg, #409EFF, #337ecc);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.25s ease;
}
.reply-submit-btn:hover:not(:disabled) {
  box-shadow: 0 3px 12px rgba(64, 158, 255, 0.35);
  transform: translateY(-1px);
}
.reply-submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.replies-list { margin-top: 4px; }
.no-reply {
  text-align: center;
  padding: 32px 0;
  color: var(--text-placeholder);
}
.no-reply p { margin: 8px 0 0; font-size: 14px; }

.reply-item {
  display: flex;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid var(--border-lighter);
}
.reply-item:last-child { border-bottom: none; }
.reply-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409EFF, #66b1ff);
  color: #fff;
  font-size: 14px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.reply-content-wrap { flex: 1; min-width: 0; }
.reply-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
}
.reply-author {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-primary);
}
.reply-time {
  font-size: 12px;
  color: var(--text-placeholder);
}
.reply-text {
  font-size: 14px;
  color: var(--text-regular);
  line-height: 1.65;
  word-break: break-word;
}
.reply-delete-btn {
  margin-top: 6px;
  padding: 2px 10px;
  font-size: 12px;
  color: #f56c6c;
  background: rgba(245, 108, 108, 0.06);
  border: 1px solid rgba(245, 108, 108, 0.15);
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.2s ease;
}
.reply-delete-btn:hover {
  background: rgba(245, 108, 108, 0.12);
}
</style>
