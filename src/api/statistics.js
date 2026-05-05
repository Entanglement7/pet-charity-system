import request from '@/utils/request'

export function getStatisticsOverview() {
  return request({ url: '/api/statistics/index', method: 'get' })
}
