import request from '@/utils/request'

export function getLogisticsInfo(id, type) {
  return request({
    url: '/api/logistics/info',
    method: 'get',
    params: { id, type }
  })
}

export function updateLogistics(data) {
  return request({
    url: '/api/logistics/update',
    method: 'post',
    data
  })
}

export function getLogisticsCompanies() {
  return request({
    url: '/api/logistics/companies',
    method: 'get'
  })
}
