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
.tab-bar {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: 1.2rem;
    background-color: #fff;
    display: flex;
    justify-content: space-around;
    align-items: center;
    box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
    z-index: 999;
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
    font-size: 0.5rem !important;
    margin-bottom: 0.1rem;
}

.tab-text {
    font-size: 0.28rem;
}
</style>
