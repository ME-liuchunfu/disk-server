import axios from 'axios'
import { ElMessage, ElLoading } from 'element-plus'
import router from '@/router'

// import JSONbig from 'json-bigint';
//
// // 创建自定义 JSON 解析器，将大整数转为字符串（或 BigInt）
// const jsonParser = JSONbig({
//     storeAsString: true, // 关键配置：将大整数转为字符串（避免 BigInt 类型在部分场景下的兼容性问题）
//     // 若需要使用 BigInt 类型，可改为：storeAsBigInt: true
// });


console.log('当前环境：', process.env.NODE_ENV)
console.log('API基础路径：', process.env.VUE_APP_BASE_API)

// 创建 axios 实例
const service = axios.create({
    baseURL: process.env.VUE_APP_BASE_API, // 基础URL（从环境变量读取）
    timeout: 5000, // 超时时间
    headers: {
        'Content-Type': 'application/json;charset=utf-8'
    },
    // 自定义响应转换
    // transformResponse: [function (data) {
    //     if (typeof data === 'string') {
    //         try {
    //             // 使用自定义解析器替代默认的 JSON.parse
    //             return jsonParser.parse(data);
    //         } catch (e) {
    //             console.error('JSON 解析失败:', e);
    //             return data;
    //         }
    //     }
    //     return data;
    // }]
})

// 加载动画实例
let loadingInstance = null

// 请求拦截器
service.interceptors.request.use(
    (config) => {
        // 显示加载动画（除了登录接口）
        if (config.url !== '/api/auth/login') {
            loadingInstance = ElLoading.service({
                lock: true,
                text: '加载中...',
                background: 'rgba(0, 0, 0, 0.1)'
            })
        }

        // 添加 token（登录后从本地存储获取）
        const tokenValue = localStorage.getItem('tokenValue');
        const tokenName = localStorage.getItem('tokenName');
        if (tokenName && tokenValue) {
            config.headers[tokenName] = tokenValue
        }

        return config
    },
    (error) => {
        // 关闭加载动画
        if (loadingInstance) loadingInstance.close()
        ElMessage.error('请求参数错误')
        return Promise.reject(error)
    }
)

// 响应拦截器
service.interceptors.response.use(
    (response) => {
        // 关闭加载动画
        if (loadingInstance) loadingInstance.close()

        const res = response.data

        // 自定义业务状态码处理
        if (res.code !== 200) {
            // 401: 未登录或token过期
            if (res.code === 401) {
                ElMessage.error('登录已过期，请重新登录')
                // 清除登录状态并跳转到登录页
                localStorage.clear();
                router.push('/login')
                return Promise.reject(res.msg || '未授权访问')
            }

            // 其他错误状态码
            // ElMessage.error(res.msg || '操作失败')
            return Promise.reject(res.msg || '接口请求失败')
        }

        // 成功返回数据
        return res.data
    },
    (error) => {
        // 关闭加载动画
        if (loadingInstance) loadingInstance.close()

        // 网络错误处理
        if (!error.response) {
            ElMessage.error('网络连接异常，请检查网络')
            return Promise.reject(error)
        }

        // HTTP状态码处理
        const status = error.response.status
        switch (status) {
            case 404:
                ElMessage.error('请求的接口不存在')
                break
            case 500:
                ElMessage.error('服务器内部错误')
                break
            default:
                ElMessage.error(`请求失败（${status}）`)
        }

        return Promise.reject(error)
    }
)

export default service
