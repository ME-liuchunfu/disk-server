<template>
    <div class="login-container">
        <div class="login-card">
            <h2 class="login-title">云网盘注册</h2>
            <el-form
                    ref="registerFormRef"
                    :model="registerForm"
                    :rules="registerRules"
                    class="login-form"
                    label-width="0px"
            >
                <!-- 用户名 -->
                <el-form-item prop="username">
                    <el-input
                            v-model="registerForm.username"
                            placeholder="请输入用户名（3-16位字符）"
                            prefix-icon="User"
                            clearable
                    ></el-input>
                </el-form-item>
                <el-form-item prop="nickname">
                    <el-input
                            v-model="registerForm.nickname"
                            placeholder="请输入昵称（3-16位字符）"
                            prefix-icon="User"
                            clearable
                    ></el-input>
                </el-form-item>

                <!-- 邮箱 -->
                <el-form-item prop="email">
                    <el-input
                            v-model="registerForm.email"
                            placeholder="请输入邮箱地址"
                            prefix-icon="Message"
                            clearable
                            type="email"
                    ></el-input>
                </el-form-item>

                <!-- 密码 -->
                <el-form-item prop="password">
                    <el-input
                            v-model="registerForm.password"
                            placeholder="请输入密码（至少6位，包含字母和数字）"
                            prefix-icon="Lock"
                            type="password"
                            @input="checkPasswordStrength"
                    ></el-input>
                    <!-- 密码强度提示 -->
                    <div class="password-strength" v-if="registerForm.password">
                        <div
                                class="strength-bar"
                                :class="passwordStrengthClass"
                        ></div>
                        <span class="strength-text">{{ passwordStrengthText }}</span>
                    </div>
                </el-form-item>

                <!-- 确认密码 -->
                <el-form-item prop="confirmPassword">
                    <el-input
                            v-model="registerForm.confirmPassword"
                            placeholder="请再次输入密码"
                            prefix-icon="Lock"
                            type="password"
                    ></el-input>
                </el-form-item>

                <!-- 同意协议 -->
                <el-form-item prop="agreement" class="agreement-item">
                    <el-checkbox v-model="registerForm.agreement">
                        我已阅读并同意
                        <el-link type="primary">用户协议</el-link> 和
                        <el-link type="primary">隐私政策</el-link>
                    </el-checkbox>
                </el-form-item>

                <!-- 注册按钮 -->
                <el-form-item>
                    <el-button
                            type="primary"
                            class="login-btn"
                            @click="handleRegister"
                            :loading="loading"
                    >
                        注册
                    </el-button>
                </el-form-item>

                <!-- 切换到登录 -->
                <div class="switch-login">
                    已有账号？
                    <el-link type="primary" @click="$router.push('/login')">立即登录</el-link>
                </div>
            </el-form>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { registerAPI } from '@/api/register'
import Cookies from 'js-cookie'

// 表单状态
const registerFormRef = ref(null)
const loading = ref(false)
const registerForm = reactive({
    username: '',
    nickname: '',
    email: '',
    password: '',
    confirmPassword: '',
    agreement: false // 是否同意协议
})

// 密码强度相关
const passwordStrength = ref(0) // 0-未输入 1-弱 2-中 3-强

// 密码强度样式
const passwordStrengthClass = computed(() => {
    const classes = ['strength-bar']
    if (passwordStrength.value === 1) classes.push('weak')
    if (passwordStrength.value === 2) classes.push('medium')
    if (passwordStrength.value === 3) classes.push('strong')
    return classes
})

// 密码强度文本
const passwordStrengthText = computed(() => {
    const texts = ['', '弱', '中', '强']
    return `密码强度：${texts[passwordStrength.value]}`
})

// 检查密码强度
const checkPasswordStrength = (val) => {
    if (!val) {
        passwordStrength.value = 0
        return
    }
    let score = 0
    // 长度 >=6
    if (val.length >= 6) score += 1
    // 包含数字
    if (/[0-9]/.test(val)) score += 1
    // 包含字母
    if (/[a-zA-Z]/.test(val)) score += 1
    passwordStrength.value = score
}

// 表单验证规则
const registerRules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 16, message: '用户名长度为3-16位', trigger: 'blur' },
        { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
    ],
    nickname: [
        { required: true, message: '请输入昵称', trigger: 'blur' },
        { min: 3, max: 16, message: '昵称长度为3-16位', trigger: 'blur' }
    ],
    email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
        { pattern: /^(?=.*[a-zA-Z])(?=.*\d).+$/, message: '密码必须包含字母和数字', trigger: 'blur' }
    ],
    confirmPassword: [
        { required: true, message: '请确认密码', trigger: 'blur' },
        {
            validator: (rule, value, callback) => {
                if (value !== registerForm.password) {
                    callback(new Error('两次输入的密码不一致'))
                } else {
                    callback()
                }
            },
            trigger: 'blur'
        }
    ],
    agreement: [
        {
            validator: (rule, value, callback) => {
                if (!value) {
                    callback(new Error('请同意用户协议和隐私政策'))
                } else {
                    callback()
                }
            },
            trigger: 'change'
        }
    ]
}

// 路由实例
const router = useRouter()

// 处理注册
const handleRegister = async () => {
    try {
        // 表单验证
        const valid = await registerFormRef.value.validate()
        if (!valid) return;
        loading.value = true;
        // 调用注册API（实际项目替换为真实接口）
        const res = await registerAPI.register({
            userName: registerForm.username,
            nickName: registerForm.nickname,
            email: registerForm.email,
            password: registerForm.password
        })
        console.log(res)
        loading.value = false;
        ElMessage.success('注册成功，请登录')
        Cookies.remove('diskUser')
        Cookies.remove('diskPwd')
        // 注册成功处理
        // 跳转到登录页，并携带用户名
        router.push({path: '/login'})
    } catch (error) {
        // 错误信息由axios拦截器统一处理
        ElMessage.success(error || '注册失败')
    } finally {
        loading.value = false
    }
}
</script>

<style scoped>
/* 复用登录页基础样式 */
.login-container {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f0f2f5;
}

.login-card {
    width: 400px;
    padding: 30px;
    background-color: #fff;
    border-radius: 10px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-title {
    text-align: center;
    margin-bottom: 30px;
    color: #303133;
}

.login-form {
    margin-top: 20px;
}

.login-btn {
    width: 100%;
    padding: 12px 0;
    font-size: 16px;
}

/* 注册页特有样式 */
.password-strength {
    margin-top: 8px;
    font-size: 12px;
}

.strength-bar {
    height: 4px;
    width: 100px;
    background-color: #eee;
    margin-bottom: 4px;
    transition: background-color 0.3s;
}

.strength-bar.weak {
    background-color: #ff4d4f;
    width: 33%;
}

.strength-bar.medium {
    background-color: #faad14;
    width: 66%;
}

.strength-bar.strong {
    background-color: #52c41a;
    width: 100%;
}

.strength-text {
    color: #666;
}

.agreement-item {
    margin-bottom: 10px;
    font-size: 14px;
}

.switch-login {
    text-align: center;
    margin-top: 15px;
    font-size: 14px;
    color: #666;
}
</style>
