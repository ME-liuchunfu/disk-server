import {eventCi, eventBus} from "@/utils/eventBus";
import {ElMessage} from "element-plus";
import router from "@/router";


const EVENT_KEYS = {
    PAGE_CHANGE: 'router:change'
}

eventBus.on(EVENT_KEYS.PAGE_CHANGE, data => {
    const type = data['type'];
    if (type === 'b') {
        router.back();
    } else if (type === 'g') {
        const page = data['value']
        router.push(page);
    }
})

const codeHandler = {
    404: (msg) => ElMessage.error(msg || '请求的接口不存在'),
    500: (msg) => ElMessage.error(msg || '服务器内部错误'),
}

export const routerEvent = {
    go: (page)=> eventCi(EVENT_KEYS.PAGE_CHANGE, 'g', page),
    back: ()=> eventCi(EVENT_KEYS.PAGE_CHANGE, 'b'),
    auth: (res)=> {
        ElMessage.error('登录已过期，请重新登录')
        localStorage.clear();
        routerEvent.go('/login')
        return Promise.reject(res.msg || '未授权访问');
    },
    login() {
        localStorage.clear();
        ElMessage.success('退出登录成功')
        routerEvent.go('/login')
    },
    primis: (res) => {
        if (res.code === 200) {
            return;
        }
        if (res.code === 401) return routerEvent.auth;
    },
    reponseCode: (error) => {
        // 网络错误处理
        if (!error.response) {
            ElMessage.error('网络连接异常，请检查网络')
            return Promise.reject(error)
        }
        const code = error.response.status;
        if (codeHandler[code]) {
            codeHandler[code](code);
        } else {
            ElMessage.error(`请求失败（${code}）`)
        }
    },
    reponseDataCode: (code, msg) => {
        if (codeHandler[code]) {
            codeHandler[code](msg);
        }
    },
    home: ()=> routerEvent.go('/home'),
    mine: ()=> routerEvent.go('/mine'),
}
