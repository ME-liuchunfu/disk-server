// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'

// 导入页面组件
import DiskView from '@/views/DiskView.vue'  // 我的文件（默认页面）
import SharedView from '@/views/SharedView.vue'  // 共享文件
// import RecycleView from '../views/RecycleView.vue'  // 回收站
// import FavoritesView from '../views/FavoritesView.vue'  // 收藏夹
// import SettingsView from '../views/SettingsView.vue'  // 设置页面、

// 路由规则
const routes = [
    {
        path: '/',
        name: 'Home',
        redirect: '/disk'  // 默认跳转到"我的文件"页面
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
    },
    // {
    //     path: '/favorites',
    //     name: 'Favorites',
    //     component: FavoritesView,
    //     meta: {
    //         title: '收藏夹',
    //         icon: 'Star'
    //     }
    // },
    // {
    //     path: '/recycle',
    //     name: 'Recycle',
    //     component: RecycleView,
    //     meta: {
    //         title: '回收站',
    //         icon: 'Delete'
    //     }
    // },
    // {
    //     path: '/settings',
    //     name: 'Settings',
    //     component: SettingsView,
    //     meta: {
    //         title: '设置',
    //         icon: 'Setting'
    //     }
    // },
    // 404页面
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: () => import('../views/NotFound.vue'),
        meta: { title: '页面不存在' }
    }
]

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