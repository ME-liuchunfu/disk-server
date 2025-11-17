// src/main.js
import { createApp } from 'vue'
import App from '@/App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'video.js/dist/video-js.css'
import 'highlight.js/styles/atom-one-dark.css'
import router from '@/router'  // 引入路由
import * as ElementPlusIconsVue from '@element-plus/icons-vue' // 导入所有图标
import 'normalize.css'
import '@/assets/css/base.css'
import '@/assets/css/scrollbar.css'
import { createPinia } from 'pinia'
import Viewer from 'v-viewer'
import 'viewerjs/dist/viewer.css' // 引入样式

const app = createApp(App);
const pinia = createPinia();
app.use(ElementPlus);
app.use(router);  // 使用路由
app.use(pinia);

// 图片预览插件
app.use(Viewer, {
    defaultOptions: {
        zIndex: 9999, // 预览层的层级
        inline: false, // 是否以内联形式展示
        button: true, // 是否显示关闭按钮
        navbar: true, // 是否显示缩略图导航栏
        title: true, // 是否显示图片标题
        toolbar: true, // 是否显示工具栏
        tooltip: true, // 缩放时是否显示百分比
        movable: true, // 是否可移动
        zoomable: true, // 是否可缩放
        rotatable: true, // 是否可旋转
        scalable: true, // 是否可翻转
        transition: true, // 是否使用过渡动画
        fullscreen: true, // 是否支持全屏
        keyboard: true // 是否支持键盘操作
    }
})

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.mount('#app')
