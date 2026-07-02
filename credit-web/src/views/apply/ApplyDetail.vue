<template>
  <div>
    <div class="page-header animate-in">
      <h2>申领详情</h2>
      <p>查看所有学分申领记录与审批进度</p>
    </div>

    <el-card shadow="never">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <div style="display: flex; align-items: center; gap: 8px">
            <el-icon color="#409EFF" :size="18"><Document /></el-icon>
            <span>申领记录</span>
          </div>
          <div class="score-badge">
            <span class="score-label">已获学分</span>
            <span class="score-value">{{ totalScore }}</span>
          </div>
        </div>
      </template>
      <el-table :data="records" stripe v-loading="loading">
        <el-table-column prop="id" label="#" width="55" align="center" />
        <el-table-column prop="awardTitle" label="获奖名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="creditScore" label="学分" width="80" align="center">
          <template #default="{ row }">
            <el-tag size="small" round>{{ row.creditScore }} 分</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="academicYear" label="学年" width="110" />
        <el-table-column label="状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType[row.status]" size="small" effect="plain" round>
              {{ statusMap[row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="170" />
        <el-table-column label="操作" width="160" align="center">
          <template #default="{ row }">
            <button type="button" class="action-btn action-view" @click="handleDetail(row)">
              <el-icon><View /></el-icon> 查看
            </button>
            <el-button v-if="row.status === 0" link type="danger" :icon="Close" @click="handleWithdraw(row)">撤回</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="detailVisible" title="申领详情" width="750px" destroy-on-close>
      <template v-if="detailRow">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="申请ID">{{ detailRow.id }}</el-descriptions-item>
          <el-descriptions-item label="获奖名称" :span="2">{{ detailRow.awardTitle }}</el-descriptions-item>
          <el-descriptions-item label="申请学分">
            <el-tag size="small" type="warning">{{ detailRow.creditScore }} 分</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="学年">{{ detailRow.academicYear }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusType[detailRow.status]" size="small" effect="plain" round>{{ statusMap[detailRow.status] }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ detailRow.createTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="审批时间">{{ detailRow.auditTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="审批备注" :span="2">
            <span v-if="detailRow.auditRemark" :style="{ color: detailRow.status === 1 ? '#67c23a' : detailRow.status === 2 ? '#f56c6c' : '#909399' }">{{ detailRow.auditRemark }}</span>
            <span v-else style="color: #c0c4cc">暂未审批</span>
          </el-descriptions-item>
        </el-descriptions>
        <div v-if="detailRow.imageUrl" style="margin-top: 20px">
          <div style="font-size: 14px; font-weight: 600; margin-bottom: 10px; color: #303133; display: flex; align-items: center; gap: 6px">
            <el-icon color="#409EFF"><Picture /></el-icon> 证明图片
          </div>
          <div style="display: flex; flex-wrap: wrap; gap: 10px">
            <el-image
              v-for="(url, i) in detailRow.imageUrl.split(',')"
              :key="i"
              :src="url"
              :preview-src-list="detailRow.imageUrl.split(',')"
              :initial-index="i"
              style="width: 170px; height: 130px; border-radius: 10px"
              fit="cover"
            >
              <template #error>
                <div style="display: flex; align-items: center; justify-content: center; height: 100%; background: #f5f7fa; color: #909399; font-size: 12px; border-radius: 10px">加载失败</div>
              </template>
            </el-image>
          </div>
        </div>
        <div v-else style="margin-top: 20px; color: #c0c4cc; font-size: 13px; text-align: center">暂无证明图片</div>
      </template>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, View, Close, Picture } from '@element-plus/icons-vue'
import { getMyAllApplies, withdrawApply } from '@/api/apply'

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
const records = ref([])
const loading = ref(false)
const detailVisible = ref(false)
const detailRow = ref(null)

const statusMap = { 0: '待审核', 1: '已批准', 2: '已驳回' }
const statusType = { 0: 'warning', 1: 'success', 2: 'danger' }

const totalScore = computed(() => {
  if (!records.value || records.value.length === 0) return 0
  return records.value
    .filter((r) => r.status === 1)
    .reduce((sum, r) => sum + (Number(r.creditScore) || 0), 0)
    .toFixed(2)
    .replace(/\.?0+$/, '')
})

async function fetchRecords() {
  loading.value = true
  try {
    const res = await getMyAllApplies(userInfo.id)
    records.value = res.data || []
  } finally { loading.value = false }
}

function handleDetail(row) {
  detailRow.value = row
  detailVisible.value = true
}

async function handleWithdraw(row) {
  try {
    await ElMessageBox.confirm('确定要撤回该申领记录吗？', '提示', { confirmButtonText: '确定撤回', cancelButtonText: '取消', type: 'warning' })
  } catch { return }
  try {
    await withdrawApply(row.id)
    ElMessage.success('撤回成功')
    fetchRecords()
  } catch { }
}

onMounted(fetchRecords)
</script>

<style scoped>
.score-badge {
  background: linear-gradient(135deg, #ecfdf5, #d1fae5);
  border: 1px solid #a7f3d0;
  border-radius: 12px;
  padding: 8px 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}
.score-label { font-size: 13px; color: #059669; }
.score-value { font-size: 24px; font-weight: 800; color: #10b981; }

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
</style>
