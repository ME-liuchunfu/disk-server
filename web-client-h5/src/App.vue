<template>
    <div class="app-container">
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
