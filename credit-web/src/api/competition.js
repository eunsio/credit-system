import request from './request'

export function getCompetitions(page = 1, size = 10) {
  return request.get('/competition/list', { params: { page, size } })
}

export function getCompetitionById(id) {
  return request.get(`/competition/${id}`)
}

export function addCompetition(data) {
  return request.post('/competition/add', data)
}

export function updateCompetition(data) {
  return request.put('/competition/update', data)
}

export function deleteCompetition(id) {
  return request.delete(`/competition/${id}`)
}
