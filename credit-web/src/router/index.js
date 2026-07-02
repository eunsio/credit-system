import { createRouter, createWebHistory } from 'vue-router'

function getHomePathByRole() {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  if (userInfo.role === 'ADMIN') {
    return '/dashboard'
  }
  if (userInfo.role === 'STUDENT') {
    return '/my-awards'
  }
  return '/competitions'
}

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
  },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    redirect: () => getHomePathByRole(),
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/AdminDashboard.vue'),
        meta: { title: '数据统计', roles: ['ADMIN'] },
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { title: '个人信息' },
      },
      {
        path: 'security',
        name: 'Security',
        component: () => import('@/views/user/Security.vue'),
        meta: { title: '安全中心' },
      },
      {
        path: 'competitions',
        name: 'Competitions',
        component: () => import('@/views/competition/CompetitionList.vue'),
        meta: { title: '比赛信息' },
      },
      {
        path: 'my-awards',
        name: 'MyAwards',
        component: () => import('@/views/apply/MyApplies.vue'),
        meta: { title: '我的获奖', roles: ['STUDENT'] },
      },
      {
        path: 'apply',
        name: 'Apply',
        component: () => import('@/views/apply/ApplyManage.vue'),
        meta: { title: '学分申领', roles: ['STUDENT'] },
      },
      {
        path: 'apply-detail',
        name: 'ApplyDetail',
        component: () => import('@/views/apply/ApplyDetail.vue'),
        meta: { title: '申领详情', roles: ['STUDENT'] },
      },
      {
        path: 'apply-audit',
        name: 'ApplyAudit',
        component: () => import('@/views/apply/ApplyManage.vue'),
        meta: { title: '审批管理', roles: ['ADMIN'] },
      },
      {
        path: 'community',
        name: 'Community',
        component: () => import('@/views/community/CommunityList.vue'),
        meta: { title: '技术社区' },
      },
      {
        path: 'my-posts',
        name: 'MyPosts',
        component: () => import('@/views/community/MyPosts.vue'),
        meta: { title: '我的帖子', roles: ['STUDENT'] },
      },
      {
        path: 'instructors',
        name: 'Instructors',
        component: () => import('@/views/instructor/InstructorList.vue'),
        meta: { title: '指导教师' },
      },
      {
        path: 'student-records',
        name: 'StudentRecords',
        component: () => import('@/views/admin/StudentRecords.vue'),
        meta: { title: '学生数据', roles: ['ADMIN'] },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404',
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  if (to.path !== '/login' && !token) {
    next('/login')
    return
  }
  if (to.path === '/login' && token) {
    next(getHomePathByRole())
    return
  }
  if (to.path === '/') {
    next(getHomePathByRole())
    return
  }
  if (to.meta?.roles && !to.meta.roles.includes(userInfo.role)) {
    next(getHomePathByRole())
    return
  }
  next()
})

export default router
