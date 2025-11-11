<!-- App.vue 完整代码 -->
<template>
  <div id="app">
    <div class="app-container">
      <!-- 侧边栏 -->
      <aside v-if="isLogin"  class="sidebar">
        <div class="sidebar-logo">
          <el-icon><Cloud /></el-icon>
          <span>云网盘</span>
        </div>
        <el-menu default-active="/disk" class="sidebar-menu" @select="handleMenuSelect">
          <!-- 硬编码图标，不使用任何动态逻辑 -->
          <el-menu-item index="/disk">
            <el-icon><HardDrive /></el-icon>
            <span>我的文件</span>
          </el-menu-item>
          <el-menu-item index="/shared">
            <el-icon><Share /></el-icon>
            <span>共享文件</span>
          </el-menu-item>
          <el-menu-item index="/favorites">
            <el-icon><Star /></el-icon>
            <span>收藏夹</span>
          </el-menu-item>
        </el-menu>
      </aside>
      <!-- 主内容区 -->
      <main class="main-content">
        <section v-if="isLogin">
          <!-- 登出按钮 -->
          <div class="logout-btn">
            <el-button
                type="danger"
                size="small"
                @click="handleLogout"
            >
              <el-icon><Logout /></el-icon> 退出登录
            </el-button>
          </div>
        </section>
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
// 直接导入需要的图标（确保名称正确）
import { ref, watch } from 'vue'
import { Cloud, HardDrive, Share, Star, Logout } from '@element-plus/icons-vue'
import {ElMessage} from "element-plus";
// import Cookies from "js-cookie";

// 登录状态：从localStorage读取
const isLogin = ref(localStorage.getItem('isLogin') === 'true')
const router = useRouter()
const route = useRoute()

// 监听路由变化，更新登录状态
watch(
    () => route.path,
    () => {
      // console.log(newPath, oldPath)
      isLogin.value = localStorage.getItem('isLogin') === 'true'
    }
)

const handleMenuSelect = (path) => {
  router.push(path)
}

const handleLogout = async () => {
  localStorage.clear();
  router.push('/login')
  ElMessage.success('已退出登录')
}
</script>

<style scoped>
/* 保持原样式不变 */
#app {
  font-family: 'Segoe UI', sans-serif;
  height: 100vh;
  overflow: hidden;
}
.app-container {
  display: flex;
  height: 100%;
}
.sidebar {
  width: 220px;
  background-color: #2c3e50;
  color: #fff;
  height: 100%;
  padding-top: 20px;
}
.sidebar-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 15px 20px;
  border-bottom: 1px solid #34495e;
  margin-bottom: 10px;
}
.sidebar-menu {
  background-color: transparent !important;
  border-right: none !important;
}
.sidebar-menu .el-menu-item {
  color: #ecf0f1;
  height: 50px;
  line-height: 50px;
  padding-left: 30px !important;
}
.main-content {
  flex: 1;
  overflow-y: auto;
  background-color: #f5f7fa;
  padding: 20px;
}
</style>