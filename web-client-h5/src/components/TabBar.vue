<template>
    <div class="tab-bar">
        <router-link
            :key="index"
            v-for="(data, index) in toolBarRouter"
            :to="data.path"
            class="tab-item"
            :class="{ active: $route.path === data.path }"
        >
            <component
                v-if="data.meta['icon']"
                :is="`ElIcon${data.meta['icon']}`"
                class="tab-icon"
            />
            <span class="tab-text">{{data.meta['title']}}</span>
        </router-link>
    </div>
</template>

<script setup>
import {toolBarRouter} from "@/router/toolbars";
</script>

<style scoped>
/* 固定高度 0.6rem=60px */
.tab-bar {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    width: 3.75rem; /* 与容器一致 */
    height: 0.6rem;
    background-color: #fff;
    display: flex;
    justify-content: space-around;
    align-items: center;
    box-shadow: 0 -0.02rem 0.1rem rgba(0, 0, 0, 0.05);
    z-index: 999;
    margin: 0 auto; /* 居中 */
}

.tab-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 50%;
    height: 100%;
    color: #666;
    text-decoration: none;
}

.tab-item.active {
    color: #409eff;
}

.tab-icon {
    font-size: 0.24rem !important; /* 24px */
    margin-bottom: 0.05rem; /* 5px */
}

.tab-text {
    font-size: 0.14rem; /* 14px */
}
</style>
