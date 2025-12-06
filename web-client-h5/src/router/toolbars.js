

export const toolBarRouter = [
    {
        path: '/home',
        name: 'HomeView',
        component: ()=>import("@/views/HomeView.vue"),
        meta: {
            title: '首页',
            icon: 'HomeFilled',
            showTabBar: true // 显示底部导航
        }
    },
    {
        path: '/mine',
        name: 'MineView',
        component: ()=>import("@/views/MineView.vue"),
        meta: {
            title: '我的',
            icon: 'UserFilled',
            showTabBar: true
        }
    },
]
