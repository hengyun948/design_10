import request from './request'

export function getUsers(params) {
  return request.get('/users', { params })
}

export function addUser(data) {
  return request.post('/users', data)
}

export function updateUser(id, data) {
  return request.put(`/users/${id}`, data)
}

export function deleteUser(id) {
  return request.delete(`/users/${id}`)
}

export function updatePassword(oldPassword, newPassword) {
  return request.put('/users/password', null, { params: { oldPassword, newPassword } })
}

export function getRoles() {
  return request.get('/roles')
}
