<template>
  <div class="login-container">
    <div class="login-card">
      <h2 class="login-title">云网盘登录</h2>
      <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
      >
        <el-form-item prop="userName">
          <el-input
              v-model="loginForm.userName"
              placeholder="请输入用户名"
              prefix-icon="User"
          ></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
          ></el-input>
        </el-form-item>
        <el-form-item class="remember-item">
          <el-checkbox v-model="loginForm.readme">记住我</el-checkbox>&nbsp;&nbsp;&nbsp;&nbsp;
          <el-link type="primary" class="forgot-link">忘记密码?</el-link>
        </el-form-item>
        <el-form-item>
          <el-button
              type="primary"
              class="login-btn"
              @click="handleLogin"
              :loading="loading"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Cookies from 'js-cookie'
import { authAPI } from '@/api/login'

// 表单状态
const loginFormRef = ref(null)
const loading = ref(false)
const loginForm = reactive({
  userName: '',
  password: '',
  readme: false
})

// 表单验证规则
const loginRules = {
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

// 路由实例
const router = useRouter()

// 页面加载时读取Cookie（如果有记住我）
const initForm = () => {
  const savedUser = Cookies.get('diskUser')
  const savedPwd = Cookies.get('diskPwd')
  if (savedUser && savedPwd) {
    loginForm.userName = savedUser
    loginForm.password = savedPwd
    loginForm.readme = true
  }
}

// 登录处理
const handleLogin = async () => {
  // 表单验证
  try {
    // 登录成功处理
    if (loginForm.readme) {
      Cookies.set('diskUser', loginForm.userName, { expires: 7 })
      Cookies.set('diskPwd', loginForm.password, { expires: 7 })
    } else {
      Cookies.remove('diskUser')
      Cookies.remove('diskPwd')
    }
    loading.value = true
    const valid = await loginFormRef.value.validate()
    if (!valid) return
    // 模拟登录请求（实际项目替换为真实接口）
    const res = await authAPI.login({
      userName: loginForm.userName,
      password: loginForm.password,
      readme: loginForm.readme,
      ac: "ac"
    });

    if (res['tokenName']) {
      // 存储登录状态和token（实际项目以token为准）
      localStorage.setItem('isLogin', 'true')
      localStorage.setItem('tokenName', res['tokenName']);
      localStorage.setItem('tokenValue', res['tokenValue']);

      router.push('/disk')
      ElMessage.success('登录成功')
    } else {
      ElMessage.error('登录失败')
    }
    loading.value = false;
  } catch (error) {
    ElMessage.error(error || '登录失败，请重试')
    loading.value = false;
  }
}

// 初始化表单
initForm()
</script>

<style scoped>
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

.remember-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.forgot-link {
  font-size: 14px;
}

.login-btn {
  width: 100%;
  padding: 12px 0;
  font-size: 16px;
}
</style>