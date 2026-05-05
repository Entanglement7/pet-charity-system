import request from '@/utils/request'

export function getPublicityRecords(params) {
  return request({ url: '/api/publicity/records', method: 'get', params })
}

export function getPublicityFlow(params) {
  return request({ url: '/api/publicity/flow', method: 'get', params })
}
