<template>
    <div id="app" class="app-container">
        <div class="main-content">
            <router-view />
        </div>
        <!-- 底部导航栏：根据路由元信息控制显示/隐藏 -->
        <TabBar v-if="$route.meta['showTabBar'] === true" />
    </div>
</template>

<script setup>
import TabBar from '@/components/TabBar.vue'
</script>

<style>
#app {
    font-family: Avenir, Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
    background-color: #eee; /* 大屏两侧背景色 */
}
.app-container {
    display: flex;
    overflow: hidden;
}
.main-content {
    flex: 1;
    box-sizing: border-box;
    padding-bottom: 0.6rem;
    overflow: hidden;
}
</style>
