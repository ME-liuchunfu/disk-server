import HomeView from "@/views/HomeView.vue";
import MineView from "@/views/MineView.vue";

export const toolBarRouter = [
    {
        path: '/home',
        name: 'HomeView',
        component: HomeView,
        meta: {
            title: '首页',
            icon: 'HomeFilled',
            showTabBar: true // 显示底部导航
        }
    },
    {
        path: '/mine',
        name: 'MineView',
        component: MineView,
        meta: {
            title: '我的',
            icon: 'UserFilled',
            showTabBar: true
        }
    },
]
