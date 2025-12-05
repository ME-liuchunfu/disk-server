<template>
    <div class="login-page">
        <!-- 背景渐变装饰 -->
        <div class="bg-decoration top-left"></div>
        <div class="bg-decoration bottom-right"></div>

        <!-- 登录卡片 -->
        <div class="login-card">
            <!-- Logo 区域 -->
            <div class="login-header">
                <div class="login-logo">
                    <el-icon class="logo-icon">
                        <UserFilled />
                    </el-icon>
                </div>
                <h2 class="login-title">欢迎回来</h2>
                <p class="login-desc">请登录您的账号继续使用</p>
            </div>

            <!-- 登录表单 -->
            <el-form
                :model="loginForm"
                :rules="loginRules"
                ref="loginFormRef"
                label-width="0px"
                class="login-form"
            >
                <!-- 用户名输入框 -->
                <el-form-item prop="username" class="form-item">
                    <el-input
                        v-model="loginForm.username"
                        placeholder="请输入用户名"
                        class="custom-input"
                        :prefix="User"
                        :focus="isUsernameFocused"
                        @focus="isUsernameFocused = true"
                        @blur="isUsernameFocused = false"
                    />
                </el-form-item>
                <el-form-item prop="password" class="form-item">
                    <el-input
                        v-model="loginForm.password"
                        placeholder="请输入密码"
                        type="password"
                        class="custom-input"
                        :prefix="User"
                        :focus="isPhoneFocused"
                        @focus="isPhoneFocused = true"
                        @blur="isPhoneFocused = false"
                    />
                </el-form-item>

                <!-- 登录按钮 -->
                <el-form-item class="form-item">
                    <el-button
                        type="primary"
                        @click="handleLogin"
                        class="login-btn"
                        block
                        :loading="isLoading"
                    >
                        <span v-if="!isLoading">登录</span>
                        <span v-else>登录中...</span>
                    </el-button>
                </el-form-item>

                <!-- 辅助链接 -->
                <div class="login-links">
                    <router-link to="/forgot-password" class="link-item">忘记密码？</router-link>
                    <router-link to="/register" class="link-item">注册账号</router-link>
                </div>
            </el-form>

            <!-- 游客登录 -->
            <div class="guest-login">
                <router-link to="/home" class="guest-link">
                    <el-icon class="guest-icon"><ArrowRight /></el-icon>
                    游客登录
                </router-link>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { UserFilled, User, ArrowRight } from '@element-plus/icons-vue'
import { ElMessage, ElForm, ElFormItem, ElInput, ElButton } from 'element-plus'
import cacheInfo from "@/stores/cacheInfo";
import {routerEvent} from "@/utils/event/router-event";
import {authAPI} from "@/api/login";

const loginFormRef = ref(null)
const isLoading = ref(false) // 登录加载状态
const isUsernameFocused = ref(false) // 用户名输入框聚焦状态
const isPhoneFocused = ref(false) // 手机号输入框聚焦状态

// 登录表单数据
const loginForm = reactive({
    username: '',
    password: ''
})

// 表单验证规则
const loginRules = reactive({
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '用户名长度为6-20个字符', trigger: 'blur' }
    ]
})

// 登录处理
const handleLogin = async () => {
    try {
        // 表单验证
        await loginFormRef.value.validate()

        // 模拟登录加载
        isLoading.value = true
        const res = await authAPI.login({
            userName: loginForm.username,
            password: loginForm.password,
            readme: false,
            ac: "ac"
        });
        isLoading.value = false
        if (res['tokenName']) {
            ElMessage.success('登录成功，欢迎回来～')
            cacheInfo.setLogin({'tokenValue': res['tokenValue'], 'tokenName': res['tokenName']})
            routerEvent.home()
        } else {
            ElMessage.success(res['msg'] || '登录失败')
        }
    } catch (error) {
        ElMessage.error('登录失败，请检查输入')
        console.error('登录表单验证失败:', error)
    }
}
</script>

<style scoped>
/* 页面容器 */
.login-page {
    width: 100%;
    min-height: 100vh;
    position: relative;
    background-color: #f8f9fa;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0.5rem;
    overflow: hidden;
}

/* 背景装饰 */
.bg-decoration {
    position: absolute;
    width: 8rem;
    height: 8rem;
    border-radius: 50%;
    opacity: 0.15;
    z-index: 0;
}

.top-left {
    top: -2rem;
    left: -2rem;
    background: linear-gradient(135deg, #409eff, #69b1ff);
}

.bottom-right {
    bottom: -2rem;
    right: -2rem;
    background: linear-gradient(135deg, #722ed1, #9370db);
}

/* 登录卡片 */
.login-card {
    width: 100%;
    max-width: 5rem;
    background-color: #fff;
    border-radius: 0.4rem;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
    padding: 0.8rem 0.6rem;
    position: relative;
    z-index: 1;
    transform: translateY(0);
    transition: all 0.3s ease;
}

.login-card:hover {
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.12);
    transform: translateY(-0.1rem);
}

/* 登录头部 */
.login-header {
    text-align: center;
    margin-bottom: 0.8rem;
}

.login-logo {
    width: 0.6rem;
    height: 0.6rem;
    background: linear-gradient(135deg, #409eff, #722ed1);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 0.4rem;
    box-shadow: 0 4px 15px rgba(64, 158, 255, 0.3);
}

.logo-icon {
    font-size: 0.8rem !important;
    color: #fff;
}

.login-title {
    font-size: 0.48rem;
    font-weight: 600;
    color: #1d2129;
    margin-bottom: 0.15rem;
}

.login-desc {
    font-size: 0.28rem;
    color: #86909c;
}

/* 表单样式 */
.login-form {
    width: 100%;
}

.form-item {
    margin-bottom: 0.4rem;
}

.custom-input {
    border-radius: 0.45rem !important;
    height: 0.95rem !important;
    font-size: 0.32rem !important;
    border: 1px solid #e5e6eb !important;
    transition: all 0.3s ease;
}

.custom-input:focus-within {
    border-color: #409eff !important;
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.15) !important;
}

.el-input__prefix {
    color: #86909c !important;
    font-size: 0.4rem !important;
    margin-right: 0.2rem !important;
}

.custom-input:focus-within .el-input__prefix {
    color: #409eff !important;
}

/* 登录按钮 */
.login-btn {
    background: linear-gradient(135deg, #409eff, #69b1ff) !important;
    border: none !important;
    height: 1rem !important;
    font-size: 0.34rem !important;
    font-weight: 500 !important;
    border-radius: 0.5rem !important;
    transition: all 0.3s ease;
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.login-btn:hover {
    background: linear-gradient(135deg, #3689e6, #5ba0ff) !important;
    box-shadow: 0 6px 16px rgba(64, 158, 255, 0.25);
}

.login-btn:active {
    transform: scale(0.98);
}

/* 辅助链接 */
.login-links {
    display: flex;
    justify-content: space-between;
    margin-top: 0.3rem;
    margin-bottom: 0.8rem;
}

.link-item {
    font-size: 0.28rem;
    color: #409eff;
    text-decoration: none;
    transition: color 0.3s ease;
}

.link-item:hover {
    color: #3689e6;
    text-decoration: underline;
}

/* 游客登录 */
.guest-login {
    text-align: center;
    margin-top: 0.5rem;
}

.guest-link {
    display: inline-flex;
    align-items: center;
    font-size: 0.3rem;
    color: #86909c;
    text-decoration: none;
    transition: all 0.3s ease;
}

.guest-link:hover {
    color: #409eff;
}

.guest-icon {
    font-size: 0.32rem !important;
    margin-left: 0.1rem;
    transition: transform 0.3s ease;
}

.guest-link:hover .guest-icon {
    transform: translateX(0.05rem);
}

/* 适配小屏幕 */
@media (max-width: 320px) {
    .login-card {
        padding: 0.6rem 0.4rem;
    }

    .login-title {
        font-size: 0.42rem;
    }

    .custom-input, .login-btn {
        height: 0.9rem !important;
    }
}
</style>
