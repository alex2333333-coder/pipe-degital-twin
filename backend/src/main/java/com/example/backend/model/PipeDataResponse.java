package com.example.backend.model;

import java.util.List;

public class PipeDataResponse {
    private Double totalLength;
    private Integer monitoringPoints;
    private Double currentPressure;
    private Double currentFlow;
    private List<MonitoringDataDto> monitoringData;

    public PipeDataResponse() {
    }

    public PipeDataResponse(Double totalLength, Integer monitoringPoints, Double currentPressure, Double currentFlow, List<MonitoringDataDto> monitoringData) {
        this.totalLength = totalLength;
        this.monitoringPoints = monitoringPoints;
        this.currentPressure = currentPressure;
        this.currentFlow = currentFlow;
        this.monitoringData = monitoringData;
    }

    // getter and setter methods
    public Double getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(Double totalLength) {
        this.totalLength = totalLength;
    }

    public Integer getMonitoringPoints() {
        return monitoringPoints;
    }

    public void setMonitoringPoints(Integer monitoringPoints) {
        this.monitoringPoints = monitoringPoints;
    }

    public Double getCurrentPressure() {
        return currentPressure;
    }

    public void setCurrentPressure(Double currentPressure) {
        this.currentPressure = currentPressure;
    }

    public Double getCurrentFlow() {
        return currentFlow;
    }

    public void setCurrentFlow(Double currentFlow) {
        this.currentFlow = currentFlow;
    }

    public List<MonitoringDataDto> getMonitoringData() {
        return monitoringData;
    }

    public void setMonitoringData(List<MonitoringDataDto> monitoringData) {
        this.monitoringData = monitoringData;
    }

    // MonitoringDataDto inner class
    public static class MonitoringDataDto {
        private String id;
        private String name;
        private Double pressure;
        private Double flow;
        private Double temperature;
        private String status;
        private String updateTime;

        public MonitoringDataDto() {
        }

        public MonitoringDataDto(String id, String name, Double pressure, Double flow, Double temperature, String status, String updateTime) {
            this.id = id;
            this.name = name;
            this.pressure = pressure;
            this.flow = flow;
            this.temperature = temperature;
            this.status = status;
            this.updateTime = updateTime;
        }

        // getter and setter methods
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getPressure() {
            return pressure;
        }

        public void setPressure(Double pressure) {
            this.pressure = pressure;
        }

        public Double getFlow() {
            return flow;
        }

        public void setFlow(Double flow) {
            this.flow = flow;
        }

        public Double getTemperature() {
            return temperature;
        }

        public void setTemperature(Double temperature) {
            this.temperature = temperature;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }
}