import request from './request'

export function getOwners(params) {
  return request.get('/owners', { params })
}

export function getMyOwnerInfo() {
  return request.get('/owners/my')
}

export function getOwnerByUserId(userId) {
  return request.get(`/owners/${userId}/info`)
}

export function getOwnerHouses(ownerId) {
  return request.get(`/owners/${ownerId}/houses`)
}

export function bindHouse(data) {
  return request.post('/owners/bind-house', data)
}

export function unbindHouse(relId) {
  return request.delete(`/owners/unbind-house/${relId}`)
}

export function updateOwnerProfile(id, data) {
  return request.put(`/owners/${id}/profile`, data)
}
