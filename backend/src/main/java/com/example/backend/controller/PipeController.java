package com.example.backend.controller;

import com.example.backend.entity.PipeParameters;
import com.example.backend.model.PipeDataResponse;
import com.example.backend.service.PipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pipe")
public class PipeController {

    @Autowired
    private PipeService pipeService;

    // 获取管道概览数据
    @GetMapping("/data")
    public ResponseEntity<PipeDataResponse> getPipeData() {
        PipeDataResponse response = pipeService.getPipeData();
        return ResponseEntity.ok(response);
    }
    
    // 初始化管道参数
    @PostMapping("/initialize-parameters")
    public ResponseEntity<PipeParameters> initializeParameters() {
        PipeParameters params = pipeService.initializeParameters();
        return ResponseEntity.ok(params);
    }
    
    // 获取管道参数
    @GetMapping("/parameters")
    public ResponseEntity<PipeParameters> getParameters() {
        PipeParameters params = pipeService.getPipeParameters();
        return ResponseEntity.ok(params);
    }
    
    // 更新管道参数
    @PutMapping("/parameters")
    public ResponseEntity<PipeParameters> updateParameters(@RequestBody PipeParameters parameters) {
        PipeParameters updatedParams = pipeService.updateParameters(parameters);
        return ResponseEntity.ok(updatedParams);
    }
    
    // 运行模拟
    @PostMapping("/run-simulation")
    public ResponseEntity<PipeParameters> runSimulation() {
        PipeParameters params = pipeService.runSimulation();
        return ResponseEntity.ok(params);
    }
    
    // 健康检查
    @GetMapping("/health-check")
    public ResponseEntity<String> checkHealth() {
        String result = pipeService.checkHealth();
        return ResponseEntity.ok(result);
    }
    
    // 寿命预测
    @GetMapping("/predict-lifetime")
    public ResponseEntity<String> predictLifetime() {
        String result = pipeService.predictLifetime();
        return ResponseEntity.ok(result);
    }
    
    // 故障模拟
    @PostMapping("/simulate-fault")
    public ResponseEntity<PipeParameters> simulateFault() {
        PipeParameters params = pipeService.simulateFault();
        return ResponseEntity.ok(params);
    }
    
    // 获取历史数据
    @GetMapping("/historical")
    public ResponseEntity<List<java.util.Map<String, Object>>> getHistoricalData(
            @RequestParam(required = false) Long monitoringPointId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        
        try {
            // 转换时间参数
            java.util.Date startDate = null;
            java.util.Date endDate = null;
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            if (startTime != null && !startTime.isEmpty()) {
                startDate = sdf.parse(startTime);
            }
            
            if (endTime != null && !endTime.isEmpty()) {
                endDate = sdf.parse(endTime);
            }
            
            // 调用服务层获取历史数据
            List<java.util.Map<String, Object>> historicalData = pipeService.getHistoricalData(monitoringPointId, startDate, endDate);
            
            return ResponseEntity.ok(historicalData);
        } catch (java.text.ParseException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
