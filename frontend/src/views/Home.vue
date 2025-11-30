<template>
  <div class="home-container">
    <h1>PIPEPro - 用户管理</h1>
    
    <!-- 用户列表 -->
    <el-card class="user-card">
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
        </div>
      </template>
      
      <el-table :data="users" style="width: 100%" :height="tableHeight" size="small">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewUser(scope.row.id)">
              查看
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { getUsers, deleteUser } from '../api/user'

export default {
  name: 'Home',
  setup() {
    const router = useRouter()
    const users = ref([])
    const tableHeight = ref('')

    // 计算表格高度
    const calculateTableHeight = () => {
      const windowHeight = window.innerHeight
      tableHeight.value = windowHeight - 280 + 'px'
    }

    // 获取用户列表
    const fetchUsers = async () => {
      try {
        const response = await getUsers()
        users.value = response
      } catch (error) {
        console.error('获取用户列表失败:', error)
        ElMessage.error('获取用户列表失败')
      }
    }

    // 查看用户详情
    const viewUser = (id) => {
      router.push(`/user/${id}`)
    }

    // 删除用户
    const handleDelete = async (id) => {
      try {
        await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        await deleteUser(id)
        ElMessage.success('删除成功')
        fetchUsers() // 重新获取用户列表
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除用户失败:', error)
          ElMessage.error('删除用户失败')
        }
      }
    }

    onMounted(() => {
      fetchUsers()
      calculateTableHeight()
      window.addEventListener('resize', calculateTableHeight)
    })

    // 组件卸载时移除事件监听
    onUnmounted(() => {
      window.removeEventListener('resize', calculateTableHeight)
    })

    return {
      users,
      viewUser,
      handleDelete,
      tableHeight
    }
  }
}
</script>

<style scoped>
.home-container {
  width: 100%;
  min-height: calc(100vh - 60px);
  padding: 0;
  background-color: #2c2c2c;
  color: #e0e0e0;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 0;
  overflow: hidden;
}

.home-container h1 {
  color: #3498db;
  font-size: 1.2rem;
  margin: 0;
  padding: 8px 12px;
  text-align: center;
  letter-spacing: 1px;
  font-weight: bold;
  background-color: #34495e;
  border-bottom: 1px solid #7f8c8d;
}

/* 用户卡片 */
.user-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 工业风格卡片 */
:deep(.el-card) {
  background-color: #2c2c2c;
  border: 1px solid #34495e;
  border-radius: 3px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
  color: #e0e0e0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  margin-bottom: 0;
}

.card-header {
  display: flex;
  align-items: center;
  color: #3498db;
  font-weight: bold;
  font-size: 12px;
  padding: 4px 0;
}

/* 卡片内容区域 */
:deep(.el-card__header) {
  padding: 6px 8px;
  border-bottom: 1px solid #34495e;
}

:deep(.el-card__body) {
  padding: 6px 8px;
  flex: 1;
  overflow: hidden;
  box-sizing: border-box;
}

/* 工业风格表格 */
:deep(.el-table) {
  background-color: #2c2c2c;
  color: #e0e0e0;
  border: 1px solid #34495e;
  border-radius: 3px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
  flex: 1;
  width: 100% !important;
  font-size: 11px;
}

:deep(.el-table__header-wrapper) {
  background-color: #34495e;
}

:deep(.el-table__header-wrapper th) {
  background-color: #34495e;
  color: #3498db;
  font-weight: bold;
  border-bottom: 1px solid #7f8c8d;
  padding: 6px 8px;
  font-size: 11px;
}

:deep(.el-table__body-wrapper) {
  overflow: auto;
}

:deep(.el-table__body-wrapper tr) {
  background-color: #2c2c2c;
  border-bottom: 1px solid #34495e;
}

:deep(.el-table__body-wrapper tr:hover) {
  background-color: #34495e !important;
}

:deep(.el-table__body-wrapper td) {
  color: #e0e0e0;
  border-bottom: 1px solid #34495e;
  padding: 6px 8px;
  font-size: 11px;
}

/* 工业风格按钮 */
:deep(.el-button) {
  height: 24px !important;
  font-size: 11px !important;
  padding: 0 8px !important;
  border-radius: 3px !important;
  margin-right: 4px !important;
  margin-bottom: 4px !important;
}

:deep(.el-button--primary) {
  background-color: #3498db;
  border-color: #3498db;
  transition: all 0.3s ease;
  font-weight: 500;
}

:deep(.el-button--primary:hover) {
  background-color: #2980b9;
  border-color: #2980b9;
  box-shadow: 0 0 10px rgba(52, 152, 219, 0.4);
}

:deep(.el-button--danger) {
  background-color: #e74c3c;
  border-color: #e74c3c;
  transition: all 0.3s ease;
  font-weight: 500;
}

:deep(.el-button--danger:hover) {
  background-color: #c0392b;
  border-color: #c0392b;
  box-shadow: 0 0 10px rgba(231, 76, 60, 0.4);
}

/* 确保表格内容不被压缩 */
:deep(.el-table__column) {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>