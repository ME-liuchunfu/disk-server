<!-- App.vue 完整代码 -->
<template>
  <div id="app">
    <div class="app-container">
      <!-- 侧边栏 -->
      <aside-menu />
      <!-- 主内容区 -->
      <main class="main-content">
          <top-nav />
          <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import TopNav from "@/views/layout/TopNav.vue";
import AsideMenu from "@/views/layout/AsideMenu.vue";

</script>

<style scoped>
/* 保持原样式不变 */
#app {
  font-family: 'Segoe UI', sans-serif;
  height: 100vh;
  overflow-y: hidden;
}
.app-container {
  display: flex;
  height: 100%;
}
.main-content {
    flex: 1;
}
</style>
