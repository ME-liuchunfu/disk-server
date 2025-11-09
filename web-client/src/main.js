// src/main.js
import { createApp } from 'vue'
import App from '@/App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'video.js/dist/video-js.css'
import 'highlight.js/styles/atom-one-dark.css'
import router from '@/router'  // 引入路由
import * as ElementPlusIconsVue from '@element-plus/icons-vue' // 导入所有图标

const app = createApp(App);

app.use(ElementPlus);
app.use(router);  // 使用路由

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.mount('#app')