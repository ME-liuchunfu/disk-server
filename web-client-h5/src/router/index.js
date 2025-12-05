import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import MineView from '@/views/MineView.vue'
import LoginView from '@/views/LoginView.vue'

const routes = [
    {
        path: '/',
        redirect: '/home' // 默认跳转到首页
    },
    {
        path: '/home',
        name: 'HomeView',
        component: HomeView,
        meta: {
            showTabBar: true // 显示底部导航
        }
    },
    {
        path: '/mine',
        name: 'MineView',
        component: MineView,
        meta: {
            showTabBar: true,
            requiresAuth: true // 需要登录
        }
    },
    {
        path: '/login',
        name: 'LoginView',
        component: LoginView,
        meta: {
            showTabBar: false // 隐藏底部导航
        }
    }
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes,
    scrollBehavior() {
        return { top: 0 } // 路由切换时滚动到顶部
    }
})

// 路由守卫：未登录访问"我的"页面时跳转到登录页
router.beforeEach((to, from, next) => {
    const isLogin = localStorage.getItem('isLogin') === 'true'
    if (to.meta.requiresAuth && !isLogin) {
        next('/login')
    } else {
        next()
    }
})

export default router
