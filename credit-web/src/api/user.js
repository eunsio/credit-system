import request from './request'

export function login(username, password) {
  return request.post('/auth/login', { username, password })
}

export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/user/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

export function getUserInfo(id) {
  return request.get(`/user/${id}`)
}

export function updateUserInfo(data) {
  return request.put('/user/update', data)
}

export function updatePassword(id, oldPassword, newPassword) {
  return request.put('/user/password', null, {
    params: { id, oldPassword, newPassword },
  })
}

export function getInstructors(page = 1, size = 10) {
  return request.get('/user/instructors', { params: { page, size } })
}

export function getInstructorById(id) {
  return request.get(`/user/instructor/${id}`)
}

export function addInstructor(data) {
  return request.post('/user/instructor', data)
}

export function updateInstructor(data) {
  return request.put('/user/instructor', data)
}

export function deleteInstructor(id) {
  return request.delete(`/user/instructor/${id}`)
}

export function getAllStudents(page = 1, size = 9999) {
  return request.get('/user/students', { params: { page, size } })
}

export function deleteFile(filename) {
  return request.delete(`/user/file/${filename}`)
}
