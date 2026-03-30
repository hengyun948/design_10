import request from './request'

export function getNotices(params) {
  return request.get('/notices', { params })
}

export function getPublishedNotices(params) {
  return request.get('/notices/published', { params })
}

export function getNoticeDetail(id) {
  return request.get(`/notices/${id}`)
}

export function addNotice(data) {
  return request.post('/notices', data)
}

export function updateNotice(id, data) {
  return request.put(`/notices/${id}`, data)
}

export function publishNotice(id) {
  return request.post(`/notices/${id}/publish`)
}

export function unpublishNotice(id) {
  return request.post(`/notices/${id}/unpublish`)
}

export function deleteNotice(id) {
  return request.delete(`/notices/${id}`)
}
