<template>
  <div>
    <div class="page-header animate-in">
      <h2>我的获奖记录</h2>
      <p>查看已批准的创新学分申领记录</p>
    </div>

    <el-card shadow="never" class="animate-in animate-in-delay-1">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <div style="display: flex; align-items: center; gap: 8px">
            <el-icon color="#409EFF" :size="18"><Medal /></el-icon>
            <span>获奖记录</span>
          </div>
          <div>
            <el-select v-model="academicYear" placeholder="选择学年" style="width: 180px; margin-right: 10px" size="small" @change="fetchRecords">
              <el-option v-for="y in yearOptions" :key="y" :label="y" :value="y" />
            </el-select>
            <el-button type="primary" size="small" @click="fetchRecords">查询</el-button>
          </div>
        </div>
      </template>
      <div class="total-score-wrap">
        <span class="total-label">已获创新学分总计</span>
        <span class="total-value">{{ totalScore }}</span>
        <span class="total-unit">分</span>
      </div>
      <el-table :data="records" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="awardTitle" label="获奖名称" min-width="200" show-overflow-tooltip />
        <el-table-column label="获得学分" width="110">
          <template #default="{ row }">
            <span class="credit-chip">+{{ row.creditScore }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="academicYear" label="学年" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <span class="status-approved-sm">
              <span class="dot-sm" />
              已批准
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="auditTime" label="审批时间" width="160" />
        <el-table-column prop="auditRemark" label="审批备注" min-width="180">
          <template #default="{ row }">
            <span v-if="row.auditRemark" style="color: var(--success)">{{ row.auditRemark }}</span>
            <span v-else style="color: var(--text-disabled)">-</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Medal } from '@element-plus/icons-vue'
import { getMyAwards } from '@/api/apply'

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

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
const records = ref([])
const loading = ref(false)
const yearOptions = buildYearOptions()
const academicYear = ref(getCurrentAcademicYear())

const totalScore = computed(() => {
  if (!records.value || records.value.length === 0) return 0
  return records.value
    .reduce((sum, r) => sum + (Number(r.creditScore) || 0), 0)
    .toFixed(2)
    .replace(/\.?0+$/, '')
})

async function fetchRecords() {
  loading.value = true
  try {
    const res = await getMyAwards(userInfo.id, academicYear.value)
    records.value = res.data || []
  } finally { loading.value = false }
}

onMounted(fetchRecords)
</script>

<style scoped>
.total-score-wrap {
  display: flex;
  align-items: baseline;
  gap: 6px;
  padding: 16px 20px;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.08), rgba(103, 194, 58, 0.08));
  border-radius: var(--radius-md);
  margin-bottom: 16px;
}
.total-label {
  font-size: 14px;
  color: var(--text-secondary);
  font-weight: 500;
}
.total-value {
  font-size: 32px;
  font-weight: 800;
  color: var(--primary);
  font-variant-numeric: tabular-nums;
}
.total-unit {
  font-size: 14px;
  color: var(--text-secondary);
}
.credit-chip {
  display: inline-block;
  padding: 2px 10px;
  background: rgba(103, 194, 58, 0.12);
  color: #67c23a;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 700;
}
.status-approved-sm {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 2px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  background: rgba(103, 194, 58, 0.12);
  color: #67c23a;
  border: 1px solid rgba(103, 194, 58, 0.25);
}
.dot-sm {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #67c23a;
}
</style>
