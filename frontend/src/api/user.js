import request from './axios'

// 用户登录
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

// 用户注册
export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

// 获取所有用户
export function getUsers() {
  return request({
    url: '/users',
    method: 'get'
  })
}

// 获取单个用户
export function getUser(id) {
  return request({
    url: `/users/${id}`,
    method: 'get'
  })
}

// 更新用户信息
export function updateUser(id, data) {
  return request({
    url: `/users/${id}`,
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/users/${id}`,
    method: 'delete'
  })
}