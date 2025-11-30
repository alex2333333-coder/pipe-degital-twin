package com.example.backend.service;

import com.example.backend.entity.MonitoringData;
import com.example.backend.entity.MonitoringPoint;
import com.example.backend.entity.Pipe;
import com.example.backend.entity.PipeParameters;
import com.example.backend.model.PipeDataResponse;
import com.example.backend.repository.MonitoringDataRepository;
import com.example.backend.repository.MonitoringPointRepository;
import com.example.backend.repository.PipeParametersRepository;
import com.example.backend.repository.PipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PipeService {

    @Autowired
    private PipeRepository pipeRepository;

    @Autowired
    private MonitoringPointRepository monitoringPointRepository;

    @Autowired
    private MonitoringDataRepository monitoringDataRepository;
    
    @Autowired
    private PipeParametersRepository pipeParametersRepository;

    // 获取管道概览数据
    public PipeDataResponse getPipeData() {
        // 计算总长度
        Double totalLength = pipeRepository.findAll().stream()
                .mapToDouble(Pipe::getLength)
                .sum();

        // 获取监测点数量
        Integer monitoringPoints = (int) monitoringPointRepository.count();

        // 模拟当前压力和流量数据
        Double currentPressure = 2.35;
        Double currentFlow = 1250.8;

        // 模拟监测数据列表
        List<PipeDataResponse.MonitoringDataDto> monitoringDataList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());

        // 添加模拟数据
        monitoringDataList.add(new PipeDataResponse.MonitoringDataDto(
                "MP001",
                "监测点1",
                2.35,
                1250.8,
                25.6,
                "正常",
                currentTime
        ));

        monitoringDataList.add(new PipeDataResponse.MonitoringDataDto(
                "MP002",
                "监测点2",
                2.32,
                1245.6,
                25.4,
                "正常",
                currentTime
        ));

        monitoringDataList.add(new PipeDataResponse.MonitoringDataDto(
                "MP003",
                "监测点3",
                2.28,
                1238.9,
                25.2,
                "正常",
                currentTime
        ));

        monitoringDataList.add(new PipeDataResponse.MonitoringDataDto(
                "MP004",
                "监测点4",
                2.30,
                1242.3,
                25.3,
                "正常",
                currentTime
        ));

        monitoringDataList.add(new PipeDataResponse.MonitoringDataDto(
                "MP005",
                "监测点5",
                2.33,
                1247.5,
                25.5,
                "正常",
                currentTime
        ));

        return new PipeDataResponse(totalLength, monitoringPoints, currentPressure, currentFlow, monitoringDataList);
    }
    
    // 初始化管道参数
    public PipeParameters initializeParameters() {
        PipeParameters params = new PipeParameters();
        
        // 设置默认参数
        params.setLength(100.0);
        params.setDiameter(0.5);
        params.setThickness(0.01);
        params.setMaterialDensity(7850.0);
        params.setElasticityModulus(2.1e11);
        params.setMaterial("低碳钢");
        
        params.setInitialPressure(5.0);
        params.setInitialTemperature(25.0);
        params.setFlowRate(100.0);
        params.setFluidViscosity(0.001);
        params.setCorrosionRate(0.005);
        
        params.setServiceLife(20);
        params.setCurrentAge(0);
        
        params.setTimeStep(10);
        params.setSimulationSpeed(2);
        params.setRealTime(true);
        
        params.setCurrentPressure(params.getInitialPressure());
        params.setCurrentTemperature(params.getInitialTemperature());
        params.setHealthStatus(100.0);
        params.setFaultStatus("正常");
        
        return pipeParametersRepository.save(params);
    }
    
    // 获取管道参数
    public PipeParameters getPipeParameters() {
        return pipeParametersRepository.findById(1L)
                .orElse(initializeParameters());
    }
    
    // 更新管道参数
    public PipeParameters updateParameters(PipeParameters parameters) {
        return pipeParametersRepository.save(parameters);
    }
    
    // 运行模拟
    public PipeParameters runSimulation() {
        PipeParameters params = getPipeParameters();
        
        // 增加运行时间
        params.setCurrentAge(params.getCurrentAge() + params.getTimeStep());
        
        // 更新健康状态
        double health = Math.max(0, 100 - (params.getCurrentAge() / (double) params.getServiceLife()) * 100);
        params.setHealthStatus(health);
        
        // 更新故障状态
        if (health > 80) {
            params.setFaultStatus("正常");
        } else if (health > 60) {
            params.setFaultStatus("轻微异常");
        } else if (health > 40) {
            params.setFaultStatus("警告");
        } else if (health > 20) {
            params.setFaultStatus("严重警告");
        } else {
            params.setFaultStatus("故障");
        }
        
        // 模拟压力和温度变化
        double pressureVariation = (Math.random() - 0.5) * 0.1;
        double temperatureVariation = (Math.random() - 0.5) * 2;
        double flowVariation = (Math.random() - 0.5) * 5;
        
        params.setCurrentPressure(Math.max(0.1, Math.min(10.0, params.getCurrentPressure() + pressureVariation)));
        params.setCurrentTemperature(Math.max(0, Math.min(100, params.getCurrentTemperature() + temperatureVariation)));
        params.setFlowRate(Math.max(0, params.getFlowRate() + flowVariation));
        
        // 保存管道参数
        PipeParameters savedParams = pipeParametersRepository.save(params);
        
        // 保存监测数据到数据库
        if (!monitoringPointRepository.findAll().isEmpty()) {
            // 获取第一个监测点作为默认监测点
            MonitoringPoint monitoringPoint = monitoringPointRepository.findAll().get(0);
            
            // 创建监测数据
            MonitoringData monitoringData = new MonitoringData();
            monitoringData.setMonitoringPoint(monitoringPoint);
            monitoringData.setPressure(savedParams.getCurrentPressure());
            monitoringData.setTemperature(savedParams.getCurrentTemperature());
            monitoringData.setFlow(savedParams.getFlowRate());
            monitoringData.setVibration(0.1); // 模拟振动数据
            monitoringData.setAlarmStatus(savedParams.getFaultStatus());
            monitoringData.setDataTime(new Date());
            monitoringData.setCreatedAt(new Date());
            
            // 保存到数据库
            monitoringDataRepository.save(monitoringData);
        }
        
        return savedParams;
    }
    
    // 健康检查
    public String checkHealth() {
        PipeParameters params = getPipeParameters();
        double health = params.getHealthStatus();
        
        if (health > 80) {
            return "健康状态: 良好";
        } else if (health > 60) {
            return "健康状态: 一般";
        } else if (health > 40) {
            return "健康状态: 警告";
        } else if (health > 20) {
            return "健康状态: 危险";
        } else {
            return "健康状态: 严重故障";
        }
    }
    
    // 寿命预测
    public String predictLifetime() {
        PipeParameters params = getPipeParameters();
        int remainingLife = Math.max(0, params.getServiceLife() - params.getCurrentAge());
        return "预计剩余寿命: " + remainingLife + " 年";
    }
    
    // 故障模拟
    public PipeParameters simulateFault() {
        PipeParameters params = getPipeParameters();
        
        // 模拟故障，增加运行年限
        params.setCurrentAge(params.getCurrentAge() + (int)(params.getServiceLife() * 0.3));
        
        // 更新健康状态
        double health = Math.max(0, 100 - (params.getCurrentAge() / (double) params.getServiceLife()) * 100);
        params.setHealthStatus(health);
        
        // 更新故障状态
        if (health > 80) {
            params.setFaultStatus("正常");
        } else if (health > 60) {
            params.setFaultStatus("轻微异常");
        } else if (health > 40) {
            params.setFaultStatus("警告");
        } else if (health > 20) {
            params.setFaultStatus("严重警告");
        } else {
            params.setFaultStatus("故障");
        }
        
        return pipeParametersRepository.save(params);
    }
    
    // 获取历史数据
    public List<java.util.Map<String, Object>> getHistoricalData(Long monitoringPointId, java.util.Date startTime, java.util.Date endTime) {
        List<MonitoringData> monitoringDataList;
        
        // 根据参数查询历史数据
        if (monitoringPointId != null && startTime != null && endTime != null) {
            // 按监测点和时间范围查询
            monitoringDataList = monitoringDataRepository.findByMonitoringPointIdAndDataTimeBetween(monitoringPointId, startTime, endTime);
        } else if (monitoringPointId != null) {
            // 仅按监测点查询
            monitoringDataList = monitoringDataRepository.findByMonitoringPointId(monitoringPointId);
        } else if (startTime != null && endTime != null) {
            // 仅按时间范围查询
            monitoringDataList = monitoringDataRepository.findByDataTimeBetween(startTime, endTime);
        } else {
            // 查询所有历史数据
            monitoringDataList = monitoringDataRepository.findAll();
        }
        
        // 获取管道参数
        PipeParameters pipeParams = getPipeParameters();
        
        // 转换数据格式
        List<java.util.Map<String, Object>> result = new ArrayList<>();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (MonitoringData data : monitoringDataList) {
            java.util.Map<String, Object> item = new java.util.HashMap<>();
            
            // 基本监测数据
            item.put("timestamp", sdf.format(data.getDataTime()));
            item.put("pressure", data.getPressure());
            item.put("temperature", data.getTemperature());
            item.put("flowRate", data.getFlow());
            
            // 管道参数
            item.put("length", pipeParams.getLength());
            item.put("diameter", pipeParams.getDiameter());
            item.put("thickness", pipeParams.getThickness());
            item.put("corrosionRate", pipeParams.getCorrosionRate());
            item.put("serviceLife", pipeParams.getServiceLife());
            
            // 计算健康状态和故障状态
            double health = Math.max(0, 100 - (pipeParams.getCurrentAge() / (double) pipeParams.getServiceLife()) * 100);
            String faultStatus;
            if (health > 80) {
                faultStatus = "正常";
            } else if (health > 60) {
                faultStatus = "轻微异常";
            } else if (health > 40) {
                faultStatus = "警告";
            } else if (health > 20) {
                faultStatus = "严重警告";
            } else {
                faultStatus = "故障";
            }
            
            item.put("health", health);
            item.put("faultStatus", faultStatus);
            item.put("currentAge", pipeParams.getCurrentAge());
            
            result.add(item);
        }
        
        return result;
    }
}
