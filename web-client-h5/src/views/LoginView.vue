<template>
    <div class="login-container">
        <div class="login-logo">
            <el-icon class="logo-icon">
                <UserFilled />
            </el-icon>
        </div>
        <h2 class="login-title">欢迎登录</h2>

        <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" label-width="0px" class="login-form">
            <el-form-item prop="username">
                <el-input
                        v-model="loginForm.username"
                        placeholder="请输入用户名"
                        class="login-input"
                />
            </el-form-item>
            <el-form-item prop="phone">
                <el-input
                        v-model="loginForm.phone"
                        placeholder="请输入手机号"
                        type="tel"
                        class="login-input"
                />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="handleLogin" class="login-btn" block>
                    登录
                </el-button>
            </el-form-item>
        </el-form>

        <div class="login-footer">
            <router-link to="/home" class="footer-link">游客登录</router-link>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { UserFilled } from '@element-plus/icons-vue'
import { ElMessage, ElForm, ElFormItem, ElInput, ElButton } from 'element-plus'
import cacheInfo from "@/stores/cacheInfo";

const router = useRouter()
const loginFormRef = ref(null)

const loginForm = reactive({
    username: '',
    phone: ''
})

const loginRules = reactive({
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
    ],
    phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
    ]
})

const handleLogin = async () => {
    try {
        await loginFormRef.value.validate()

        // 模拟登录成功
        localStorage.setItem('isLogin', 'true')
        localStorage.setItem('username', loginForm.username)
        localStorage.setItem('phone', loginForm.phone)
        cacheInfo.setLogin({username: loginForm.username, phone: loginForm.phone})

        ElMessage.success('登录成功')

        // 跳转到"我的"页面
        setTimeout(() => {
            router.push('/mine')
        }, 1000)
    } catch (error) {
        ElMessage.error('登录失败，请检查输入')
        console.error('登录表单验证失败:', error)
    }
}
</script>

<style scoped>
.login-container {
    width: 100%;
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 0.5rem;
    background-color: #f5f5f5;
}

.login-logo {
    margin-bottom: 0.5rem;
}

.logo-icon {
    font-size: 1.5rem !important;
    color: #409eff;
}

.login-title {
    font-size: 0.48rem;
    font-weight: 600;
    margin-bottom: 1rem;
    color: #333;
}

.login-form {
    width: 100%;
    max-width: 5rem;
}

.login-input {
    margin-bottom: 0.3rem;
}

.login-btn {
    background-color: #409eff !important;
    border-color: #409eff !important;
}

.login-footer {
    margin-top: 0.8rem;
    width: 100%;
    max-width: 5rem;
    text-align: right;
}

.footer-link {
    font-size: 0.28rem;
    color: #409eff;
    text-decoration: none;
}
</style>
