<template>
    <div class="container">
        <h1 class="page-title">我的</h1>

        <el-card class="user-card" shadow="hover" style="margin-bottom: 0.25rem;">
            <div class="user-info">
                <div class="custom-cell">
                    <span class="custom-cell__label">用户名</span>
                    <span class="custom-cell__value" v-html="userInfo.name"></span>
                </div>
                <div class="custom-cell">
                    <span class="custom-cell__label">邮箱</span>
                    <span class="custom-cell__value" v-html="userInfo.email"></span>
                </div>
                <div class="custom-cell" @click="logout" style="cursor: pointer;">
                    <span class="custom-cell__label">退出登录</span>
                    <el-icon>
                      <component :is="'ElIconArrowRight'" />
                    </el-icon>
                </div>
            </div>
        </el-card>

        <el-card class="menu-card" shadow="hover">
            <el-menu default-active="0" class="mine-menu">
                <el-menu-item v-for="(item, index) in menuList" :index="index+''" :key="index">
                    <template #icon v-if="item.icon">
                        <el-icon>
                          <component :is="'ElIcon'+item.icon" />
                        </el-icon>
                    </template>
                    <template #title>{{item.title}}</template>
                </el-menu-item>
            </el-menu>
        </el-card>
    </div>
</template>

<script setup>
import {ref, onMounted, onActivated } from 'vue'
import cacheInfo from "@/stores/cacheInfo";
import {userAPI} from "@/api/userinfo";
import router from "@/router";

const getUserIfo = async ()=>{
  const data = cacheInfo.getUserInfo();
  if (data) {
    userInfo.value = {...userInfo.value, ...data}
  } else {
    const data = await userAPI.info()
    cacheInfo.setUserInfo(data);
    userInfo.value = {...userInfo.value, ...data};
  }
}

const userInfo = ref({
  name: null,
  email: null
})
const menuList = ref([
  {icon: 'Setting', title: '个人设置'},
  {icon: 'Bell', title: '消息通知'},
  {icon: 'HelpFilled', title: '帮助中心'},
  {icon: 'InfoFilled', title: '关于我们'},
])
// const logoutIcon = ref('ElIconArrowRight')

// 退出登录
const logout = () => {
    cacheInfo.logout()
    router.push('/login')
}

onMounted(async ()=>{
  await getUserIfo()
})

onActivated(async ()=>{
  await getUserIfo()
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
