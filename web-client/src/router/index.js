// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'

// 导入页面组件
import LoginView from '@/views/LoginView.vue'  // 登录页
import DiskView from '@/views/DiskView.vue'  // 我的文件（默认页面）
import SharedView from '@/views/SharedView.vue'  // 共享文件
import RegisterView from '@/views/RegisterView.vue'
import NotFound from "@/views/NotFound.vue";

// 路由守卫：验证登录状态
const requireAuth = (to, from, next) => {
    const isLogin = localStorage.getItem('isLogin') === 'true'
    if (isLogin || to.meta['ignore']) {
        next()  // 已登录，放行
    } else {
        next('/login')  // 未登录，跳转到登录页
    }
}

// 路由规则
const routes = [
    {
        path: '/',
        redirect: '/disk'
    },
    {
        path: '/login',
        name: 'Login',
        component: LoginView,
        meta: {
            title: '登录'
        }
    },
    {
        path: '/register',
        name: 'Register',
        component: RegisterView,
        meta: { title: '注册' }
    },
    {
        path: '/disk',
        name: 'MyDisk',
        component: DiskView,
        meta: {
            title: '我的文件',
            icon: 'Star'  // 对应Element Plus的图标名称
        }
    },
    {
        path: '/shared',
        name: 'Shared',
        component: SharedView,
        meta: {
            title: '共享文件',
            icon: 'Share'
        }
    }
]

routes.push({
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFound,
    meta: { title: '页面不存在' }
})

const inArrValue = (arr, value) => {
    for (let k in arr) {
        if (arr[k] === value) {
            return true;
        }
    }
    return false;
}

routes.forEach(item=>{
   if (!inArrValue(['/', '/login', '/register'], item.path)) {
       item['beforeEnter'] = requireAuth
   }
});

// 创建路由实例
const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),  // 使用HTML5 history模式
    routes
})

// 全局导航守卫：动态修改页面标题
router.beforeEach((to, from, next) => {
    if (to.meta.title) {
        document.title = `云网盘 - ${to.meta.title}`
    } else {
        document.title = '云网盘'
    }
    next()
})

export default router
