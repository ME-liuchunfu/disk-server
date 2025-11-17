<template>
    <div class="aside-menu" :class="{'mini-menu': miniNav}" v-show="isLogin">
      <aside class="sidebar">
          <div class="sidebar-logo">
              <el-icon><Coin /></el-icon>
              <span>云网盘</span>
          </div>
          <el-menu default-active="/disk" class="sidebar-menu" @select="handleMenuSelect">
              <el-menu-item index="/disk">
                  <el-icon><Folder /></el-icon>
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
    </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import {ref, onMounted, onUnmounted} from 'vue'
import eventBus from '@/utils/eventBus'
import {
    Folder, Coin
} from '@element-plus/icons-vue'

// 登录状态：从localStorage读取
const isLogin = ref(localStorage.getItem('isLogin') === 'true')
const router = useRouter()

const miniNav = ref(localStorage.getItem('miniNav') === 'true')

const handleReceivedData = (data) => {
    if (data && data['type']) {
        const type = data['type']
        const value = data['value']
        if (type === 'miniNav') {
            miniNav.value = value
        } else if (type === 'isLogin') {
            isLogin.value = value
        }
    }
}


// 组件挂载时监听事件
onMounted(() => {
    eventBus.on('data-event', handleReceivedData)
})

// 组件卸载时移除监听，避免内存泄漏
onUnmounted(() => {
    eventBus.off('data-event', handleReceivedData)
})


const handleMenuSelect = (path) => {
    router.push(path)
}

</script>

<style scoped>
.aside-menu {
    display: flex;
    width: 150px;
    background-color: #2c3e50;
    overflow: hidden;
}
.mini-menu {
    width: 40px;
}
.sidebar {
    color: #fff;
    height: 100%;
    padding-top: 20px;
}
.sidebar-logo {
    display: flex;
    align-items: center;
    justify-content: center;
    padding-bottom: 20px;
    border-bottom: 1px solid #34495e;
}
.sidebar-menu {
    background-color: transparent !important;
    border-right: none !important;
}
.sidebar-menu .el-menu-item {
    color: #ecf0f1;
    height: 50px;
    line-height: 50px;
    padding-left: 10px !important;
}
.el-icon {
    margin-right: 10px !important;
}
.el-menu-item:hover {
    background-color: #2c3e5091;
    color: rgb(64 158 255);
}
</style>
