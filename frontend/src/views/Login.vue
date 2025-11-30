<template>
  <div class="login-container">
    <!-- 标题动画效果 -->
    <div class="title-animation">
      <h1 class="main-title">PIPEPro</h1>
      <h2 class="sub-title">管道数字孪生平台</h2>
    </div>
    
    <el-card class="login-form">
      <template #header>
        <div class="login-title">用户登录</div>
      </template>
      
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">
            登录
          </el-button>
        </el-form-item>
        
        <div class="register-link">
          还没有账号？<router-link to="/register">立即注册</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { login } from '../api/user'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const loginFormRef = ref(null)
    const loading = ref(false)
    
    const loginForm = reactive({
      username: '',
      password: ''
    })
    
    const rules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' }
      ]
    }
    
    const handleLogin = async () => {
      try {
        // 表单验证
        await loginFormRef.value.validate()
        loading.value = true
        
        console.log('登录请求数据:', loginForm)
        const response = await login(loginForm)
        console.log('登录响应数据:', response)
        
        // 保存token和用户信息到localStorage
        localStorage.setItem('token', response.token)
        localStorage.setItem('userInfo', JSON.stringify({
          username: response.username,
          nickname: response.nickname
        }))
        
        ElMessage.success('登录成功')
        router.push('/pipe-digital-twin-full')
      } catch (error) {
        if (error !== false) { // 排除表单验证失败的情况
          console.error('登录失败:', error)
          console.error('登录失败响应:', error.response)
          ElMessage.error(error.response?.data || '登录失败，请检查用户名和密码')
        }
      } finally {
        loading.value = false
      }
    }
    
    return {
      loginFormRef,
      loginForm,
      rules,
      loading,
      handleLogin
    }
  }
}
</script>

<style scoped>
/* 工业风格登录容器 */
.login-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #1a1a1a;
  background-image: 
    radial-gradient(circle at 10% 20%, rgba(52, 152, 219, 0.1) 0%, transparent 20%),
    radial-gradient(circle at 80% 80%, rgba(52, 152, 219, 0.1) 0%, transparent 20%);
  position: relative;
  overflow: hidden;
}

/* 标题动画效果 */
.title-animation {
  text-align: center;
  margin-bottom: 40px;
  animation: fadeInUp 1.2s ease-out;
}

.main-title {
  font-size: 3.5rem;
  font-weight: bold;
  color: #3498db;
  margin: 0;
  letter-spacing: 2px;
  text-transform: uppercase;
  animation: pulse 2s infinite;
  text-shadow: 0 0 20px rgba(52, 152, 219, 0.5);
}

.sub-title {
  font-size: 1.3rem;
  color: #95a5a6;
  margin: 10px 0 0 0;
  letter-spacing: 1px;
  animation: fadeIn 1.5s ease-out 0.5s both;
}

/* 工业风格登录表单卡片 */
.login-form {
  width: 420px;
  background-color: #2c2c2c;
  border: 1px solid #34495e;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.4);
  border-radius: 3px;
  overflow: hidden;
  animation: slideInUp 0.8s ease-out 0.3s both;
}

/* 表单头部 */
.login-title {
  text-align: center;
  font-size: 1.3rem;
  font-weight: bold;
  color: #3498db;
  padding: 15px 0;
  border-bottom: 1px solid #34495e;
  background-color: #34495e;
  letter-spacing: 1px;
}

/* Element Plus 表单样式覆盖 */
:deep(.el-form-item__label) {
  color: #bdc3c7;
  font-weight: 500;
}

:deep(.el-input__wrapper) {
  background-color: #34495e;
  border: 1px solid #7f8c8d;
  border-radius: 3px;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  border-color: #3498db;
  box-shadow: 0 0 8px rgba(52, 152, 219, 0.3);
}

:deep(.el-input__input) {
  color: #e0e0e0;
}

:deep(.el-button--primary) {
  background-color: #3498db;
  border-color: #3498db;
  border-radius: 3px;
  height: 40px;
  font-size: 1rem;
  font-weight: 500;
  transition: all 0.3s ease;
}

:deep(.el-button--primary:hover) {
  background-color: #2980b9;
  border-color: #2980b9;
  box-shadow: 0 0 12px rgba(52, 152, 219, 0.4);
  transform: translateY(-1px);
}

:deep(.el-button--primary:active) {
  transform: translateY(0);
}

/* 注册链接 */
.register-link {
  text-align: center;
  margin-top: 20px;
  color: #95a5a6;
  font-size: 0.95rem;
}

.register-link a {
  color: #3498db;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
}

.register-link a:hover {
  color: #2980b9;
  text-decoration: underline;
}

/* 动画定义 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    text-shadow: 0 0 20px rgba(52, 152, 219, 0.5);
  }
  50% {
    transform: scale(1.03);
    text-shadow: 0 0 30px rgba(52, 152, 219, 0.8);
  }
}
</style>