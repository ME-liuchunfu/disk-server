<template>
    <div class="container">
        <h1 class="page-title">首页</h1>
        <el-card class="home-card" shadow="hover">
            <div class="card-header">
                <h2 class="card-title">欢迎使用</h2>
                <p class="card-desc">这是H5移动端首页</p>
            </div>
        </el-card>

        <div class="home-features" style="margin-top: 0.25rem;">
            <el-row :gutter="20">
                <el-col :span="12" v-for="(item, index) in features" :key="index">
                    <div class="feature-item">
                        <el-icon class="feature-icon">
                            <component :is="'ElIcon'+item.icon" />
                        </el-icon>
                        <span class="feature-text">{{ item.name }}</span>
                    </div>
                </el-col>
            </el-row>
        </div>
    </div>
</template>

<script setup>
import {ref} from "vue";

const features = ref([
    { name: '功能1', icon: "ShoppingCart" },
    { name: '功能2', icon: "Ticket" },
    { name: '功能3', icon: "Gift" },
    { name: '功能3', icon: "Gift" },
    { name: '功能3', icon: "Gift" },
    { name: '功能3', icon: "Gift" },
    { name: '功能3', icon: "Gift" },
    { name: '功能3', icon: "Gift" },
    { name: '功能3', icon: "Gift" },
    { name: '功能3', icon: "Gift" },
    { name: '功能3', icon: "Gift" },
    { name: '功能3', icon: "Gift" },
    { name: '功能3', icon: "Gift" },
    { name: '功能3', icon: "Gift" },
    { name: '功能3', icon: "Gift" },
    { name: '功能3', icon: "Gift" },
    { name: '功能3', icon: "Gift" },
    { name: '功能3', icon: "Gift" },
    { name: '功能3', icon: "Gift" },
    { name: '功能3', icon: "Gift" },
    { name: '功能4', icon: "Star" }
])

</script>

<style scoped>
.home-card {
    padding: 0.2rem; /* 20px */
    margin-bottom: 0.25rem; /* 25px */
}

.card-header {
    text-align: center;
}

.card-title {
    font-size: 0.18rem; /* 18px */
    font-weight: 600;
    margin-bottom: 0.1rem; /* 10px */
    color: #333;
}

.card-desc {
    font-size: 0.14rem; /* 14px */
    color: #666;
}

.feature-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background-color: #fff;
    border-radius: 0.1rem; /* 10px */
    height: 1rem; /* 100px */
    box-shadow: 0 0.02rem 0.08rem rgba(0, 0, 0, 0.05);
    margin-bottom: 0.1rem; /* 10px */
}

.feature-icon {
    font-size: 0.3rem !important; /* 30px */
    margin-bottom: 0.08rem; /* 8px */
    color: #409eff;
}

.feature-text {
    font-size: 0.15rem; /* 15px */
    color: #333;
}
</style>
