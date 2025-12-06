<template>
    <div class="tab-bar">
        <span
            :key="index"
            v-for="(data, index) in toolBarRouter"
            @click="goPage(data.path)"
            class="tab-item"
            :class="{ active: $route.path === data.path }"
        >
            <component
                v-if="data.meta['icon']"
                :is="`ElIcon${data.meta['icon']}`"
                class="tab-icon"
            />
            <span class="tab-text">{{data.meta['title']}}</span>
        </span>
    </div>
</template>

<script setup>
import router from "@/router";
import {toolBarRouter} from "@/router/toolbars";
import {tooBarEvent} from "@/utils/event/toolbar-event";

const goPage = (path) => {
  tooBarEvent.change(path)
  router.push(path)
}

</script>

<style scoped>
/* 固定高度 0.6rem=60px */
.tab-bar {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    width: 100%; /* 与容器一致 */
    height: 0.6rem;
    background-color: #fff;
    display: flex;
    justify-content: space-around;
    align-items: center;
    box-shadow: 0 -0.02rem 0.1rem rgba(0, 0, 0, 0.05);
    z-index: 999;
}

.tab-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    flex: 1;
    height: 100%;
    color: #666;
    text-decoration: none;
    padding: 0.1rem 0;
}

.tab-item.active {
    color: #409eff;
}

.tab-icon {
    font-size: 0.26rem !important; /* 24px */
    margin-bottom: 0.05rem; /* 5px */
}

.tab-text {
    font-size: 0.12rem; /* 14px */
}
</style>
