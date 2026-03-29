import request from './request'

export function getBills(params) {
  return request.get('/bills', { params })
}

export function getMyBills(params) {
  return request.get('/bills/my', { params })
}

export function getBillDetail(id) {
  return request.get(`/bills/${id}`)
}

export function addBill(data) {
  return request.post('/bills', data)
}

export function payBill(id) {
  return request.post(`/bills/${id}/pay`)
}

export function getPaymentRecords(id) {
  return request.get(`/bills/${id}/payments`)
}

export function deleteBill(id) {
  return request.delete(`/bills/${id}`)
}
