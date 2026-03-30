import request from './request'

export function getFacilities(params) {
  return request.get('/facilities', { params })
}

export function addFacility(data) {
  return request.post('/facilities', data)
}

export function updateFacility(id, data) {
  return request.put(`/facilities/${id}`, data)
}

export function deleteFacility(id) {
  return request.delete(`/facilities/${id}`)
}
