import request from './request'

export function getPosts(page = 1, size = 10) {
  return request.get('/community/list', { params: { page, size } })
}

export function addPost(data) {
  return request.post('/community/post', data)
}

export function deletePost(id) {
  return request.delete(`/community/${id}`)
}

export function getMyPosts(userId) {
  return request.get(`/community/my-posts/${userId}`)
}

export function updatePost(data) {
  return request.put('/community/update', data)
}

export function getPostById(id) {
  return request.get(`/community/${id}`)
}

export function addReply(data) {
  return request.post('/community/reply', data)
}

export function getReplies(postId) {
  return request.get(`/community/replies/${postId}`)
}

export function deleteReply(id) {
  return request.delete(`/community/reply/${id}`)
}
