<template>
  <div class="not-found">
    <h1>404</h1>
    <p>页面不存在</p>
    <el-button type="primary" @click="goHome">返回首页</el-button>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
  name: 'NotFound',
  setup() {
    const router = useRouter()
    
    const goHome = () => {
      router.push('/home')
    }
    
    return {
      goHome
    }
  }
}
</script>

<style scoped>
.not-found {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 60vh;
  text-align: center;
  background-color: #1a1a1a;
  color: #e0e0e0;
}

.not-found h1 {
  font-size: 8rem;
  font-weight: bold;
  color: #3498db;
  margin-bottom: 20px;
  text-shadow: 0 0 30px rgba(52, 152, 219, 0.5);
  animation: pulse 2s infinite;
}

.not-found p {
  font-size: 1.3rem;
  color: #95a5a6;
  margin-bottom: 30px;
  letter-spacing: 1px;
}

/* 工业风格按钮 */
:deep(.el-button--primary) {
  background-color: #3498db;
  border-color: #3498db;
  border-radius: 3px;
  padding: 10px 30px;
  font-size: 1rem;
  font-weight: 500;
  transition: all 0.3s ease;
}

:deep(.el-button--primary:hover) {
  background-color: #2980b9;
  border-color: #2980b9;
  box-shadow: 0 0 15px rgba(52, 152, 219, 0.4);
  transform: translateY(-1px);
}

/* 动画效果 */
@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}
</style>