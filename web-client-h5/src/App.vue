<template>
    <div id="app">
        <router-view />
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
}
</style>
