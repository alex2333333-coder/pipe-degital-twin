package com.example.backend.entity;

import javax.persistence.*;

@Entity
@Table(name = "pipe_parameters")
public class PipeParameters {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 基本参数
    private Double length; // 管道长度（米）
    private Double diameter; // 管道直径（米）
    private Double thickness; // 管道壁厚（米）
    private Double materialDensity; // 材料密度（kg/m³）
    private Double elasticityModulus; // 弹性模量（Pa）
    private String material; // 材料类型
    
    // 运行参数
    private Double initialPressure; // 初始压力（MPa）
    private Double initialTemperature; // 初始温度（°C）
    private Double flowRate; // 流量（m³/h）
    private Double fluidViscosity; // 介质粘度（Pa·s）
    private Double corrosionRate; // 腐蚀速率（mm/年）
    
    // 寿命参数
    private Integer serviceLife; // 设计寿命（年）
    private Integer currentAge; // 当前运行年限（年）
    
    // 模拟参数
    private Integer timeStep; // 模拟时间步长（年）
    private Integer simulationSpeed; // 模拟速度
    private Boolean realTime; // 是否实时更新
    
    // 状态参数
    private Double currentPressure; // 当前压力（MPa）
    private Double currentTemperature; // 当前温度（°C）
    private Double healthStatus; // 健康状态（%）
    private String faultStatus; // 故障状态
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Double getLength() {
        return length;
    }
    
    public void setLength(Double length) {
        this.length = length;
    }
    
    public Double getDiameter() {
        return diameter;
    }
    
    public void setDiameter(Double diameter) {
        this.diameter = diameter;
    }
    
    public Double getThickness() {
        return thickness;
    }
    
    public void setThickness(Double thickness) {
        this.thickness = thickness;
    }
    
    public Double getMaterialDensity() {
        return materialDensity;
    }
    
    public void setMaterialDensity(Double materialDensity) {
        this.materialDensity = materialDensity;
    }
    
    public Double getElasticityModulus() {
        return elasticityModulus;
    }
    
    public void setElasticityModulus(Double elasticityModulus) {
        this.elasticityModulus = elasticityModulus;
    }
    
    public String getMaterial() {
        return material;
    }
    
    public void setMaterial(String material) {
        this.material = material;
    }
    
    public Double getInitialPressure() {
        return initialPressure;
    }
    
    public void setInitialPressure(Double initialPressure) {
        this.initialPressure = initialPressure;
    }
    
    public Double getInitialTemperature() {
        return initialTemperature;
    }
    
    public void setInitialTemperature(Double initialTemperature) {
        this.initialTemperature = initialTemperature;
    }
    
    public Double getFlowRate() {
        return flowRate;
    }
    
    public void setFlowRate(Double flowRate) {
        this.flowRate = flowRate;
    }
    
    public Double getFluidViscosity() {
        return fluidViscosity;
    }
    
    public void setFluidViscosity(Double fluidViscosity) {
        this.fluidViscosity = fluidViscosity;
    }
    
    public Double getCorrosionRate() {
        return corrosionRate;
    }
    
    public void setCorrosionRate(Double corrosionRate) {
        this.corrosionRate = corrosionRate;
    }
    
    public Integer getServiceLife() {
        return serviceLife;
    }
    
    public void setServiceLife(Integer serviceLife) {
        this.serviceLife = serviceLife;
    }
    
    public Integer getCurrentAge() {
        return currentAge;
    }
    
    public void setCurrentAge(Integer currentAge) {
        this.currentAge = currentAge;
    }
    
    public Integer getTimeStep() {
        return timeStep;
    }
    
    public void setTimeStep(Integer timeStep) {
        this.timeStep = timeStep;
    }
    
    public Integer getSimulationSpeed() {
        return simulationSpeed;
    }
    
    public void setSimulationSpeed(Integer simulationSpeed) {
        this.simulationSpeed = simulationSpeed;
    }
    
    public Boolean getRealTime() {
        return realTime;
    }
    
    public void setRealTime(Boolean realTime) {
        this.realTime = realTime;
    }
    
    public Double getCurrentPressure() {
        return currentPressure;
    }
    
    public void setCurrentPressure(Double currentPressure) {
        this.currentPressure = currentPressure;
    }
    
    public Double getCurrentTemperature() {
        return currentTemperature;
    }
    
    public void setCurrentTemperature(Double currentTemperature) {
        this.currentTemperature = currentTemperature;
    }
    
    public Double getHealthStatus() {
        return healthStatus;
    }
    
    public void setHealthStatus(Double healthStatus) {
        this.healthStatus = healthStatus;
    }
    
    public String getFaultStatus() {
        return faultStatus;
    }
    
    public void setFaultStatus(String faultStatus) {
        this.faultStatus = faultStatus;
    }
}
