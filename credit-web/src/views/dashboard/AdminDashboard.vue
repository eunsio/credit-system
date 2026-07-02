<template>
  <div>
    <div class="page-header">
      <h2>数据统计</h2>
      <p>创新学分申领数据实时总览</p>
    </div>

    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :xs="12" :sm="6">
        <el-card shadow="never" class="stat-card" v-loading="loading">
          <div class="stat-inner">
            <div class="stat-icon-box" style="background: linear-gradient(135deg, #409EFF, #66b1ff)">
              <el-icon :size="22" color="#fff"><User /></el-icon>
            </div>
            <div class="stat-body">
              <div class="stat-label">学生总人数</div>
              <div class="stat-value">{{ data.totalStudents }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="never" class="stat-card" v-loading="loading">
          <div class="stat-inner">
            <div class="stat-icon-box" style="background: linear-gradient(135deg, #67c23a, #85ce61)">
              <el-icon :size="22" color="#fff"><Document /></el-icon>
            </div>
            <div class="stat-body">
              <div class="stat-label">申领总次数</div>
              <div class="stat-value">{{ data.totalApplies }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="never" class="stat-card" v-loading="loading">
          <div class="stat-inner">
            <div class="stat-icon-box" style="background: linear-gradient(135deg, #e6a23c, #eebe77)">
              <el-icon :size="22" color="#fff"><Clock /></el-icon>
            </div>
            <div class="stat-body">
              <div class="stat-label">待审核</div>
              <div class="stat-value">{{ data.pendingCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="never" class="stat-card" v-loading="loading">
          <div class="stat-inner">
            <div class="stat-icon-box" style="background: linear-gradient(135deg, #f56c6c, #f89898)">
              <el-icon :size="22" color="#fff"><Medal /></el-icon>
            </div>
            <div class="stat-body">
              <div class="stat-label">已批准总学分</div>
              <div class="stat-value">{{ data.totalCredits }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :xs="24" :lg="8">
        <el-card header="申领状态分布" shadow="never" v-loading="loading">
          <div ref="pieChartRef" style="height: 300px" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="16">
        <el-card header="各学年申领统计" shadow="never" v-loading="loading">
          <div ref="barChartRef" style="height: 300px" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :xs="24" :lg="24">
        <el-card header="各年级获奖统计" shadow="never" v-loading="loading">
          <el-table :data="data.gradeStats" size="small" max-height="300" stripe>
            <el-table-column prop="academicYear" label="学年" />
            <el-table-column prop="studentCount" label="获奖人数" />
            <el-table-column prop="awardCount" label="获奖次数" />
            <el-table-column prop="totalCredits" label="批准学分" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 学生数据统计 -->
    <el-card shadow="never" v-loading="loadingStudents">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon color="#409EFF" :size="18"><DataAnalysis /></el-icon>
            <span>学生列表</span>
          </div>
          <div class="header-right">
            <el-select v-model="academicYear" placeholder="选择学年" style="width: 180px; margin-right: 10px" size="small" @change="fetchStudents">
              <el-option v-for="y in yearOptions" :key="y" :label="y" :value="y" />
            </el-select>
            <el-input v-model="searchKeyword" placeholder="搜索学生姓名/学号..." clearable maxlength="50" style="width: 220px; margin-right: 10px" size="small" :prefix-icon="Search" @input="handleSearch" />
            <el-button type="primary" size="small" @click="fetchStudents">查询</el-button>
          </div>
        </div>
      </template>

      <div class="stats-bar">
        <div class="stat-item">
          <div class="stat-icon stat-icon-total">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ filteredStudents.length }}</div>
            <div class="stat-label">学生总数</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon stat-icon-credit">
            <el-icon><Medal /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ totalCredits }}</div>
            <div class="stat-label">学分总计</div>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon stat-icon-avg">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ avgCredits }}</div>
            <div class="stat-label">人均学分</div>
          </div>
        </div>
      </div>

      <el-table :data="pagedStudents" stripe style="width: 100%" class="clickable-table" @row-click="handleViewDetail">
        <el-table-column prop="id" label="#" width="55" align="center" />
        <el-table-column label="学生信息" min-width="200">
          <template #default="{ row }">
            <div class="student-cell">
              <el-avatar :size="36" :src="row.avatar">
                <el-icon :size="18"><UserFilled /></el-icon>
              </el-avatar>
              <div class="student-info">
                <div class="student-name">{{ row.nickname || row.username }}</div>
                <div class="student-id">学号：{{ row.username }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="已获学分" width="110" align="center">
          <template #default="{ row }">
            <span class="credit-chip">+{{ row.totalScore || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="获奖次数" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small" round type="success">{{ row.awardCount || 0 }} 次</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <button type="button" class="action-btn action-view" @click.stop="handleViewDetail(row)">
              <el-icon><View /></el-icon> 详情
            </button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="filteredStudents.length"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
        />
      </div>
    </el-card>

    <el-dialog v-model="detailVisible" title="学生详情" width="900px" destroy-on-close>
      <template v-if="currentStudent">
        <div class="student-detail-header">
          <el-avatar :size="64" :src="currentStudent.avatar">
            <el-icon :size="32"><UserFilled /></el-icon>
          </el-avatar>
          <div class="student-detail-info">
            <h3>{{ currentStudent.nickname || currentStudent.username }}</h3>
            <p>学号：{{ currentStudent.username }} | 手机号：{{ currentStudent.phone || '未填写' }}</p>
          </div>
          <div class="student-detail-score">
            <div class="score-big">{{ currentStudent.totalScore || 0 }}</div>
            <div class="score-unit">创新学分</div>
          </div>
        </div>

        <el-divider />

        <h4 style="margin-bottom: 16px; display: flex; align-items: center; gap: 8px">
          <el-icon color="#409EFF"><Medal /></el-icon>
          获奖记录（{{ studentAwards.length }} 条）
        </h4>

        <el-table :data="studentAwards" stripe size="small" v-loading="loadingAwards">
          <el-table-column prop="id" label="#" width="50" />
          <el-table-column prop="awardTitle" label="获奖名称" min-width="200" show-overflow-tooltip />
          <el-table-column label="获得学分" width="90" align="center">
            <template #default="{ row }">
              <span class="credit-chip-sm">+{{ row.creditScore }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="academicYear" label="学年" width="110" />
          <el-table-column prop="auditTime" label="审批时间" width="160" />
          <el-table-column prop="auditRemark" label="审批备注" min-width="150" show-overflow-tooltip>
            <template #default="{ row }">
              <span v-if="row.auditRemark" style="color: #67c23a; font-size: 12px">{{ row.auditRemark }}</span>
              <span v-else style="color: #c0c4cc; font-size: 12px">-</span>
            </template>
          </el-table-column>
        </el-table>

        <div v-if="!loadingAwards && studentAwards.length === 0" class="empty-state">
          <el-icon :size="48" color="#c0c4cc"><Document /></el-icon>
          <p>暂无获奖记录</p>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, reactive, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { User, UserFilled, Medal, DataAnalysis, TrendCharts, View, Search, Document } from '@element-plus/icons-vue'
import { getDashboard } from '@/api/apply'
import { getMyAwards } from '@/api/apply'
import { getAllStudents } from '@/api/user'

const data = reactive({
  totalStudents: 0, totalApplies: 0, pendingCount: 0,
  approvedCount: 0, rejectedCount: 0, totalCredits: 0,
  statusDistribution: [], yearStats: [], gradeStats: [], topStudents: [],
})
const loading = ref(false)
const router = useRouter()

const pieChartRef = ref(null)
const barChartRef = ref(null)
let pieChart = null
let barChart = null

function initPieChart() {
  if (!pieChartRef.value) return
  pieChart = echarts.init(pieChartRef.value)
  const itemCount = [
    { value: data.approvedCount || 0, name: '已批准' },
    { value: data.pendingCount || 0, name: '待审核' },
    { value: data.rejectedCount || 0, name: '已驳回' },
  ]
  pieChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    color: ['#67c23a', '#e6a23c', '#f56c6c'],
    series: [{
      type: 'pie',
      radius: ['55%', '78%'],
      center: ['50%', '46%'],
      emphasis: { label: { fontSize: 18, fontWeight: 'bold' } },
      label: { show: false },
      data: itemCount,
    }],
  })

  pieChart.on('click', (params) => {
    const statusMap = { '已批准': 'approved', '待审核': 'pending', '已驳回': 'rejected' }
    const tab = statusMap[params.name]
    if (tab) {
      router.push({ path: '/apply-audit', query: { status: tab } })
    }
  })
}

function initBarChart() {
  if (!barChartRef.value) return
  barChart = echarts.init(barChartRef.value)
  const years = (data.yearStats || []).map((i) => i.academicYear)
  const applyData = (data.yearStats || []).map((i) => i.applyCount || 0)
  const approvedData = (data.yearStats || []).map((i) => i.approvedCount || 0)
  barChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['申领总数', '已批准'], bottom: 0 },
    color: ['#409EFF', '#67c23a'],
    grid: { left: 8, right: 8, top: 10, bottom: 40 },
    xAxis: { type: 'category', data: years, axisLabel: { rotate: years.length > 5 ? 30 : 0 } },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      { name: '申领总数', type: 'bar', data: applyData, barMaxWidth: 36, itemStyle: { borderRadius: [4, 4, 0, 0] } },
      { name: '已批准', type: 'bar', data: approvedData, barMaxWidth: 36, itemStyle: { borderRadius: [4, 4, 0, 0] } },
    ],
  })
}

function getCurrentAcademicYear() {
  const now = new Date()
  const y = now.getFullYear()
  const m = now.getMonth() + 1
  return m >= 9 ? y + '-' + (y + 1) : (y - 1) + '-' + y
}

function buildYearOptions() {
  const current = getCurrentAcademicYear()
  const [base] = current.split('-').map(Number)
  const options = ['全部', current]
  for (let i = 1; i <= 3; i++) {
    options.push((base - i) + '-' + (base - i + 1))
  }
  return options
}

const loadingStudents = ref(false)
const loadingAwards = ref(false)
const students = ref([])
const searchKeyword = ref('')
const yearOptions = buildYearOptions()
const academicYear = ref(getCurrentAcademicYear())
const pageNum = ref(1)
const pageSize = ref(10)
const detailVisible = ref(false)
const currentStudent = ref(null)
const studentAwards = ref([])

const filteredStudents = computed(() => {
  if (!searchKeyword.value.trim()) return students.value
  const keyword = searchKeyword.value.toLowerCase()
  return students.value.filter(s =>
    (s.username || '').toLowerCase().includes(keyword) ||
    (s.nickname || '').toLowerCase().includes(keyword)
  )
})

const pagedStudents = computed(() => {
  const start = (pageNum.value - 1) * pageSize.value
  return filteredStudents.value.slice(start, start + pageSize.value)
})

const totalCredits = computed(() => {
  return students.value.reduce((sum, s) => sum + (Number(s.totalScore) || 0), 0).toFixed(1)
})

const avgCredits = computed(() => {
  if (!students.value.length) return '0'
  const total = students.value.reduce((sum, s) => sum + (Number(s.totalScore) || 0), 0)
  return (total / students.value.length).toFixed(1)
})

async function fetchStudents() {
  loadingStudents.value = true
  try {
    const res = await getAllStudents(1, 9999)
    let allUsers = res.data.records || []

    const enriched = []
    for (const u of allUsers) {
      try {
        if (academicYear.value === '全部') {
          const allYearOptions = yearOptions.filter(y => y !== '全部')
          let totalAwardCount = 0
          let totalScoreSum = 0
          for (const year of allYearOptions) {
            const awardRes = await getMyAwards(u.id, year)
            const awards = awardRes.data || []
            totalAwardCount += awards.length
            totalScoreSum += awards.reduce((sum, a) => sum + (Number(a.creditScore) || 0), 0)
          }
          enriched.push({
            ...u,
            awardCount: totalAwardCount,
            totalScore: totalScoreSum,
          })
        } else {
          const awardRes = await getMyAwards(u.id, academicYear.value)
          const awards = awardRes.data || []
          enriched.push({
            ...u,
            awardCount: awards.length,
            totalScore: awards.reduce((sum, a) => sum + (Number(a.creditScore) || 0), 0),
          })
        }
      } catch {
        enriched.push({ ...u, awardCount: 0, totalScore: 0 })
      }
    }

    students.value = enriched.sort((a, b) => (b.totalScore || 0) - (a.totalScore || 0))
    pageNum.value = 1
  } catch {
    ElMessage.error('获取学生数据失败')
  } finally {
    loadingStudents.value = false
  }
}

function handleSearch() {
  pageNum.value = 1
}

async function handleViewDetail(row) {
  currentStudent.value = row
  detailVisible.value = true
  loadingAwards.value = true
  try {
    const res = await getMyAwards(row.id, academicYear.value)
    studentAwards.value = res.data || []
  } catch {
    studentAwards.value = []
  } finally {
    loadingAwards.value = false
  }
}

function handleSizeChange(val) {
  pageSize.value = val
  const maxPage = Math.ceil(filteredStudents.value.length / pageSize.value) || 1
  if (pageNum.value > maxPage) {
    pageNum.value = maxPage
  }
}

function handleCurrentChange(val) {
  pageNum.value = val
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await getDashboard()
    Object.assign(data, res.data)
    initPieChart()
    initBarChart()
  } finally { loading.value = false }

  fetchStudents()
})

onUnmounted(() => {
  pieChart?.dispose()
  barChart?.dispose()
})
</script>

<style scoped>
.stat-card :deep(.el-card__body) { padding: 20px; }
.stat-inner { display: flex; align-items: center; gap: 16px; }
.stat-body { flex: 1; }
.stat-label { font-size: 13px; color: #909399; margin-bottom: 6px; }
.stat-value { font-size: 28px; font-weight: 700; color: #303133; line-height: 1; }

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
}

.stats-bar {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  padding: 18px 22px;
  background: linear-gradient(135deg, #f8fafc, #f1f5f9);
  border-radius: 10px;
  border: 1px solid var(--border-light);
}
.stat-item {
  display: flex;
  align-items: center;
  gap: 14px;
}
.stat-icon {
  width: 46px;
  height: 46px;
  border-radius: 11px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #fff;
}
.stat-icon-total { background: linear-gradient(135deg, #409EFF, #337ecc); }
.stat-icon-credit { background: linear-gradient(135deg, #67c23a, #529b2e); }
.stat-icon-avg { background: linear-gradient(135deg, #e6a23c, #d48806); }
.stat-info { display: flex; flex-direction: column; }
.stat-value { font-size: 22px; font-weight: 800; color: var(--text-primary); line-height: 1.2; }
.stat-label { font-size: 12px; color: var(--text-placeholder); margin-top: 2px; }

.student-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}
.student-info { display: flex; flex-direction: column; }
.student-name { font-weight: 600; font-size: 13px; color: var(--text-primary); }
.student-id { font-size: 11px; color: var(--text-placeholder); margin-top: 2px; }

.credit-chip {
  display: inline-block;
  padding: 3px 10px;
  font-size: 13px;
  font-weight: 700;
  color: #67c23a;
  background: rgba(103, 194, 58, 0.08);
  border: 1px solid rgba(103, 194, 58, 0.2);
  border-radius: 6px;
}
.credit-chip-sm {
  display: inline-block;
  padding: 2px 8px;
  font-size: 12px;
  font-weight: 600;
  color: #67c23a;
  background: rgba(103, 194, 58, 0.08);
  border-radius: 4px;
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
.action-btn:hover { transform: translateY(-1px); }
.action-view {
  color: var(--primary);
  background: rgba(64, 158, 255, 0.06);
}
.action-view:hover {
  background: rgba(64, 158, 255, 0.15);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.18);
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.student-detail-header {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 10px;
}
.student-detail-info { flex: 1; }
.student-detail-info h3 { font-size: 18px; font-weight: 700; color: var(--text-primary); margin: 0 0 6px; }
.student-detail-info p { font-size: 13px; color: var(--text-placeholder); margin: 0; }
.student-detail-score {
  text-align: center;
  padding: 12px 24px;
  background: linear-gradient(135deg, rgba(103, 194, 58, 0.08), rgba(103, 194, 58, 0.04));
  border: 1px solid rgba(103, 194, 58, 0.15);
  border-radius: 10px;
}
.score-big { font-size: 32px; font-weight: 800; color: #67c23a; line-height: 1; }
.score-unit { font-size: 12px; color: #67c23a; margin-top: 4px; opacity: 0.8; }

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #c0c4cc;
}
.empty-state p { margin-top: 12px; font-size: 14px; }

.clickable-table :deep(.el-table__body tr) {
  cursor: pointer;
  transition: background-color 0.2s ease;
}
</style>
