import request from './request'

export function getHouses(params) {
  return request.get('/houses', { params })
}

export function getAllHouses() {
  return request.get('/houses/all')
}

export function getHouseDetail(id) {
  return request.get(`/houses/${id}`)
}

export function addHouse(data) {
  return request.post('/houses', data)
}

export function updateHouse(id, data) {
  return request.put(`/houses/${id}`, data)
}

export function deleteHouse(id) {
  return request.delete(`/houses/${id}`)
}
