<template>
  <div class="pipe-digital-twin-full">
    <!-- <h1>PIPEPro - 管道数字孪生系统</h1> -->
    
    <!-- 顶部工具栏 -->
    <el-card class="toolbar-card">
      <div class="toolbar">
        <el-button type="primary" @click="initializeParameters">初始化参数</el-button>
        <el-button type="success" @click="runSimulation" :disabled="isSimulating">运行模拟</el-button>
        <el-button type="warning" @click="stopSimulation" :disabled="!isSimulating">停止模拟</el-button>
        <el-button type="info" @click="checkHealth">健康检查</el-button>
        <el-button type="info" @click="predictLifetime">寿命预测</el-button>
        <el-button type="danger" @click="simulateFault">故障模拟</el-button>
        <el-button type="default" @click="exportData">导出数据</el-button>
      </div>
    </el-card>
    
    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 左侧参数面板 -->
      <el-card class="params-panel">
        <template #header>
          <div class="card-header">
            <span>管道参数</span>
          </div>
        </template>
        
        <!-- 基本参数 -->
        <el-collapse v-model="activeParamsTab">
          <el-collapse-item title="基本参数" name="basic">
            <el-form label-position="left" label-width="100px">
              <el-form-item label="管道长度:">
                <el-input-number v-model="pipeParams.length" :min="1" :max="10000" :step="1" suffix="m"/>
              </el-form-item>
              <el-form-item label="管道直径:">
                <el-input-number v-model="pipeParams.diameter" :min="0.1" :max="5" :step="0.1" suffix="m"/>
              </el-form-item>
              <el-form-item label="管道壁厚:">
                <el-input-number v-model="pipeParams.thickness" :min="0.001" :max="0.5" :step="0.001" suffix="m"/>
              </el-form-item>
              <el-form-item label="材料密度:">
                <el-input-number v-model="pipeParams.materialDensity" :min="1000" :max="10000" :step="10" suffix="kg/m³"/>
              </el-form-item>
              <el-form-item label="弹性模量:">
                <el-input-number v-model="pipeParams.elasticityModulus" :min="1e10" :max="5e11" :step="1e9" suffix="Pa"/>
              </el-form-item>
            </el-form>
          </el-collapse-item>
          
          <!-- 运行参数 -->
          <el-collapse-item title="运行参数" name="operation">
            <el-form label-position="left" label-width="100px">
              <el-form-item label="初始压力:">
                <el-input-number v-model="pipeParams.initialPressure" :min="0.1" :max="10" :step="0.1" suffix="MPa"/>
              </el-form-item>
              <el-form-item label="初始温度:">
                <el-input-number v-model="pipeParams.initialTemperature" :min="0" :max="200" :step="1" suffix="°C"/>
              </el-form-item>
              <el-form-item label="流量:">
                <el-input-number v-model="pipeParams.flowRate" :min="0" :max="1000" :step="1" suffix="m³/h"/>
              </el-form-item>
              <el-form-item label="介质粘度:">
                <el-input-number v-model="pipeParams.fluidViscosity" :min="0.0001" :max="0.1" :step="0.0001" suffix="Pa·s"/>
              </el-form-item>
            </el-form>
          </el-collapse-item>
          
          <!-- 模拟控制 -->
          <el-collapse-item title="模拟控制" name="simulation">
            <el-form label-position="left" label-width="100px">
              <el-form-item label="时间步长:">
                <el-input-number v-model="simulationParams.timeStep" :min="1" :max="100" :step="1" suffix="年"/>
              </el-form-item>
              <el-form-item label="模拟速度:">
                <el-select v-model="simulationParams.simulationSpeed">
                  <el-option label="慢速" value="1"/>
                  <el-option label="中速" value="2"/>
                  <el-option label="快速" value="5"/>
                  <el-option label="极速" value="10"/>
                </el-select>
              </el-form-item>
              <el-form-item label="实时更新:">
                <el-switch v-model="simulationParams.realTime"/>
              </el-form-item>
            </el-form>
          </el-collapse-item>
        </el-collapse>
      </el-card>
      
      <!-- 中间3D可视化和状态区 -->
      <div class="visualization-section">
        <!-- 3D可视化区域 -->
        <el-card class="visualization-card">
          <template #header>
            <div class="card-header">
              <span>管道3D可视化</span>
            </div>
          </template>
          <div class="pipe-visualization">
            <!-- 这里将放置Three.js 3D视图 -->
            <div id="three-container" ref="threeContainer"></div>
          </div>
        </el-card>
        
        <!-- 状态显示栏 -->
        <el-card class="status-bar">
          <div class="status-info">
            <el-tag :type="statusColor" size="small">{{ statusText }}</el-tag>
            <span>压力: {{ currentState.pressure.toFixed(2) }} MPa</span>
            <span>温度: {{ currentState.temperature.toFixed(1) }} °C</span>
            <span>健康状态: {{ currentState.health.toFixed(0) }}%</span>
            <span>运行时间: {{ pipeParams.currentAge }} 年</span>
          </div>
        </el-card>
        
        <!-- 监测数据历史 -->
        <el-card class="history-data-card">
          <template #header>
            <div class="card-header">
              <span>历史数据</span>
              <el-button 
                type="text" 
                size="small" 
                @click="toggleHistoryDrawer"
              >
                {{ isHistoryDrawerOpen ? '收起' : '查看' }}
              </el-button>
            </div>
          </template>
          
          <!-- 历史数据摘要 -->
          <div class="history-summary-card">
            <div class="summary-info">
              <span class="summary-text">历史数据: {{ stateHistory.length }} 条</span>
              <el-button 
                type="primary" 
                size="small" 
                @click="toggleHistoryDrawer"
              >
                查看历史数据
              </el-button>
            </div>
          </div>
        </el-card>
        
        <!-- 历史数据上拉抽屉 -->
        <div 
          class="history-drawer-fixed" 
          :class="{ 'history-drawer-visible': isHistoryDrawerOpen }"
        >
          <div class="history-drawer-content">
            <!-- 抽屉头部 -->
            <div class="history-drawer-header">
              <h3>历史数据列表</h3>
              <el-button 
                type="text" 
                size="small" 
                @click="toggleHistoryDrawer"
              >
                收起
              </el-button>
            </div>
            
            <!-- 抽屉内容 -->
            <div class="history-drawer-body">
              <!-- 抽屉头部操作 -->
              <div class="drawer-header-actions">
                <span class="history-count">共 {{ stateHistory.length }} 条历史数据</span>
                <el-button 
                  type="danger" 
                  size="small" 
                  @click="clearHistory"
                  :disabled="stateHistory.length === 0"
                >
                  清空历史
                </el-button>
              </div>
              
              <!-- 历史数据列表 -->
              <div class="history-data-list">
                <div 
                  v-for="(item, index) in stateHistory" 
                  :key="index"
                  class="history-data-item"
                  draggable="true"
                  @dragstart="handleRowDragStart($event, item)"
                >
                  <div class="history-item-header">
                    <span class="item-timestamp">{{ item.timestamp }}</span>
                  </div>
                  <div class="history-item-details">
                    <div class="detail-row">
                      <span class="detail-item">压力: {{ item.pressure.toFixed(2) }} MPa</span>
                      <span class="detail-item">温度: {{ item.temperature.toFixed(1) }} °C</span>
                      <span class="detail-item">流量: {{ item.flowRate.toFixed(0) }} m³/h</span>
                    </div>
                    <div class="detail-row">
                      <span class="detail-item">
                        健康: 
                        <el-progress 
                          :percentage="item.health" 
                          :stroke-width="8" 
                          :color="getHealthColor(item.health)"
                          :show-text="false"
                        />
                        {{ item.health.toFixed(0) }}%
                      </span>
                      <span class="detail-item">
                        状态: 
                        <el-tag :type="item.faultStatus === '正常' ? 'success' : 'danger'" size="small">
                          {{ item.faultStatus }}
                        </el-tag>
                      </span>
                      <span class="detail-item">
                        年龄: {{ item.currentAge }} 年
                      </span>
                    </div>
                    <div class="detail-row">
                      <span class="detail-item">腐蚀率: {{ item.corrosionRate }} mm/年</span>
                      <span class="detail-item">剩余寿命: {{ Math.max(0, item.serviceLife - item.currentAge) }} 年</span>
                    </div>
                    <div class="detail-row">
                      <span class="detail-item">长度: {{ item.length }} m</span>
                      <span class="detail-item">直径: {{ item.diameter }} m</span>
                      <span class="detail-item">壁厚: {{ item.thickness }} m</span>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- 空状态 -->
              <div class="history-empty" v-if="stateHistory.length === 0">
                <el-empty description="暂无历史数据，点击运行模拟生成历史数据" />
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 右侧数据处理区域 -->
      <div class="data-processing-section" :class="{ 'collapsed': isProcessingCollapsed }">
        <div class="processing-header">
          <h3>数据处理</h3>
          <div class="processing-controls">
            <el-button type="text" @click="toggleProcessingPanel" size="small">
              {{ isProcessingCollapsed ? '展开' : '收起' }}
            </el-button>
          </div>
        </div>
        
        <div class="processing-content" ref="processingContent">
          <!-- 模型选择 -->
          <div class="model-selection">
            <h4>选择处理模型</h4>
            <el-radio-group v-model="selectedModel" size="small">
              <el-radio-button label="trend">趋势图</el-radio-button>
              <el-radio-button label="statistics">数据统计</el-radio-button>
              <el-radio-button label="anomaly">异常检测</el-radio-button>
              <el-radio-button label="correlation">相关性分析</el-radio-button>
            </el-radio-group>
          </div>
          
          <!-- 拖放区域 -->
          <div class="drop-zone" :class="{ 'has-data': droppedData.length > 0 }" @dragover.prevent @drop="handleDrop">
            <div v-if="droppedData.length === 0" class="drop-zone-empty">
              <el-icon class="drop-icon"><el-icon-document-copy /></el-icon>
              <p>从左侧历史数据表格中拖动数据到此处</p>
            </div>
            <div v-else class="dropped-data-list">
              <h4>已选择数据 ({{ droppedData.length }})</h4>
              <el-tag 
                v-for="(item, index) in droppedData" 
                :key="index" 
                closable 
                @close="removeDroppedData(index)"
                size="small"
              >
                {{ item.timestamp }} - 压力: {{ item.pressure.toFixed(2) }} MPa - 健康: {{ item.health.toFixed(0) }}%
              </el-tag>
            </div>
          </div>
          
          <!-- 执行按钮 -->
          <div class="execute-section">
            <el-button 
              type="primary" 
              @click="executeProcessing" 
              :disabled="droppedData.length === 0"
              size="small"
            >
              执行处理
            </el-button>
          </div>
          
          <!-- 结果展示区域 -->
          <div class="result-section" ref="resultSection" :style="resultSectionStyle">
            <div class="result-header">
              <h4>处理结果</h4>
              <el-button 
                v-if="processingResult" 
                type="primary" 
                size="small" 
                @click="downloadChart"
                class="download-button"
              >
                <el-icon><el-icon-download /></el-icon>
                下载图片
              </el-button>
            </div>
            <div class="result-content" ref="resultContent">
              <div v-if="!processingResult" class="result-empty">
                <p>执行处理后将显示结果</p>
              </div>
              <div v-else class="result-chart">
                <div ref="chartContainer"></div>
              </div>
            </div>
            <!-- 拖动手柄 -->
            <div class="resize-handles">
              <div class="resize-handle resize-handle-n" @mousedown="startResizeResult($event, 'n')"></div>
              <div class="resize-handle resize-handle-e" @mousedown="startResizeResult($event, 'e')"></div>
              <div class="resize-handle resize-handle-s" @mousedown="startResizeResult($event, 's')"></div>
              <div class="resize-handle resize-handle-w" @mousedown="startResizeResult($event, 'w')"></div>
              <div class="resize-handle resize-handle-ne" @mousedown="startResizeResult($event, 'ne')"></div>
              <div class="resize-handle resize-handle-se" @mousedown="startResizeResult($event, 'se')"></div>
              <div class="resize-handle resize-handle-sw" @mousedown="startResizeResult($event, 'sw')"></div>
              <div class="resize-handle resize-handle-nw" @mousedown="startResizeResult($event, 'nw')"></div>
            </div>
          </div>
        </div>
        
        <!-- 拉伸手柄 -->
        <div class="resize-handle" @mousedown="startResize"></div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import * as THREE from 'three'
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import * as echarts from 'echarts'
import { getHistoricalData, saveSimulationHistory, getSimulationHistory } from '../api/pipe'

export default {
  name: 'PipeDigitalTwinFull',
  setup() {
    // 管道参数
    const pipeParams = reactive({
      length: 100,
      diameter: 0.5,
      thickness: 0.01,
      materialDensity: 7850,
      elasticityModulus: 2.1e11,
      initialPressure: 5.0,
      initialTemperature: 25.0,
      flowRate: 100,
      fluidViscosity: 0.001,
      material: '低碳钢',
      corrosionRate: 0.005,
      serviceLife: 20,
      currentAge: 0
    })
    
    // 模拟参数
    const simulationParams = reactive({
      timeStep: 10,
      simulationSpeed: '2',
      realTime: true
    })
    
    // 模拟状态
    const isSimulating = ref(false)
    const statusText = ref('就绪')
    const statusColor = ref('success')
    const activeParamsTab = ref(['basic'])
    
    // 当前状态
    const currentState = reactive({
      timestamp: new Date().toLocaleString(),
      pressure: 5.0,
      temperature: 25.0,
      flowRate: 100,
      health: 100,
      faultStatus: '正常'
    })
    
    // 状态历史
    const stateHistory = ref([])
    
    // Three.js 相关
    let scene, camera, renderer, controls
    let pipeModel = null
    let animationId = null
    const threeContainer = ref(null)
    
    // 数据处理区域相关
    const isProcessingCollapsed = ref(false)
    const selectedModel = ref('trend')
    const droppedData = ref([])
    const processingResult = ref(null)
    const processingContent = ref(null)
    const resultContent = ref(null)
    const chartContainer = ref(null)
    let chartInstance = null
    
    // 历史数据选择相关
    const isHistoryDrawerOpen = ref(false)
    
    // 拉伸相关
    const isResizing = ref(false)
    const resizeStartX = ref(0)
    const resizeStartWidth = ref(0)
    
    // 切换历史数据抽屉
    const toggleHistoryDrawer = () => {
      isHistoryDrawerOpen.value = !isHistoryDrawerOpen.value
    }
    
    // 结果区域拉伸相关
    const resultSection = ref(null)
    const resultSectionStyle = reactive({
      width: '100%',
      height: 'auto',
      minHeight: '300px', // 增加最小高度
      position: 'relative'
    })
    const isResizingResult = ref(false)
    const resizeResultStartPos = ref({ x: 0, y: 0 })
    const resizeResultStartSize = ref({ width: 0, height: 0 })
    const resizeResultDirection = ref('')
    

    
    // 计算健康状态
    const calculateHealth = () => {
      // 简单的健康状态计算逻辑
      const calculatedHealth = 100 - (pipeParams.currentAge / pipeParams.serviceLife) * 100
      return Math.max(0, Math.min(100, calculatedHealth))
    }
    
    // 检查故障
    const checkForFaults = () => {
      const currentHealth = calculateHealth()
      if (currentHealth < 20) return '严重故障'
      if (currentHealth < 50) return '警告'
      return '正常'
    }
    
    // 更新状态
    const updateStatusBar = () => {
      // 更新时间戳
      currentState.timestamp = new Date().toLocaleString()
      currentState.health = calculateHealth()
      currentState.faultStatus = checkForFaults()
      
      // 保存到历史记录，包含完整信息
      const historyItem = {
        ...JSON.parse(JSON.stringify(currentState)),
        // 添加pipeParams的相关字段，确保每条记录都有完整信息
        currentAge: pipeParams.currentAge,
        corrosionRate: pipeParams.corrosionRate,
        serviceLife: pipeParams.serviceLife,
        length: pipeParams.length,
        diameter: pipeParams.diameter,
        thickness: pipeParams.thickness
      }
      
      // 添加到历史记录开头，实现倒序排列
      stateHistory.value.unshift(historyItem)
      
      // 限制历史记录数量
      if (stateHistory.value.length > 100) {
        stateHistory.value.pop()
      }
      
      // 调用API保存到后端
      const saveHistoryToBackend = async () => {
        try {
          // 转换时间格式为Date对象
          const historyData = {
            ...historyItem,
            timestamp: new Date(),
            pressure: historyItem.pressure,
            flowRate: historyItem.flowRate,
            temperature: historyItem.temperature,
            health: historyItem.health,
            faultStatus: historyItem.faultStatus,
            currentAge: historyItem.currentAge,
            corrosionRate: historyItem.corrosionRate,
            serviceLife: historyItem.serviceLife,
            length: historyItem.length,
            diameter: historyItem.diameter,
            thickness: historyItem.thickness
          }
          await saveSimulationHistory(historyData)
        } catch (error) {
          console.error('保存历史数据到后端失败:', error)
        }
      }
      
      // 调用保存函数
      saveHistoryToBackend()
    }
    
    // 创建管道3D模型
    const createPipeModel = () => {
      if (pipeModel) {
        scene.remove(pipeModel)
      }
      
      // 根据健康状态设置材质
      const modelHealth = calculateHealth()
      let materialColor;
      if (modelHealth > 80) materialColor = 0x00ff00; // 绿色
      else if (modelHealth > 60) materialColor = 0x90ee90; // 浅绿色
      else if (modelHealth > 40) materialColor = 0xffff00; // 黄色
      else if (modelHealth > 20) materialColor = 0xffa500; // 橙色
      else materialColor = 0xff0000; // 红色
      
      // 创建管道组
      pipeModel = new THREE.Group()
      
      const segments = 32;
      const height = pipeParams.length / 10;
      const outerRadius = pipeParams.diameter / 2;
      const innerRadius = outerRadius - pipeParams.thickness;
      
      // 创建管道材质
      const material = new THREE.MeshStandardMaterial({
        color: materialColor,
        roughness: 0.5,
        metalness: 0.5,
        side: THREE.DoubleSide, // 双面渲染，确保能看到管道内部
        transparent: true,
        opacity: 0.8
      });
      
      // 使用单个圆柱体创建空心管道，通过材质设置实现空心效果
      // 创建圆柱体几何体
      const cylinderGeometry = new THREE.CylinderGeometry(
        outerRadius,
        outerRadius,
        height,
        segments,
        1, // 高度分段数设为1
        true // 打开底面，形成空心效果
      );
      
      // 创建管道网格
      const pipeMesh = new THREE.Mesh(cylinderGeometry, material);
      pipeMesh.rotation.x = Math.PI / 2;
      pipeModel.add(pipeMesh);
      
      scene.add(pipeModel);
    }
    
    // 初始化Three.js场景
    const initializeThreeScene = () => {
      if (!threeContainer.value) return
      
      // 创建场景
      scene = new THREE.Scene()
      scene.background = new THREE.Color(0x0d47a1)
      
      // 创建相机
      const width = threeContainer.value.clientWidth
      const height = threeContainer.value.clientHeight
      camera = new THREE.PerspectiveCamera(75, width / height, 0.1, 1000)
      camera.position.set(0, 50, 50)
      camera.lookAt(0, 0, 0)
      
      // 创建渲染器
      renderer = new THREE.WebGLRenderer({ antialias: true })
      renderer.setSize(width, height)
      renderer.setPixelRatio(window.devicePixelRatio)
      threeContainer.value.innerHTML = ''
      threeContainer.value.appendChild(renderer.domElement)
      
      // 添加轨道控制器
      controls = new OrbitControls(camera, renderer.domElement)
      controls.enableDamping = true
      controls.dampingFactor = 0.05
      
      // 添加光源
      const ambientLight = new THREE.AmbientLight(0x404040, 1)
      scene.add(ambientLight)
      
      const directionalLight = new THREE.DirectionalLight(0xffffff, 0.5)
      directionalLight.position.set(10, 10, 10)
      scene.add(directionalLight)
      
      // 添加坐标轴辅助
      const axesHelper = new THREE.AxesHelper(50)
      scene.add(axesHelper)
      
      // 创建管道模型
      createPipeModel()
      
      // 开始动画循环
      const animate = () => {
        animationId = requestAnimationFrame(animate)
        controls.update()
        renderer.render(scene, camera)
        // 移除自动更新历史数据，只在运行模拟时更新
      }
      animate()
      
      // 处理窗口大小变化
      const handleResize = () => {
        if (!threeContainer.value) return
        const width = threeContainer.value.clientWidth
        const height = threeContainer.value.clientHeight
        camera.aspect = width / height
        camera.updateProjectionMatrix()
        renderer.setSize(width, height)
      }
      window.addEventListener('resize', handleResize)
    }
    
    // 初始化参数
    const initializeParameters = async () => {
      try {
        const response = await axios.post('/api/pipe/initialize-parameters')
        const data = response.data
        
        // 更新本地参数
        Object.assign(pipeParams, data)
        Object.assign(currentState, {
          pressure: data.currentPressure,
          temperature: data.currentTemperature,
          flowRate: data.flowRate
        })
        
        createPipeModel()
        // 移除初始化参数时生成历史数据
        statusText.value = '参数已初始化'
        statusColor.value = 'success'
        ElMessage.success('参数初始化成功')
      } catch (error) {
        console.error('初始化参数失败:', error)
        ElMessage.error('初始化参数失败')
      }
    }
    
    // 运行模拟
    let simulationTimer = null
    const runSimulation = async () => {
      isSimulating.value = true
      statusText.value = '正在模拟运行...'
      statusColor.value = 'warning'
      
      const speed = parseInt(simulationParams.simulationSpeed)
      const interval = 1000 / speed
      
      simulationTimer = setInterval(async () => {
        try {
          const response = await axios.post('/api/pipe/run-simulation')
          const data = response.data
          
          // 更新本地参数
          Object.assign(pipeParams, data)
          Object.assign(currentState, {
            pressure: data.currentPressure,
            temperature: data.currentTemperature,
            flowRate: data.flowRate
          })
          
          // 更新3D模型
          createPipeModel()
          
          // 更新状态
          updateStatusBar()
          
          // 检查是否达到设计寿命
          if (pipeParams.currentAge >= pipeParams.serviceLife) {
            stopSimulation()
            statusText.value = '达到设计寿命'
            statusColor.value = 'danger'
            ElMessage.warning('模拟已达到设计寿命')
          }
        } catch (error) {
          console.error('运行模拟失败:', error)
          ElMessage.error('运行模拟失败')
        }
      }, interval)
    }
    
    // 停止模拟
    const stopSimulation = () => {
      isSimulating.value = false
      if (simulationTimer) {
        clearInterval(simulationTimer)
        simulationTimer = null
      }
      statusText.value = '模拟已停止'
      statusColor.value = 'warning'
    }
    
    // 健康检查
    const checkHealth = async () => {
      try {
        const response = await axios.get('/api/pipe/health-check')
        const result = response.data
        
        statusText.value = result
        statusColor.value = 'info'
        ElMessage.info(result)
      } catch (error) {
        console.error('健康检查失败:', error)
        ElMessage.error('健康检查失败')
      }
    }
    
    // 寿命预测
    const predictLifetime = async () => {
      try {
        const response = await axios.get('/api/pipe/predict-lifetime')
        const result = response.data
        
        statusText.value = result
        statusColor.value = 'info'
        ElMessage.info(result)
      } catch (error) {
        console.error('寿命预测失败:', error)
        ElMessage.error('寿命预测失败')
      }
    }
    
    // 故障模拟
    const simulateFault = async () => {
      try {
        const response = await axios.post('/api/pipe/simulate-fault')
        const data = response.data
        
        // 更新本地参数
        Object.assign(pipeParams, data)
        Object.assign(currentState, {
          pressure: data.currentPressure,
          temperature: data.currentTemperature,
          flowRate: data.flowRate
        })
        
        createPipeModel()
        // 移除故障模拟时生成历史数据
        statusText.value = '故障模拟已执行'
        statusColor.value = 'danger'
        ElMessage.warning('故障模拟已执行')
      } catch (error) {
        console.error('故障模拟失败:', error)
        ElMessage.error('故障模拟失败')
      }
    }
    
    // 导出数据
    const exportData = () => {
      // 简单的数据导出逻辑
      const data = {
        pipeParams: { ...pipeParams },
        stateHistory: [...stateHistory.value]
      }
      
      const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `pipe-simulation-data-${new Date().toISOString().slice(0, 10)}.json`
      a.click()
      URL.revokeObjectURL(url)
      
      statusText.value = '数据已导出'
      statusColor.value = 'success'
    }
    
    // 清空历史数据
    const clearHistory = () => {
      stateHistory.value = []
    }
    
    // 获取健康状态颜色
    const getHealthColor = (health) => {
      if (health > 80) return '#00ff00'
      if (health > 60) return '#90ee90'
      if (health > 40) return '#ffff00'
      if (health > 20) return '#ffa500'
      return '#ff0000'
    }
    
    // 从后端获取管道参数
    const fetchPipeParameters = async () => {
      try {
        const response = await axios.get('/api/pipe/parameters')
        const data = response.data
        
        // 更新本地参数
        Object.assign(pipeParams, data)
        Object.assign(currentState, {
          pressure: data.currentPressure,
          temperature: data.currentTemperature,
          flowRate: data.flowRate
        })
        
        createPipeModel()
        // 移除获取参数时生成历史数据
      } catch (error) {
        console.error('获取管道参数失败:', error)
        // 如果获取失败，初始化参数
        initializeParameters()
      }
    }
    
    // 数据处理区域相关函数
    
    // 切换处理面板显示/隐藏
    const toggleProcessingPanel = () => {
      isProcessingCollapsed.value = !isProcessingCollapsed.value
    }
    
    // 处理表格行拖动开始
    const handleRowDragStart = (event, row) => {
      event.dataTransfer.setData('text/plain', JSON.stringify(row))
      event.dataTransfer.effectAllowed = 'copy'
    }
    
    // 处理拖放事件
    const handleDrop = (event) => {
      event.preventDefault()
      try {
        const data = JSON.parse(event.dataTransfer.getData('text/plain'))
        if (data) {
          // 检查是否已存在相同数据
          const exists = droppedData.value.some(item => item.timestamp === data.timestamp)
          if (!exists) {
            droppedData.value.push(data)
          }
        }
      } catch (error) {
        console.error('拖放数据解析失败:', error)
      }
    }
    
    // 移除已拖放的数据
    const removeDroppedData = (index) => {
      droppedData.value.splice(index, 1)
    }
    
    // 开始拉伸
    const startResize = (event) => {
      isResizing.value = true
      resizeStartX.value = event.clientX
      const processingSection = event.target.parentElement
      resizeStartWidth.value = processingSection.offsetWidth
      document.addEventListener('mousemove', resize)
      document.addEventListener('mouseup', stopResize)
    }
    
    // 拉伸过程
    const resize = (event) => {
      if (!isResizing.value) return
      const deltaX = event.clientX - resizeStartX.value
      const processingSection = document.querySelector('.data-processing-section')
      const newWidth = Math.max(200, Math.min(800, resizeStartWidth.value - deltaX))
      processingSection.style.width = `${newWidth}px`
    }
    
    // 停止拉伸
    const stopResize = () => {
      isResizing.value = false
      document.removeEventListener('mousemove', resize)
      document.removeEventListener('mouseup', stopResize)
    }
    
    // 开始拉伸结果区域
    const startResizeResult = (event, direction) => {
      isResizingResult.value = true
      resizeResultDirection.value = direction
      resizeResultStartPos.value = { x: event.clientX, y: event.clientY }
      
      if (resultSection.value) {
        const rect = resultSection.value.getBoundingClientRect()
        resizeResultStartSize.value = {
          width: rect.width,
          height: rect.height
        }
      }
      
      document.addEventListener('mousemove', resizeResult)
      document.addEventListener('mouseup', stopResizeResult)
      event.preventDefault()
    }
    
    // 拉伸结果区域过程
    const resizeResult = (event) => {
      if (!isResizingResult.value || !resultSection.value) return
      
      const deltaX = event.clientX - resizeResultStartPos.value.x
      const deltaY = event.clientY - resizeResultStartPos.value.y
      
      let newWidth = resizeResultStartSize.value.width
      let newHeight = resizeResultStartSize.value.height
      
      // 根据方向调整大小
      switch (resizeResultDirection.value) {
        case 'n':
          newHeight = Math.max(150, resizeResultStartSize.value.height - deltaY)
          break
        case 'e':
          newWidth = Math.max(200, resizeResultStartSize.value.width + deltaX)
          break
        case 's':
          newHeight = Math.max(150, resizeResultStartSize.value.height + deltaY)
          break
        case 'w':
          newWidth = Math.max(200, resizeResultStartSize.value.width - deltaX)
          break
        case 'ne':
          newWidth = Math.max(200, resizeResultStartSize.value.width + deltaX)
          newHeight = Math.max(150, resizeResultStartSize.value.height - deltaY)
          break
        case 'se':
          newWidth = Math.max(200, resizeResultStartSize.value.width + deltaX)
          newHeight = Math.max(150, resizeResultStartSize.value.height + deltaY)
          break
        case 'sw':
          newWidth = Math.max(200, resizeResultStartSize.value.width - deltaX)
          newHeight = Math.max(150, resizeResultStartSize.value.height + deltaY)
          break
        case 'nw':
          newWidth = Math.max(200, resizeResultStartSize.value.width - deltaX)
          newHeight = Math.max(150, resizeResultStartSize.value.height - deltaY)
          break
      }
      
      // 更新样式
      resultSectionStyle.width = `${newWidth}px`
      resultSectionStyle.height = `${newHeight}px`
      
      // 调整图表大小
      resizeChart()
    }
    
    // 停止拉伸结果区域
    const stopResizeResult = () => {
      isResizingResult.value = false
      document.removeEventListener('mousemove', resizeResult)
      document.removeEventListener('mouseup', stopResizeResult)
    }
    
    // 执行数据处理
    const executeProcessing = () => {
      if (droppedData.value.length === 0) return
      
      // 根据选择的模型执行不同的处理逻辑
      switch (selectedModel.value) {
        case 'trend':
          processTrendData()
          break
        case 'statistics':
          processStatisticsData()
          break
        case 'anomaly':
          processAnomalyData()
          break
        case 'correlation':
          processCorrelationData()
          break
        default:
          processTrendData()
      }
    }
    
    // 处理趋势图数据
    const processTrendData = () => {
      // 准备图表数据
      const timestamps = droppedData.value.map(item => item.timestamp)
      const pressures = droppedData.value.map(item => item.pressure)
      const temperatures = droppedData.value.map(item => item.temperature)
      const flowRates = droppedData.value.map(item => item.flowRate)
      const healths = droppedData.value.map(item => item.health)
      
      // 创建图表
      renderChart({
        timestamps,
        series: [
          { name: '压力', data: pressures, color: '#3498db' },
          { name: '温度', data: temperatures, color: '#e74c3c' },
          { name: '流量', data: flowRates, color: '#2ecc71' },
          { name: '健康状态', data: healths, color: '#f39c12' }
        ]
      })
      
      processingResult.value = 'trend'
    }
    
    // 处理数据统计
    const processStatisticsData = () => {
      // 简单的统计计算
      const pressures = droppedData.value.map(item => item.pressure)
      const temperatures = droppedData.value.map(item => item.temperature)
      const flowRates = droppedData.value.map(item => item.flowRate)
      const healths = droppedData.value.map(item => item.health)
      
      const calculateStats = (data) => {
        const sum = data.reduce((acc, val) => acc + val, 0)
        const avg = sum / data.length
        const min = Math.min(...data)
        const max = Math.max(...data)
        const sorted = [...data].sort((a, b) => a - b)
        const median = sorted.length % 2 === 0 
          ? (sorted[sorted.length / 2 - 1] + sorted[sorted.length / 2]) / 2
          : sorted[Math.floor(sorted.length / 2)]
        
        return { sum, avg, min, max, median }
      }
      
      const stats = {
        pressure: calculateStats(pressures),
        temperature: calculateStats(temperatures),
        flowRate: calculateStats(flowRates),
        health: calculateStats(healths)
      }
      
      // 创建统计图表
      renderStatisticsChart(stats)
      processingResult.value = 'statistics'
    }
    
    // 处理异常检测
    const processAnomalyData = () => {
      // 简单的异常检测逻辑（基于阈值）
      const anomalies = droppedData.value.map((item, index) => {
        const isAnomaly = item.health < 50 || item.pressure > 8 || item.temperature > 100
        return {
          ...item,
          isAnomaly
        }
      })
      
      // 创建异常检测图表
      renderAnomalyChart(anomalies)
      processingResult.value = 'anomaly'
    }
    
    // 处理相关性分析
    const processCorrelationData = () => {
      // 简单的相关性分析
      const pressures = droppedData.value.map(item => item.pressure)
      const temperatures = droppedData.value.map(item => item.temperature)
      const flowRates = droppedData.value.map(item => item.flowRate)
      const healths = droppedData.value.map(item => item.health)
      
      // 计算相关系数
      const calculateCorrelation = (data1, data2) => {
        const n = data1.length
        const sum1 = data1.reduce((acc, val) => acc + val, 0)
        const sum2 = data2.reduce((acc, val) => acc + val, 0)
        const sum1Sq = data1.reduce((acc, val) => acc + val * val, 0)
        const sum2Sq = data2.reduce((acc, val) => acc + val * val, 0)
        const pSum = data1.reduce((acc, val, i) => acc + val * data2[i], 0)
        
        const num = pSum - (sum1 * sum2 / n)
        const den = Math.sqrt((sum1Sq - sum1 * sum1 / n) * (sum2Sq - sum2 * sum2 / n))
        
        return den === 0 ? 0 : num / den
      }
      
      const correlations = {
        pressureTemperature: calculateCorrelation(pressures, temperatures),
        pressureFlowRate: calculateCorrelation(pressures, flowRates),
        pressureHealth: calculateCorrelation(pressures, healths),
        temperatureFlowRate: calculateCorrelation(temperatures, flowRates),
        temperatureHealth: calculateCorrelation(temperatures, healths),
        flowRateHealth: calculateCorrelation(flowRates, healths)
      }
      
      // 创建相关性图表
      renderCorrelationChart(correlations)
      processingResult.value = 'correlation'
    }
    
    // 监听图表容器大小变化
    let resizeObserver = null
    
    // 调整图表大小
    const resizeChart = () => {
      if (chartInstance) {
        chartInstance.resize()
      }
    }
    
    // 初始化图表大小监听
    const initChartResizeObserver = () => {
      if (!chartContainer.value) return
      
      // 销毁现有观察者
      if (resizeObserver) {
        resizeObserver.disconnect()
      }
      
      // 创建新的ResizeObserver
      resizeObserver = new ResizeObserver(() => {
        resizeChart()
      })
      
      // 开始观察容器大小变化
      resizeObserver.observe(chartContainer.value)
    }
    
    // 渲染图表
    const renderChart = (chartData) => {
      if (!chartContainer.value) return
      
      // 销毁现有图表
      if (chartInstance) {
        chartInstance.dispose()
      }
      
      // 创建新图表
      chartInstance = echarts.init(chartContainer.value)
      
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          }
        },
        legend: {
          data: chartData.series.map(item => item.name),
          textStyle: {
            color: '#e0e0e0'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: chartData.timestamps,
          axisLabel: {
            color: '#bdc3c7',
            rotate: 45
          },
          axisLine: {
            lineStyle: {
              color: '#34495e'
            }
          }
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            color: '#bdc3c7'
          },
          axisLine: {
            lineStyle: {
              color: '#34495e'
            }
          },
          splitLine: {
            lineStyle: {
              color: '#34495e',
              type: 'dashed'
            }
          }
        },
        series: chartData.series.map(item => ({
          name: item.name,
          type: 'line',
          data: item.data,
          itemStyle: {
            color: item.color
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              {
                offset: 0,
                color: item.color + '80'
              },
              {
                offset: 1,
                color: item.color + '10'
              }
            ])
          }
        }))
      }
      
      chartInstance.setOption(option)
      
      // 初始化大小监听
      initChartResizeObserver()
    }
    
    // 渲染统计图表
    const renderStatisticsChart = (stats) => {
      if (!chartContainer.value) return
      
      if (chartInstance) {
        chartInstance.dispose()
      }
      
      chartInstance = echarts.init(chartContainer.value)
      
      const categories = ['压力', '温度', '流量', '健康状态']
      const metrics = ['平均值', '最小值', '最大值', '中位数']
      
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {
          data: metrics,
          textStyle: {
            color: '#e0e0e0'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: categories,
          axisLabel: {
            color: '#bdc3c7'
          },
          axisLine: {
            lineStyle: {
              color: '#34495e'
            }
          }
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            color: '#bdc3c7'
          },
          axisLine: {
            lineStyle: {
              color: '#34495e'
            }
          },
          splitLine: {
            lineStyle: {
              color: '#34495e',
              type: 'dashed'
            }
          }
        },
        series: [
          {
            name: '平均值',
            type: 'bar',
            data: [stats.pressure.avg, stats.temperature.avg, stats.flowRate.avg, stats.health.avg],
            itemStyle: {
              color: '#3498db'
            }
          },
          {
            name: '最小值',
            type: 'bar',
            data: [stats.pressure.min, stats.temperature.min, stats.flowRate.min, stats.health.min],
            itemStyle: {
              color: '#2ecc71'
            }
          },
          {
            name: '最大值',
            type: 'bar',
            data: [stats.pressure.max, stats.temperature.max, stats.flowRate.max, stats.health.max],
            itemStyle: {
              color: '#e74c3c'
            }
          },
          {
            name: '中位数',
            type: 'bar',
            data: [stats.pressure.median, stats.temperature.median, stats.flowRate.median, stats.health.median],
            itemStyle: {
              color: '#f39c12'
            }
          }
        ]
      }
      
      chartInstance.setOption(option)
      
      // 初始化大小监听
      initChartResizeObserver()
    }
    
    // 渲染异常检测图表
    const renderAnomalyChart = (anomalies) => {
      if (!chartContainer.value) return
      
      if (chartInstance) {
        chartInstance.dispose()
      }
      
      chartInstance = echarts.init(chartContainer.value)
      
      const timestamps = anomalies.map(item => item.timestamp)
      const pressures = anomalies.map(item => item.pressure)
      const anomaliesMark = anomalies.map(item => item.isAnomaly ? item.pressure : null)
      
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['压力', '异常点'],
          textStyle: {
            color: '#e0e0e0'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: timestamps,
          axisLabel: {
            color: '#bdc3c7',
            rotate: 45
          },
          axisLine: {
            lineStyle: {
              color: '#34495e'
            }
          }
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            color: '#bdc3c7'
          },
          axisLine: {
            lineStyle: {
              color: '#34495e'
            }
          },
          splitLine: {
            lineStyle: {
              color: '#34495e',
              type: 'dashed'
            }
          }
        },
        series: [
          {
            name: '压力',
            type: 'line',
            data: pressures,
            itemStyle: {
              color: '#3498db'
            }
          },
          {
            name: '异常点',
            type: 'scatter',
            data: anomaliesMark,
            itemStyle: {
              color: '#e74c3c'
            },
            symbolSize: 10
          }
        ]
      }
      
      chartInstance.setOption(option)
      
      // 初始化大小监听
      initChartResizeObserver()
    }
    
    // 渲染相关性图表
    const renderCorrelationChart = (correlations) => {
      if (!chartContainer.value) return
      
      if (chartInstance) {
        chartInstance.dispose()
      }
      
      chartInstance = echarts.init(chartContainer.value)
      
      const categories = ['压力-温度', '压力-流量', '压力-健康', '温度-流量', '温度-健康', '流量-健康']
      const values = [
        correlations.pressureTemperature,
        correlations.pressureFlowRate,
        correlations.pressureHealth,
        correlations.temperatureFlowRate,
        correlations.temperatureHealth,
        correlations.flowRateHealth
      ]
      
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          formatter: (params) => {
            const value = params[0].value
            return `${params[0].name}: ${value.toFixed(4)}`
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: categories,
          axisLabel: {
            color: '#bdc3c7',
            rotate: 45
          },
          axisLine: {
            lineStyle: {
              color: '#34495e'
            }
          }
        },
        yAxis: {
          type: 'value',
          min: -1,
          max: 1,
          axisLabel: {
            color: '#bdc3c7'
          },
          axisLine: {
            lineStyle: {
              color: '#34495e'
            }
          },
          splitLine: {
            lineStyle: {
              color: '#34495e',
              type: 'dashed'
            }
          }
        },
        series: [
          {
            name: '相关系数',
            type: 'bar',
            data: values,
            itemStyle: {
              color: (params) => {
                const value = params.value
                if (value > 0.7) return '#2ecc71'
                if (value > 0.3) return '#3498db'
                if (value > -0.3) return '#f39c12'
                if (value > -0.7) return '#e67e22'
                return '#e74c3c'
              }
            }
          }
        ]
      }
      
      chartInstance.setOption(option)
      
      // 初始化大小监听
      initChartResizeObserver()
    }
    
    // 下载图表图片
    const downloadChart = () => {
      if (!chartInstance) return
      
      try {
        // 使用ECharts的getDataURL方法获取图表图片数据
        const dataURL = chartInstance.getDataURL({
          type: 'png',
          pixelRatio: 2, // 提高图片清晰度
          backgroundColor: '#2c2c2c' // 设置背景色为深色
        })
        
        // 创建下载链接
        const link = document.createElement('a')
        link.href = dataURL
        link.download = `chart-${selectedModel.value}-${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.png`
        link.click()
        
        ElMessage.success('图表下载成功')
      } catch (error) {
        console.error('下载图表失败:', error)
        ElMessage.error('下载图表失败')
      }
    }
    
    // 获取历史数据
    const fetchHistoricalData = async () => {
      try {
        // 调用API获取历史数据
        const response = await getSimulationHistory()
        // 更新历史数据列表
        if (response && response.length > 0) {
          // 转换后端数据格式为前端所需格式
          stateHistory.value = response.map(item => ({
            timestamp: new Date(item.timestamp).toLocaleString(),
            pressure: item.pressure,
            temperature: item.temperature,
            flowRate: item.flowRate,
            health: item.health,
            faultStatus: item.faultStatus,
            currentAge: item.currentAge,
            corrosionRate: item.corrosionRate,
            serviceLife: item.serviceLife,
            length: item.length,
            diameter: item.diameter,
            thickness: item.thickness
          }))
          ElMessage.success('获取历史数据成功')
        }
      } catch (error) {
        console.error('获取历史数据失败:', error)
        ElMessage.error('获取历史数据失败')
      }
    }
    
    // 生命周期钩子
    onMounted(() => {
      initializeThreeScene()
      fetchPipeParameters()
      fetchHistoricalData()
    })
    
    onUnmounted(() => {
      if (simulationTimer) {
        clearInterval(simulationTimer)
      }
      if (animationId) {
        cancelAnimationFrame(animationId)
      }
      if (renderer) {
        renderer.dispose()
      }
      if (chartInstance) {
        chartInstance.dispose()
      }
      // 清理ResizeObserver
      if (resizeObserver) {
        resizeObserver.disconnect()
      }
      window.removeEventListener('resize', () => {})
      document.removeEventListener('mousemove', resize)
      document.removeEventListener('mouseup', stopResize)
      // 清理结果区域拉伸事件
      document.removeEventListener('mousemove', resizeResult)
      document.removeEventListener('mouseup', stopResizeResult)
    })
    
    return {
      pipeParams,
      simulationParams,
      isSimulating,
      statusText,
      statusColor,
      activeParamsTab,
      currentState,
      stateHistory,
      threeContainer,
      initializeParameters,
      runSimulation,
      stopSimulation,
      checkHealth,
      predictLifetime,
      simulateFault,
      exportData,
      clearHistory,
      getHealthColor,
      // 数据处理区域相关
      isProcessingCollapsed,
      selectedModel,
      droppedData,
      processingResult,
      processingContent,
      resultContent,
      resultSection,
      resultSectionStyle,
      chartContainer,
      toggleProcessingPanel,
      handleRowDragStart,
      handleDrop,
      removeDroppedData,
      startResize,
      executeProcessing,
      downloadChart,
      // 结果区域拉伸相关
      startResizeResult,
      // 历史数据选择相关
      isHistoryDrawerOpen,
      toggleHistoryDrawer
    }
  }
}
</script>

<style scoped>
.pipe-digital-twin-full {
  width: 100%;
  height: calc(100vh - 60px);
  display: flex;
  flex-direction: column;
  color: #e0e0e0;
  padding: 0;
  gap: 0;
  box-sizing: border-box;
}

.pipe-digital-twin-full h1 {
  color: #3498db;
  font-size: 1.2rem;
  margin: 0 0 8px 0;
  letter-spacing: 1px;
  font-weight: bold;
  text-align: center;
}

/* 工具栏样式 */
.toolbar-card {
  margin-bottom: 0;
}

.toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 0;
  padding: 4px 8px;
  background-color: #2c2c2c;
  border: 1px solid #34495e;
  border-radius: 3px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
  align-items: center;
  justify-content: flex-start;
}

/* 按钮分隔符 */
.toolbar :deep(.el-button) {
  position: relative;
  margin-right: 0;
  margin-bottom: 5px;
  padding-left: 10px;
  padding-right: 10px;
}

/* 为每个按钮添加分隔符，除了最后一个 */
.toolbar :deep(.el-button:not(:last-child))::after {
  content: '|';
  position: absolute;
  right: -5px;
  top: 50%;
  transform: translateY(-50%);
  color: #3498db;
  margin: 0 5px;
}

/* 调整第一个按钮的左边距 */
.toolbar :deep(.el-button:first-child) {
  margin-left: 0;
}

/* 主内容区布局 */
.main-content {
  display: flex;
  flex-direction: row;
  gap: 0;
  flex: 1;
  min-height: 0; /* 解决flex子元素溢出问题 */
  box-sizing: border-box;
}

/* 左侧参数面板 */
.params-panel {
  width: 280px;
  min-width: 280px;
  background-color: #2c2c2c;
  border: 1px solid #34495e;
  border-radius: 3px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
  height: calc(100vh - 140px);
  overflow-y: auto;
  box-sizing: border-box;
  flex-shrink: 0; /* 防止面板被压缩 */
}

/* 右侧可视化区域 */
.visualization-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0;
  min-width: 0; /* 解决flex子元素溢出问题 */
  box-sizing: border-box;
  min-height: 300px;
  height: auto;
}

/* 3D可视化卡片 */
.visualization-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 400px;
}

.visualization-card .el-card__body {
  flex: 1;
  padding: 0;
  display: flex;
  flex-direction: column;
}

.pipe-visualization {
  width: 100%;
  flex: 1;
  background-color: #0a0a0a;
  overflow: hidden;
  min-height: 300px;
}

#three-container {
  width: 100%;
  height: 100%;
  min-height: 400px;
}

/* 状态栏 */
.status-bar {
  background-color: #2c2c2c;
  border: 1px solid #34495e;
  border-radius: 3px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
}

.status-info {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
  justify-content: center;
  padding: 10px;
  font-size: 11px;
}

.status-info span {
  color: #e0e0e0;
  font-size: 14px;
}

/* 历史数据卡片 */
.history-data-card {
  background-color: #2c2c2c;
  border: 1px solid #34495e;
  border-radius: 3px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
  flex: 0 1 auto;
  min-height: 120px;
}

.history-data-card .el-card__body {
  padding: 10px;
}

/* 历史数据摘要卡片 */
.history-summary-card {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
  background-color: rgba(52, 73, 94, 0.3);
  padding: 10px;
  border-radius: 3px;
  border: 1px solid #34495e;
}

.summary-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.summary-text {
  font-weight: bold;
  color: #3498db;
  font-size: 12px;
}

.selected-info {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  width: 100%;
  font-size: 11px;
  color: #bdc3c7;
}

/* 固定定位历史数据抽屉 */
.history-drawer-fixed {
  position: fixed;
  bottom: 0;
  left: 282px;
  right: 350px;
  background-color: #2c2c2c;
  border: 1px solid #34495e;
  border-radius: 3px 3px 0 0;
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.3);
  max-height: 600px;
  overflow: hidden;
  z-index: 1000;
  transition: all 0.3s ease;
  transform: translateY(100%);
  opacity: 0;
  visibility: hidden;
}

/* 抽屉显示状态 */
.history-drawer-fixed.history-drawer-visible {
  transform: translateY(0);
  opacity: 1;
  visibility: visible;
}

/* 抽屉收起动画 */
.history-drawer-fixed {
  transition: all 0.3s ease;
}

/* 抽屉内容 */
.history-drawer-content {
  display: flex;
  flex-direction: column;
  height: 100%;
  max-height: 600px;
}

/* 抽屉头部 */
.history-drawer-header {
  background-color: #34495e;
  color: #3498db;
  padding: 10px 15px;
  border-bottom: 1px solid #7f8c8d;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  font-weight: bold;
}

/* 抽屉内容 */
.history-drawer-body {
  padding: 15px;
  display: flex;
  flex-direction: column;
  gap: 15px;
  height: calc(100% - 50px);
  overflow: hidden;
}

.history-drawer-content {
  display: flex;
  flex-direction: column;
  gap: 15px;
  height: 100%;
  overflow: hidden;
}

/* 抽屉头部操作 */
.drawer-header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding-bottom: 10px;
  border-bottom: 1px solid #34495e;
}

.history-count {
  font-weight: bold;
  color: #3498db;
  font-size: 12px;
}

/* 历史数据选择容器 */
.history-select-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
}

.history-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.history-title h4 {
  margin: 0;
  font-size: 12px;
  color: #3498db;
  font-weight: bold;
}

/* 历史数据列表 */
.history-data-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding-right: 5px;
  max-height: calc(100% - 60px);
}

/* 滚动条样式 */
.history-data-list::-webkit-scrollbar {
  width: 8px;
}

.history-data-list::-webkit-scrollbar-track {
  background: #2c2c2c;
  border-radius: 4px;
}

.history-data-list::-webkit-scrollbar-thumb {
  background: #34495e;
  border-radius: 4px;
}

.history-data-list::-webkit-scrollbar-thumb:hover {
  background: #3498db;
}

/* 历史数据项 */
.history-data-item {
  background-color: #2c2c2c;
  border: 1px solid #34495e;
  border-radius: 3px;
  padding: 10px;
  cursor: grab;
  transition: all 0.2s ease;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.history-data-item:hover {
  background-color: #34495e;
  border-color: #3498db;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.history-data-item:active {
  cursor: grabbing;
}

/* 历史数据项头部 */
.history-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.item-timestamp {
  font-weight: bold;
  color: #3498db;
  font-size: 13px;
}

.item-checkbox {
  margin: 0;
}

/* 历史数据项详情 */
.history-item-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: 12px;
  color: #e0e0e0;
}

.detail-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 6px;
  background-color: rgba(52, 73, 94, 0.5);
  padding: 4px 8px;
  border-radius: 3px;
  flex-shrink: 0;
}

.detail-item .el-progress {
  width: 70px;
}

/* 空状态 */
.history-empty {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  background-color: rgba(52, 73, 94, 0.3);
  border: 1px dashed #34495e;
  border-radius: 3px;
  color: #bdc3c7;
  font-size: 13px;
  text-align: center;
}

.history-empty :deep(.el-empty__description) {
  color: #bdc3c7 !important;
}

/* 选择框样式 */
:deep(.el-select) {
  width: 100%;
}

:deep(.el-select__wrapper) {
  background-color: #2c2c2c !important;
  border: 1px solid #34495e !important;
  border-radius: 3px !important;
}

:deep(.el-select__input) {
  color: #e0e0e0 !important;
  font-size: 11px !important;
}

:deep(.el-select__placeholder) {
  color: #bdc3c7 !important;
  font-size: 11px !important;
}

:deep(.el-select-dropdown) {
  background-color: #2c2c2c !important;
  border: 1px solid #34495e !important;
  border-radius: 3px !important;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3) !important;
}

:deep(.el-select-dropdown__item) {
  color: #e0e0e0 !important;
  font-size: 11px !important;
  padding: 8px 12px !important;
}

:deep(.el-select-dropdown__item:hover) {
  background-color: #34495e !important;
}

:deep(.el-select-dropdown__item.selected) {
  background-color: rgba(52, 152, 219, 0.2) !important;
}

:deep(.el-tag) {
  margin-right: 5px !important;
  margin-bottom: 5px !important;
  font-size: 10px !important;
}

/* 确保卡片内容不会被压缩 */
.el-card {
  display: flex;
  flex-direction: column;
}

.el-card__header {
  padding: 12px 15px;
  flex-shrink: 0;
}

.el-card__body {
  flex: 1;
  padding: 15px;
  overflow: hidden;
  box-sizing: border-box;
}

/* 折叠面板样式调整 */
:deep(.el-collapse-item__content) {
  padding: 15px;
  max-height: none;
  overflow: visible;
}

/* 表单样式调整 */
:deep(.el-form) {
  margin: 0;
}

:deep(.el-form-item) {
  margin-bottom: 15px;
}

/* 按钮样式调整 */
:deep(.el-button) {
  margin-right: 5px;
  margin-bottom: 5px;
}

/* 确保内容区域滚动正常 */
.visualization-section > .el-card {
  display: flex;
  flex-direction: column;
}

.visualization-section > .el-card .el-card__body {
  flex: 1;
  display: flex;
  flex-direction: column;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .main-content {
    flex-direction: column;
  }
  
  .params-panel {
    width: 100%;
    min-width: auto;
  }
}

/* 工业风格卡片 */
:deep(.el-card) {
  background-color: #2c2c2c !important;
  border: 1px solid #34495e !important;
  border-radius: 3px !important;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3) !important;
  color: #e0e0e0 !important;
  margin-bottom: 20px !important;
}

:deep(.el-card__header) {
  background-color: #34495e !important;
  color: #3498db !important;
  font-weight: bold !important;
  border-bottom: 1px solid #7f8c8d !important;
  padding: 6px 8px !important;
  font-size: 12px !important;
}

:deep(.el-card__body) {
  background-color: #2c2c2c !important;
  padding: 6px 8px !important;
}

/* 工业风格表单 */
:deep(.el-form) {
  margin-bottom: 0;
}

:deep(.el-form-item) {
  margin-bottom: 4px;
}

:deep(.el-form-item__label) {
  color: #3498db !important;
  font-weight: bold !important;
  font-size: 11px !important;
  padding: 0 8px 0 0 !important;
}

:deep(.el-input) {
  background-color: #2c2c2c !important;
  border: 1px solid #34495e !important;
  border-radius: 3px !important;
}

:deep(.el-input__wrapper) {
  background-color: #2c2c2c !important;
  border-radius: 3px !important;
}

:deep(.el-input__inner) {
  background-color: #2c2c2c !important;
  color: #e0e0e0 !important;
  border: none !important;
  border-radius: 3px !important;
  height: 24px !important;
  font-size: 11px !important;
}

:deep(.el-input__inner:focus) {
  background-color: #34495e !important;
  color: #e0e0e0 !important;
  box-shadow: 0 0 0 1px #3498db inset !important;
}

:deep(.el-input-number) {
  background-color: #2c2c2c !important;
  border: 1px solid #34495e !important;
  border-radius: 3px !important;
}

:deep(.el-select) {
  background-color: #2c2c2c !important;
  border: 1px solid #34495e !important;
  border-radius: 3px !important;
}

:deep(.el-select__wrapper) {
  background-color: #2c2c2c !important;
  border-radius: 3px !important;
  height: 24px !important;
}

:deep(.el-select__input) {
  color: #e0e0e0 !important;
}

/* 工业风格按钮 */
:deep(.el-button) {
  background-color: #3498db !important;
  border-color: #3498db !important;
  color: #ffffff !important;
  border-radius: 3px !important;
  font-weight: bold !important;
  height: 24px !important;
  padding: 0 8px !important;
  font-size: 11px !important;
  margin-right: 4px !important;
  margin-bottom: 4px !important;
}

:deep(.el-button:hover) {
  background-color: #2980b9 !important;
  border-color: #2980b9 !important;
}

:deep(.el-button:active) {
  background-color: #1f618d !important;
  border-color: #1f618d !important;
}

/* 工业风格标签 */
:deep(.el-tag) {
  border-radius: 2px !important;
  font-weight: bold !important;
}

:deep(.el-tag--success) {
  background-color: #27ae60 !important;
  border-color: #27ae60 !important;
  color: #ffffff !important;
}

:deep(.el-tag--warning) {
  background-color: #f39c12 !important;
  border-color: #f39c12 !important;
  color: #ffffff !important;
}

:deep(.el-tag--danger) {
  background-color: #e74c3c !important;
  border-color: #e74c3c !important;
  color: #ffffff !important;
}

:deep(.el-collapse) {
  background-color: #2c2c2c !important;
  border: none !important;
}

:deep(.el-collapse-item) {
  border: 1px solid #34495e !important;
  border-radius: 3px !important;
  margin-bottom: 4px !important;
  background-color: #2c2c2c !important;
}

:deep(.el-collapse-item__header) {
  background-color: #34495e !important;
  color: #3498db !important;
  font-weight: bold !important;
  border-bottom: 1px solid #7f8c8d !important;
  padding: 6px 8px !important;
  font-size: 11px !important;
}

:deep(.el-collapse-item__content) {
  background-color: #2c2c2c !important;
  padding: 6px 8px !important;
}

/* 工业风格表格 */
:deep(.el-table) {
  background-color: #2c2c2c !important;
  border: 1px solid #34495e !important;
  border-radius: 3px !important;
  color: #e0e0e0 !important;
  font-size: 11px !important;
}

:deep(.el-table__header-wrapper) {
  background-color: #34495e !important;
}

:deep(.el-table__header th) {
  background-color: #34495e !important;
  color: #3498db !important;
  font-weight: bold !important;
  border-bottom: 1px solid #7f8c8d !important;
  padding: 4px 6px !important;
  font-size: 11px !important;
}

:deep(.el-table__body tr) {
  background-color: #2c2c2c !important;
}

:deep(.el-table__body tr:hover) {
  background-color: #34495e !important;
}

:deep(.el-table__body td) {
  border-bottom: 1px solid #34495e !important;
  color: #e0e0e0 !important;
  padding: 4px 6px !important;
  font-size: 11px !important;
}

/* 工业风格进度条 */
:deep(.el-progress__bar) {
  background-color: #3498db !important;
}

:deep(.el-progress__text) {
  color: #e0e0e0 !important;
}

/* 工业风格开关 */
:deep(.el-switch) {
  background-color: #34495e !important;
}

:deep(.el-switch.is-checked) {
  background-color: #3498db !important;
}

/* 数据处理区域样式 */
.data-processing-section {
  width: 350px;
  background-color: #2c2c2c;
  border: 1px solid #34495e;
  border-radius: 3px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;
  height: calc(100vh - 140px);
  box-sizing: border-box;
  transition: all 0.3s ease;
  flex-shrink: 0;
  overflow: hidden;
}

.data-processing-section.collapsed {
  width: 50px;
}

.processing-header {
  background-color: #34495e;
  color: #3498db;
  font-weight: bold;
  padding: 10px 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #7f8c8d;
  box-sizing: border-box;
}

.processing-header h3 {
  margin: 0;
  font-size: 14px;
}

.processing-controls {
  display: flex;
  gap: 5px;
}

.processing-content {
  flex: 1;
  padding: 15px;
  overflow-y: auto;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.data-processing-section.collapsed .processing-content {
  display: none;
}

/* 模型选择样式 */
.model-selection {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.model-selection h4 {
  margin: 0;
  font-size: 12px;
  color: #3498db;
  font-weight: bold;
}

/* 拖放区域样式 */
.drop-zone {
  border: 2px dashed #34495e;
  border-radius: 3px;
  padding: 20px;
  text-align: center;
  background-color: rgba(52, 73, 94, 0.2);
  transition: all 0.3s ease;
  min-height: 100px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.drop-zone.has-data {
  border-color: #3498db;
  background-color: rgba(52, 152, 219, 0.1);
}

.drop-zone-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  color: #bdc3c7;
}

.drop-icon {
  font-size: 24px;
  color: #3498db;
}

.drop-zone-empty p {
  margin: 0;
  font-size: 12px;
}

.dropped-data-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
}

.dropped-data-list h4 {
  margin: 0;
  font-size: 12px;
  color: #3498db;
  font-weight: bold;
}

/* 执行按钮区域 */
.execute-section {
  display: flex;
  justify-content: center;
  margin: 10px 0;
}

/* 结果展示区域 */
.result-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
  min-height: 300px; /* 增加最小高度 */
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.result-header h4 {
  margin: 0;
  font-size: 12px;
  color: #3498db;
  font-weight: bold;
}

.download-button {
  height: 24px;
  padding: 0 8px;
  font-size: 11px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.result-content {
  flex: 1;
  background-color: rgba(0, 0, 0, 0.2);
  border: 1px solid #34495e;
  border-radius: 3px;
  padding: 10px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 280px; /* 增加最小高度 */
}

.result-empty {
  color: #bdc3c7;
  font-size: 12px;
  text-align: center;
}

.result-chart {
  width: 100%;
  height: 100%;
  min-height: 280px; /* 增加最小高度 */
  display: flex;
  align-items: center;
  justify-content: center;
}

.result-chart > div {
  width: 100% !important;
  height: 100% !important;
  min-height: 280px; /* 增加最小高度 */
}

/* 结果区域拉伸手柄样式 */
.resize-handles {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 10;
}

.result-section .resize-handle {
  width: 8px;
  height: 8px;
  background-color: #3498db;
  border: 1px solid #2c2c2c;
  pointer-events: auto;
  position: absolute;
  transition: all 0.3s ease;
  z-index: 11;
  border-radius: 50%;
}

.result-section .resize-handle:hover {
  background-color: #2980b9;
  transform: scale(1.5);
}

/* 各个方向的手柄位置 */
.result-section .resize-handle-n {
  top: -4px;
  left: 50%;
  transform: translateX(-50%);
  cursor: ns-resize;
}

.result-section .resize-handle-e {
  right: -4px;
  top: 50%;
  transform: translateY(-50%);
  cursor: ew-resize;
}

.result-section .resize-handle-s {
  bottom: -4px;
  left: 50%;
  transform: translateX(-50%);
  cursor: ns-resize;
}

.result-section .resize-handle-w {
  left: -4px;
  top: 50%;
  transform: translateY(-50%);
  cursor: ew-resize;
}

.result-section .resize-handle-ne {
  top: -4px;
  right: -4px;
  cursor: nesw-resize;
}

.result-section .resize-handle-se {
  bottom: -4px;
  right: -4px;
  cursor: nwse-resize;
}

.result-section .resize-handle-sw {
  bottom: -4px;
  left: -4px;
  cursor: nesw-resize;
}

.result-section .resize-handle-nw {
  top: -4px;
  left: -4px;
  cursor: nwse-resize;
}

/* 数据处理区域拉伸手柄样式 */
.data-processing-section .resize-handle {
  width: 5px;
  background-color: #34495e;
  cursor: ew-resize;
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  transition: background-color 0.3s ease;
  z-index: 10;
}

.data-processing-section .resize-handle:hover {
  background-color: #3498db;
}

.data-processing-section {
  position: relative;
}

/* 工业风格单选按钮 */
:deep(.el-radio-group) {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

:deep(.el-radio-button) {
  background-color: #34495e !important;
  border-color: #3498db !important;
  color: #e0e0e0 !important;
}

:deep(.el-radio-button__inner) {
  background-color: #34495e !important;
  border-color: #3498db !important;
  color: #e0e0e0 !important;
  font-size: 11px !important;
  padding: 4px 8px !important;
}

:deep(.el-radio-button__orig-radio:checked + .el-radio-button__inner) {
  background-color: #3498db !important;
  border-color: #3498db !important;
  color: #ffffff !important;
}

:deep(.el-radio-button:first-child .el-radio-button__inner) {
  border-radius: 3px 0 0 3px !important;
}

:deep(.el-radio-button:last-child .el-radio-button__inner) {
  border-radius: 0 3px 3px 0 !important;
}

/* 工业风格标签 */
:deep(.el-tag) {
  background-color: #34495e !important;
  border-color: #3498db !important;
  color: #e0e0e0 !important;
  margin-right: 5px !important;
  margin-bottom: 5px !important;
  font-size: 11px !important;
}

:deep(.el-tag__close) {
  color: #e0e0e0 !important;
}

:deep(.el-tag__close:hover) {
  color: #ffffff !important;
  background-color: rgba(255, 255, 255, 0.1) !important;
}

/* 表格行拖动样式 */
:deep(.el-table__row) {
  cursor: grab;
  transition: all 0.2s ease;
}

:deep(.el-table__row:hover) {
  background-color: rgba(52, 152, 219, 0.1) !important;
}

:deep(.el-table__row:active) {
  cursor: grabbing;
}
</style>