import request from '@/utils/request'

export function getPointsInfo() {
  return request({
    url: '/api/points/info',
    method: 'get'
  })
}

export function getPointsDetail(params) {
  return request({
    url: '/api/points/detail',
    method: 'get',
    params
  })
}

export function getExchangeList(params) {
  return request({
    url: '/api/points/exchange/list',
    method: 'get',
    params
  })
}

export function exchangeItem(data) {
  return request({
    url: '/api/points/exchange',
    method: 'post',
    data
  })
}

export function getExchangeRecords(params) {
  return request({
    url: '/api/points/exchange/records',
    method: 'get',
    params
  })
}

export function getExchangeLogistics(id) {
  return request({
    url: `/api/points/exchange/logistics/${id}`,
    method: 'get'
  })
}

export function confirmExchangeReceipt(id) {
  return request({
    url: `/api/points/exchange/confirm/${id}`,
    method: 'post'
  })
}

export function getUserPointsList(params) {
  return request({
    url: '/api/points/user/list',
    method: 'get',
    params
  })
}

export function addExchangeItem(data) {
  return request({ url: '/api/points/exchange/item', method: 'post', data })
}

export function updateExchangeItem(id, data) {
  return request({ url: `/api/points/exchange/item/${id}`, method: 'put', data })
}

export function deleteExchangeItem(id) {
  return request({ url: `/api/points/exchange/item/${id}`, method: 'delete' })
}
