import request from './request'

export function submitApply(data) {
  return request.post('/apply/submit', data)
}

export function getMyAwards(userId, academicYear) {
  const params = { userId }
  if (academicYear) params.academicYear = academicYear
  return request.get('/apply/my-awards', { params })
}

export function getMyAllApplies(userId) {
  return request.get(`/apply/my-all/${userId}`)
}

export function getTotalScore(userId) {
  return request.get(`/apply/total-score/${userId}`)
}

export function getAllApplies(page = 1, size = 10) {
  return request.get('/apply/all', { params: { page, size } })
}

export function auditApply(id, status, remark, auditorId) {
  return request.put('/apply/audit', null, {
    params: { id, status, remark, auditorId },
  })
}

export function getDashboard() {
  return request.get('/apply/stat/dashboard')
}

export function deleteApply(id) {
  return request.delete(`/apply/delete/${id}`)
}

export function withdrawApply(id) {
  return request.delete(`/apply/withdraw/${id}`)
}
