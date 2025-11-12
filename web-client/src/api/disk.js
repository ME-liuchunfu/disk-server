import request from '@/utils/request'

export const diskAPI = {
    // 扫描文件
    scan: (data) => request.post('/api/disk/dir/ref/manager/scan', data),
    // 新增
    add: (data) => request.post('/api/disk/dir/ref/manager/add', data),
    update: (data) => request.post('/api/disk/dir/ref/manager/update', data),
    del: (data) => request.post('/api/disk/dir/ref/manager/del', data),
}
