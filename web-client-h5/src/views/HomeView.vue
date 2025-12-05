<template>
    <div class="container">
        <h1 class="page-title">首页</h1>
        <el-card class="home-card" shadow="hover">
            <div class="card-header">
                <h2 class="card-title">欢迎使用</h2>
                <p class="card-desc">这是H5移动端首页</p>
            </div>
        </el-card>

        <div class="home-features" style="margin-top: 0.5rem;">
            <el-row :gutter="20">
                <el-col :span="12" v-for="(item, index) in features" :key="index">
                    <div class="feature-item">
                        <el-icon class="feature-icon">
                            <component :is="item.icon" />
                        </el-icon>
                        <span class="feature-text">{{ item.name }}</span>
                    </div>
                </el-col>
            </el-row>
        </div>
    </div>
</template>

<script setup>
import {
    ShoppingCart,
    Ticket,
    Gift,
    Star
} from '@element-plus/icons-vue'
import {ref} from "vue";

const features = ref([
    { name: '功能1', icon: ShoppingCart },
    { name: '功能2', icon: Ticket },
    { name: '功能3', icon: Gift },
    { name: '功能4', icon: Star }
])

</script>

<style scoped>
.home-card {
    padding: 0.4rem;
    margin-bottom: 0.5rem;
}

.card-header {
    text-align: center;
}

.card-title {
    font-size: 0.36rem;
    font-weight: 600;
    margin-bottom: 0.2rem;
    color: #333;
}

.card-desc {
    font-size: 0.28rem;
    color: #666;
}

.feature-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background-color: #fff;
    border-radius: 0.2rem;
    height: 2rem;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.feature-icon {
    font-size: 0.6rem !important;
    margin-bottom: 0.15rem;
    color: #409eff;
}

.feature-text {
    font-size: 0.3rem;
    color: #333;
}
</style>
