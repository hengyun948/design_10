import request from './request'

export function getInspections(params) {
  return request.get('/inspections', { params })
}

export function getMyInspections(params) {
  return request.get('/inspections/my', { params })
}

export function getFacilities() {
  return request.get('/inspections/facilities')
}

export function addInspection(data) {
  return request.post('/inspections', data)
}

export function deleteInspection(id) {
  return request.delete(`/inspections/${id}`)
}
