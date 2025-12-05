<template>
    <div class="container">
        <h1 class="page-title">我的</h1>

        <el-card class="user-card" shadow="hover" style="margin-bottom: 0.5rem;">
            <div class="user-info">
                <div class="custom-cell">
                    <span class="custom-cell__label">用户名</span>
                    <span class="custom-cell__value">{{ username }}</span>
                </div>
                <div class="custom-cell">
                    <span class="custom-cell__label">手机号</span>
                    <span class="custom-cell__value">{{ phone }}</span>
                </div>
                <div class="custom-cell" @click="logout" style="cursor: pointer;">
                    <span class="custom-cell__label">退出登录</span>
                    <el-icon class="custom-cell__arrow">
                        <ArrowRight />
                    </el-icon>
                </div>
            </div>
        </el-card>

        <el-card class="menu-card" shadow="hover">
            <el-menu default-active="0" class="mine-menu">
                <!-- 修复：使用新的插槽语法 #icon 和 #title 替代 slot 属性 -->
                <el-menu-item index="0">
                    <template #icon>
                        <el-icon>
                            <Setting />
                        </el-icon>
                    </template>
                    <template #title>个人设置</template>
                </el-menu-item>
                <el-menu-item index="1">
                    <template #icon>
                        <el-icon>
                            <Bell />
                        </el-icon>
                    </template>
                    <template #title>消息通知</template>
                </el-menu-item>
                <el-menu-item index="2">
                    <template #icon>
                        <el-icon>
                            <HelpFilled />
                        </el-icon>
                    </template>
                    <template #title>帮助中心</template>
                </el-menu-item>
                <el-menu-item index="3">
                    <template #icon>
                        <el-icon>
                            <InfoFilled />
                        </el-icon>
                    </template>
                    <template #title>关于我们</template>
                </el-menu-item>
            </el-menu>
        </el-card>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import {
    ArrowRight,
    Setting,
    Bell,
    HelpFilled,
    InfoFilled
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const username = ref(localStorage.getItem('username') || '用户')
const phone = ref(localStorage.getItem('phone') || '未绑定')

// 退出登录
const logout = () => {
    localStorage.removeItem('isLogin')
    localStorage.removeItem('username')
    localStorage.removeItem('phone')
    ElMessage.success('退出登录成功')
    router.push('/login')
}
</script>

<style scoped>
.user-card, .menu-card {
    padding: 0.3rem;
}

.user-info {
    padding: 0.1rem;
}

.mine-menu {
    width: 100%;
    background-color: transparent;
}

.el-menu-item {
    display: flex;
    align-items: center;
    padding: 0 0.2rem !important;
}

.el-menu-item .el-icon {
    margin-right: 0.2rem !important;
    color: #409eff !important;
}
</style>
