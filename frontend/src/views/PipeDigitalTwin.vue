<template>
  <div class="pipe-digital-twin">
    <h1>PIPEPro - 管道数字孪生系统</h1>
    
    <!-- 管道概览 -->
    <el-card class="overview-card">
      <template #header>
        <div class="card-header">
          <span>管道概览</span>
        </div>
      </template>
      
      <div class="overview-stats">
        <el-statistic title="管道总长度" :value="totalLength" suffix="km" />
        <el-statistic title="监测点数量" :value="monitoringPoints" />
        <el-statistic title="当前压力" :value="currentPressure" suffix="MPa" />
        <el-statistic title="当前流量" :value="currentFlow" suffix="m³/h" />
      </div>
    </el-card>
    
    <!-- 管道可视化 -->
    <el-card class="visualization-card">
      <template #header>
        <div class="card-header">
          <span>管道可视化</span>
        </div>
      </template>
      
      <div class="pipe-visualization">
        <!-- 这里将放置管道可视化图表或3D模型 -->
        <div class="placeholder">
          <el-icon class="placeholder-icon"><DataAnalysis /></el-icon>
          <p>管道可视化区域</p>
        </div>
      </div>
    </el-card>
    
    <!-- 监测数据列表 -->
    <el-card class="data-list-card">
      <template #header>
        <div class="card-header">
          <span>实时监测数据</span>
        </div>
      </template>
      
      <el-table :data="monitoringData" style="width: 100%">
        <el-table-column prop="id" label="监测点ID" width="120" />
        <el-table-column prop="name" label="监测点名称" width="180" />
        <el-table-column prop="pressure" label="压力(MPa)" />
        <el-table-column prop="flow" label="流量(m³/h)" />
        <el-table-column prop="temperature" label="温度(℃)" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.status === '正常' ? 'success' : 'danger'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { DataAnalysis } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getPipeData } from '../api/pipe'

export default {
  name: 'PipeDigitalTwin',
  components: {
    DataAnalysis
  },
  setup() {
    // 管道概览数据
    const totalLength = ref(125.5)
    const monitoringPoints = ref(24)
    const currentPressure = ref(2.35)
    const currentFlow = ref(1250.8)
    
    // 监测数据列表
    const monitoringData = ref([
      {
        id: 'MP001',
        name: '监测点1',
        pressure: 2.35,
        flow: 1250.8,
        temperature: 25.6,
        status: '正常',
        updateTime: '2023-10-01 10:30:00'
      },
      {
        id: 'MP002',
        name: '监测点2',
        pressure: 2.32,
        flow: 1245.6,
        temperature: 25.4,
        status: '正常',
        updateTime: '2023-10-01 10:30:00'
      },
      {
        id: 'MP003',
        name: '监测点3',
        pressure: 2.28,
        flow: 1238.9,
        temperature: 25.2,
        status: '正常',
        updateTime: '2023-10-01 10:30:00'
      }
    ])
    
    // 获取管道数据
    const fetchPipeData = async () => {
      try {
        const response = await getPipeData()
        // 更新数据
        monitoringData.value = response.data.monitoringData
        totalLength.value = response.data.totalLength
        monitoringPoints.value = response.data.monitoringPoints
        currentPressure.value = response.data.currentPressure
        currentFlow.value = response.data.currentFlow
      } catch (error) {
        console.error('获取管道数据失败:', error)
        ElMessage.error('获取管道数据失败')
      }
    }
    
    onMounted(() => {
      fetchPipeData()
      // 定时更新数据
      setInterval(fetchPipeData, 30000) // 每30秒更新一次
    })
    
    return {
      totalLength,
      monitoringPoints,
      currentPressure,
      currentFlow,
      monitoringData
    }
  }
}
</script>

<style scoped>
.pipe-digital-twin {
  width: 100%;
  color: #e0e0e0;
  display: flex;
  flex-direction: column;
  gap: 0;
  box-sizing: border-box;
}

.pipe-digital-twin h1 {
  color: #3498db;
  font-size: 1.2rem;
  margin: 0;
  padding: 8px 12px;
  text-align: center;
  background-color: #34495e;
  border: 1px solid #7f8c8d;
  font-weight: bold;
}

/* 工业风格卡片 */
:deep(.el-card) {
  background-color: #2c2c2c;
  border: 1px solid #34495e;
  border-radius: 3px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
  color: #e0e0e0;
  margin-bottom: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 卡片头部样式 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #3498db;
  font-weight: bold;
  font-size: 12px;
  padding: 6px 8px;
  background-color: #34495e;
  border-bottom: 1px solid #7f8c8d;
  flex-shrink: 0;
}

/* 工业风格统计数据 */
.overview-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 0;
  margin-bottom: 0;
}

:deep(.el-statistic) {
  color: #3498db;
  font-weight: bold;
  text-align: center;
  background-color: #34495e;
  padding: 6px 8px;
  border: 1px solid #7f8c8d;
  border-radius: 3px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
}

:deep(.el-statistic__title) {
  color: #95a5a6 !important;
  font-size: 11px !important;
  margin-bottom: 4px !important;
}

:deep(.el-statistic__content) {
  color: #3498db !important;
  font-size: 14px !important;
  font-weight: bold !important;
}

/* 管道可视化区域 - 保持不变 */
.pipe-visualization {
  height: 500px;
  min-height: 500px;
  background-color: #34495e;
  border: 1px solid #7f8c8d;
  border-radius: 3px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
}

.placeholder {
  text-align: center;
  color: #95a5a6;
}

.placeholder-icon {
  font-size: 36px;
  margin-bottom: 8px;
  color: #3498db;
}

/* 监测数据区域 */
.monitoring-data {
  margin-top: 0;
}

/* 工业风格表格 */
:deep(.el-table) {
  background-color: #2c2c2c;
  color: #e0e0e0;
  border: 1px solid #34495e;
  border-radius: 3px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
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
  padding: 4px 6px;
  font-size: 11px;
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
  padding: 4px 6px;
  font-size: 11px;
}

/* 工业风格标签 */
:deep(.el-tag--success) {
  background-color: #27ae60;
  border-color: #27ae60;
  color: #ffffff;
  border-radius: 2px;
  font-size: 11px;
}

:deep(.el-tag--danger) {
  background-color: #e74c3c;
  border-color: #e74c3c;
  color: #ffffff;
  border-radius: 2px;
  font-size: 11px;
}

/* 确保卡片内容不会被压缩 */
.el-card__body {
  padding: 6px 8px;
  flex: 1;
  overflow: hidden;
  box-sizing: border-box;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .overview-stats {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .pipe-visualization {
    height: 300px;
    min-height: 300px;
  }
}
</style>