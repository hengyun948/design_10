import request from './request'

export function getParkingSpaces(params) {
  return request.get('/parking/spaces', { params })
}

export function addParkingSpace(data) {
  return request.post('/parking/spaces', data)
}

export function updateParkingSpace(id, data) {
  return request.put(`/parking/spaces/${id}`, data)
}

export function deleteParkingSpace(id) {
  return request.delete(`/parking/spaces/${id}`)
}

export function getParkingRecords(params) {
  return request.get('/parking/records', { params })
}

export function addParkingRecord(data) {
  return request.post('/parking/records', data)
}

export function getMyParkingRecords(params) {
  return request.get('/parking/records/my', { params })
}

export function payParkingRecord(id, data) {
  return request.post(`/parking/records/${id}/pay`, data)
}
