import request from '@/utils/request'

export function publishDonation(data) {
  return request({
    url: '/api/donation/publish',
    method: 'post',
    data
  })
}

export function getDonationList(params) {
  return request({
    url: '/api/donation/list',
    method: 'get',
    params
  })
}

export function getDonationDetail(id) {
  return request({
    url: `/api/donation/detail/${id}`,
    method: 'get'
  })
}

export function updateDonation(id, data) {
  return request({
    url: `/api/donation/update/${id}`,
    method: 'put',
    data
  })
}

export function deleteDonation(id) {
  return request({
    url: `/api/donation/delete/${id}`,
    method: 'delete'
  })
}

export function getLogistics(id) {
  return request({
    url: `/api/donation/logistics/${id}`,
    method: 'get'
  })
}

export function confirmReceipt(id) {
  return request({
    url: `/api/donation/confirm/${id}`,
    method: 'post'
  })
}
