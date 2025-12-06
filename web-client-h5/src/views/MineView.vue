<template>
    <div class="container">
        <h1 class="page-title">我的</h1>

        <el-card class="user-card" shadow="hover" style="margin-bottom: 0.25rem;">
            <div class="user-info">
                <div class="custom-cell">
                    <span class="custom-cell__label">用户名</span>
                    <span class="custom-cell__value">{{ userInfo?.name }}</span>
                </div>
                <div class="custom-cell">
                    <span class="custom-cell__label">邮箱</span>
                    <span class="custom-cell__value">{{ userInfo?.email }}</span>
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
import { ref, onMounted } from 'vue'
import {
    ArrowRight,
    Setting,
    Bell,
    HelpFilled,
    InfoFilled
} from '@element-plus/icons-vue'
import cacheInfo from "@/stores/cacheInfo";
import {userAPI} from "@/api/userinfo";
import router from "@/router";
import {EVENT_KEYS} from "@/utils/event/toolbar-event";
import {eventBus} from "@/utils/eventBus";

const userInfo = ref(null)

// 退出登录
const logout = () => {
    cacheInfo.logout()
    router.push('/login')
}

const toolbarCall = async (event) => {
  if (event['type'] === 'mine') {
    const data = await userAPI.info()
    cacheInfo.setUserInfo(data);
    userInfo.value = data;
  }
}
eventBus.on(EVENT_KEYS.TOOLBAR_CHANGE, toolbarCall);

onMounted(()=>{
  userInfo.value = cacheInfo.getUserInfo()
})


</script>

<style scoped>
.user-card, .menu-card {
    padding: 0.15rem; /* 15px */
}

.user-info {
    padding: 0.05rem; /* 5px */
}

.mine-menu {
    width: 100%;
    background-color: transparent;
}

.el-menu-item {
    display: flex;
    align-items: center;
    padding: 0 0.1rem !important; /* 10px */
}

.el-menu-item .el-icon {
    margin-right: 0.1rem !important; /* 10px */
    color: #409eff !important;
}
</style>
