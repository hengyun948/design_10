import request from './request'

export function getActivities(params) {
  return request.get('/activities', { params })
}

export function getPublishedActivities(params) {
  return request.get('/activities/published', { params })
}

export function getActivityDetail(id) {
  return request.get(`/activities/${id}`)
}

export function addActivity(data) {
  return request.post('/activities', data)
}

export function updateActivity(id, data) {
  return request.put(`/activities/${id}`, data)
}

export function cancelActivity(id) {
  return request.post(`/activities/${id}/cancel`)
}

export function finishActivity(id) {
  return request.post(`/activities/${id}/finish`)
}

export function signupActivity(id) {
  return request.post(`/activities/${id}/signup`)
}

export function cancelSignupActivity(id) {
  return request.post(`/activities/${id}/cancelSignup`)
}

export function getActivitySignups(id) {
  return request.get(`/activities/${id}/signups`)
}
