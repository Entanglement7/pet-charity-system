import request from '@/utils/request'

export function getOrganizationAuditList(params) {
  return request({
    url: '/api/audit/organization/list',
    method: 'get',
    params
  })
}

export function auditOrganization(id, data) {
  return request({
    url: `/api/audit/organization/${id}`,
    method: 'post',
    data
  })
}

export function getDonationAuditList(params) {
  return request({
    url: '/api/audit/donation/list',
    method: 'get',
    params
  })
}

export function auditDonation(id, data) {
  return request({
    url: `/api/audit/donation/${id}`,
    method: 'post',
    data
  })
}

export function getApplicationAuditList(params) {
  return request({
    url: '/api/audit/application/list',
    method: 'get',
    params
  })
}

export function auditApplication(id, data) {
  return request({
    url: `/api/audit/application/${id}`,
    method: 'post',
    data
  })
}

export function getAuditHistory(params) {
  return request({
    url: '/api/audit/history',
    method: 'get',
    params
  })
}
