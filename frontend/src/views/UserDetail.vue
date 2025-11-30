<template>
  <div class="user-detail">
    <h1>用户详情</h1>
    
    <el-card v-if="user">
      <el-form :model="editForm" :rules="rules" ref="editFormRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="editForm.username" disabled></el-input>
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email"></el-input>
        </el-form-item>
        
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="editForm.nickname"></el-input>
        </el-form-item>
        
        <el-form-item label="修改密码">
          <el-input v-model="editForm.password" type="password" placeholder="留空表示不修改密码"></el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleUpdate" :loading="loading">保存修改</el-button>
          <el-button @click="goBack">返回列表</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { getUser, updateUser } from '../api/user'

export default {
  name: 'UserDetail',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const editFormRef = ref(null)
    const loading = ref(false)
    const user = ref(null)
    
    const editForm = reactive({
      username: '',
      email: '',
      nickname: '',
      password: ''
    })
    
    const rules = {
      email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
      ],
      nickname: [
        { required: true, message: '请输入昵称', trigger: 'blur' }
      ]
    }
    
    // 获取用户详情
    const fetchUserDetail = async () => {
      const id = route.params.id
      try {
        const response = await getUser(id)
        user.value = response
        // 填充表单数据
        editForm.username = response.username
        editForm.email = response.email
        editForm.nickname = response.nickname
      } catch (error) {
        console.error('获取用户详情失败:', error)
        ElMessage.error('获取用户详情失败')
        router.push('/')
      }
    }
    
    // 更新用户信息
    const handleUpdate = async () => {
      try {
        // 表单验证
        await editFormRef.value.validate()
        loading.value = true
        
        const id = route.params.id
        // 准备更新数据，过滤掉空密码
        const updateData = {
          email: editForm.email,
          nickname: editForm.nickname
        }
        // 只有在输入了密码时才更新密码
        if (editForm.password) {
          updateData.password = editForm.password
        }
        
        await updateUser(id, updateData)
        ElMessage.success('更新成功')
        
        // 重置密码字段
        editForm.password = ''
      } catch (error) {
        if (error !== false) { // 排除表单验证失败的情况
          console.error('更新失败:', error)
          ElMessage.error(error.response?.data || '更新失败，请稍后重试')
        }
      } finally {
        loading.value = false
      }
    }
    
    // 返回列表页
    const goBack = () => {
      router.push('/')
    }
    
    onMounted(() => {
      fetchUserDetail()
    })
    
    return {
      editFormRef,
      editForm,
      rules,
      loading,
      user,
      handleUpdate,
      goBack
    }
  }
}
</script>

<style scoped>
.user-detail {
  max-width: 800px;
  margin: 0 auto;
}

:deep(.el-card) {
  margin-top: 20px;
}
</style>