import request from '@/utils/request'

// 登录相关接口
export const authAPI = {
    // 登录
    login: (data) => request.post('/api/auth/login', data),
    // 登出
    logout: () => request.post('/api/auth/logout')
}