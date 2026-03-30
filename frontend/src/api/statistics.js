import request from './request'

export function getOverview() {
  return request.get('/statistics/overview')
}

export function getRepairStatus() {
  return request.get('/statistics/repair-status')
}

export function getBillStatus() {
  return request.get('/statistics/bill-status')
}

export function getHouseStatus() {
  return request.get('/statistics/house-status')
}

export function getParkingStatus() {
  return request.get('/statistics/parking-status')
}

export function getComplaintCategory() {
  return request.get('/statistics/complaint-category')
}
