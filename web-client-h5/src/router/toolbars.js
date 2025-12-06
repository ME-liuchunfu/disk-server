
import HomeView from "@/views/layout/HomeView.vue"
import DiskView from "@/views/layout/DiskView.vue";
import MineView from "@/views/layout/MineView.vue";

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
        path: '/disk',
        name: 'DiskView',
        component: DiskView,
        meta: {
            title: '磁盘',
            icon: 'ChromeFilled',
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
