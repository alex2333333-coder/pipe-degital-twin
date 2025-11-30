import request from './axios'

// 获取管道数据
export const getPipeData = () => {
  return request({
    url: '/api/pipe/data',
    method: 'get'
  })
}

// 获取管道详情
export const getPipeDetail = (id) => {
  return request({
    url: `/api/pipe/${id}`,
    method: 'get'
  })
}

// 获取监测点数据
export const getMonitoringData = (pointId) => {
  return request({
    url: `/api/monitoring/${pointId}`,
    method: 'get'
  })
}

// 获取历史数据
export const getHistoricalData = (params) => {
  return request({
    url: '/api/pipe/historical',
    method: 'get',
    params
  })
}

// 获取报警数据
export const getAlarmData = () => {
  return request({
    url: '/api/pipe/alarm',
    method: 'get'
  })
}

// 保存模拟历史数据
export const saveSimulationHistory = (history) => {
  return request({
    url: '/simulation-history',
    method: 'post',
    data: history
  })
}

// 获取模拟历史数据
export const getSimulationHistory = () => {
  return request({
    url: '/simulation-history',
    method: 'get'
  })
}

// 获取最新模拟历史数据
export const getLatestSimulationHistory = () => {
  return request({
    url: '/simulation-history/latest',
    method: 'get'
  })
}

// 清空模拟历史数据
export const clearSimulationHistory = () => {
  return request({
    url: '/simulation-history/clear',
    method: 'delete'
  })
}