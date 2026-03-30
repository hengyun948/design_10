import request from './request'

export function getRepairs(params) {
  return request.get('/repairs', { params })
}

export function getMyRepairs(params) {
  return request.get('/repairs/my', { params })
}

export function getAssignedRepairs(params) {
  return request.get('/repairs/assigned', { params })
}

export function getWorkerStats() {
  return request.get('/repairs/worker-stats')
}

export function getRepairDetail(id) {
  return request.get(`/repairs/${id}`)
}

export function getRepairRecords(id) {
  return request.get(`/repairs/${id}/records`)
}

export function submitRepair(data) {
  return request.post('/repairs', data)
}

export function acceptRepair(id) {
  return request.post(`/repairs/${id}/accept`)
}

export function assignRepair(id, data) {
  return request.post(`/repairs/${id}/assign`, data)
}

export function receiveRepair(id) {
  return request.post(`/repairs/${id}/receive`)
}

export function finishRepair(id, data) {
  return request.post(`/repairs/${id}/finish`, data)
}

export function cancelRepair(id, data) {
  return request.post(`/repairs/${id}/cancel`, data)
}
