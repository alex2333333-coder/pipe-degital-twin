<template>
  <div class="app-container">
    <!-- 导航栏 -->
    <header v-if="isAuthenticated && !isAuthPage" class="navbar">
      <div class="navbar-brand">
        <router-link to="/pipe-digital-twin-full">PIPEPro-管道数字孪生平台</router-link>
      </div>
      <div class="navbar-menu">
        <router-link to="/pipe-digital-twin-full" class="nav-link">管道数字孪生</router-link>
        <div class="user-dropdown">
          <el-dropdown trigger="click">
            <span class="user-info">用户: {{ userInfo?.username }}</span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item divided>
                  <el-button type="text" @click="showUserInfo">查看用户信息</el-button>
                </el-dropdown-item>
                <el-dropdown-item divided>
                  <el-button type="text" @click="logout">退出登录</el-button>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>
    
    <!-- 主要内容区域 -->
    <main :class="{'main-content': !isAuthPage}">
      <router-view />
    </main>
    
    <!-- 页脚 -->
    <footer v-if="isAuthenticated && !isAuthPage" class="footer">
      <p>© 2023 Vue3 + SpringBoot 前后端分离项目</p>
    </footer>
  
    <!-- 用户信息弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="用户信息"
      width="500px"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户名">{{ userInfo?.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ userInfo?.nickname }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElDialog, ElDescriptions, ElDescriptionsItem } from 'element-plus'

export default {
  name: 'App',
  setup() {
    const router = useRouter()
    const isAuthenticated = ref(false)
    const userInfo = ref(null)

    // 检查用户登录状态
    const checkAuth = () => {
      const token = localStorage.getItem('token')
      const userData = localStorage.getItem('userInfo')
      if (token && userData) {
        isAuthenticated.value = true
        userInfo.value = JSON.parse(userData)
      } else {
        isAuthenticated.value = false
        userInfo.value = null
      }
    }

    // 查看用户信息
    const showUserInfo = () => {
      dialogVisible.value = true
    }

    // 退出登录
    const logout = () => {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      isAuthenticated.value = false
      userInfo.value = null
      router.push('/login')
      ElMessage.success('退出登录成功')
    }

    // 用户信息弹窗
    const dialogVisible = ref(false)

    // 计算当前是否是登录或注册页面
    const isAuthPage = computed(() => {
      const currentRoute = router.currentRoute.value
      return currentRoute.path === '/login' || currentRoute.path === '/register'
    })

    onMounted(() => {
      checkAuth()
    })

    // 监听路由变化，更新认证状态
    router.afterEach(() => {
      checkAuth()
    })

    return {
      isAuthenticated,
      userInfo,
      logout,
      showUserInfo,
      dialogVisible,
      isAuthPage
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #e0e0e0;
  background-color: #1a1a1a;
}

.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #1a1a1a;
}

/* 工业风格导航栏 */
.navbar {
  background-color: #2c2c2c;
  color: #e0e0e0;
  padding: 0 15px;
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  border-bottom: 2px solid #3498db;
}

.navbar-brand a {
  color: #3498db;
  text-decoration: none;
  font-size: 1.3rem;
  font-weight: bold;
  letter-spacing: 1px;
  text-transform: uppercase;
}

.navbar-menu {
  display: flex;
  align-items: center;
  gap: 10px;
}

.nav-link {
  color: #e0e0e0;
  text-decoration: none;
  padding: 6px 12px;
  border-radius: 3px;
  transition: all 0.3s ease;
  font-weight: 500;
  border: 1px solid transparent;
  font-size: 12px;
}

.nav-link:hover {
  background-color: #34495e;
  border-color: #3498db;
  color: #3498db;
}

.user-info {
  font-size: 11px;
  color: #bdc3c7;
  padding: 4px 8px;
  background-color: #34495e;
  border-radius: 3px;
}

/* 主要内容区域 */
.main-content {
  flex: 1;
  width: 100%;
  min-height: calc(100vh - 60px);
  background-color: #2c2c2c;
  box-sizing: border-box;
  overflow: hidden;
  padding: 0;
}

/* 登录和注册页面特殊处理 */
.login-container,
.register-container {
  margin: 0 !important;
  padding: 0 !important;
  width: 100% !important;
  min-height: 100vh;
  background-color: #1a1a1a;
  box-sizing: border-box;
}

/* 工业风格页脚 */
.footer {
  text-align: center;
  padding: 10px;
  background-color: #2c2c2c;
  color: #95a5a6;
  border-top: 1px solid #34495e;
  font-size: 0.8rem;
}
</style>