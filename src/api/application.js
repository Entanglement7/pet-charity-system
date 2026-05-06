import request from '@/utils/request'

export function getAvailableItems(params) {
  return request({
    url: '/api/application/available',
    method: 'get',
    params
  })
}

export function applyForItem(data) {
  return request({
    url: '/api/application/apply',
    method: 'post',
    data
  })
}

export function getApplicationRecords(params) {
  return request({
    url: '/api/application/records',
    method: 'get',
    params
  })
}

export function getApplicationDetail(id) {
  return request({
    url: `/api/application/detail/${id}`,
    method: 'get'
  })
}

export function getApplicationLogistics(id) {
  return request({
    url: `/api/application/logistics/${id}`,
    method: 'get'
  })
}

export function confirmApplicationReceipt(id) {
  return request({
    url: `/api/application/confirm/${id}`,
    method: 'post'
  })
}

export function submitUsageReport(id, report) {
  return request({
    url: `/api/application/usage/${id}`,
    method: 'post',
    data: { report }
  })
}
