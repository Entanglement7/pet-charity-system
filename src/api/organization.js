import request from '@/utils/request'

export function registerOrganization(data) {
  return request({
    url: '/api/organization/register',
    method: 'post',
    data
  })
}

export function getOrganizationInfo() {
  return request({
    url: '/api/organization/info',
    method: 'get'
  })
}

export function updateOrganizationInfo(data) {
  return request({
    url: '/api/organization/update',
    method: 'put',
    data
  })
}

export function getVerificationStatus() {
  return request({
    url: '/api/organization/verification/status',
    method: 'get'
  })
}

export function getAddressList() {
  return request({
    url: '/api/organization/address/list',
    method: 'get'
  })
}

export function addAddress(data) {
  return request({
    url: '/api/organization/address/add',
    method: 'post',
    data
  })
}

export function updateAddress(id, data) {
  return request({
    url: `/api/organization/address/update/${id}`,
    method: 'put',
    data
  })
}

export function deleteAddress(id) {
  return request({
    url: `/api/organization/address/delete/${id}`,
    method: 'delete'
  })
}
