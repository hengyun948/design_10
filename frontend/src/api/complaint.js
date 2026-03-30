import request from './request'

export function getComplaints(params) {
  return request.get('/complaints', { params })
}

export function getMyComplaints(params) {
  return request.get('/complaints/my', { params })
}

export function getComplaintDetail(id) {
  return request.get(`/complaints/${id}`)
}

export function submitComplaint(data) {
  return request.post('/complaints', data)
}

export function handleComplaint(id) {
  return request.post(`/complaints/${id}/handle`)
}

export function replyComplaint(id, data) {
  return request.post(`/complaints/${id}/reply`, data)
}

export function deleteComplaint(id) {
  return request.delete(`/complaints/${id}`)
}
