package com.example.backend.repository;

import com.example.backend.entity.MonitoringData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MonitoringDataRepository extends JpaRepository<MonitoringData, Long> {
    
    // 按时间范围查询历史数据
    List<MonitoringData> findByDataTimeBetween(Date startTime, Date endTime);
    
    // 按监测点查询历史数据
    List<MonitoringData> findByMonitoringPointId(Long monitoringPointId);
    
    // 按时间范围和监测点查询历史数据
    List<MonitoringData> findByMonitoringPointIdAndDataTimeBetween(Long monitoringPointId, Date startTime, Date endTime);
    
    // 按时间范围查询历史数据（分页）
    Page<MonitoringData> findByDataTimeBetween(Date startTime, Date endTime, Pageable pageable);
    
    // 按监测点查询历史数据（分页）
    Page<MonitoringData> findByMonitoringPointId(Long monitoringPointId, Pageable pageable);
    
    // 按时间范围和监测点查询历史数据（分页）
    Page<MonitoringData> findByMonitoringPointIdAndDataTimeBetween(Long monitoringPointId, Date startTime, Date endTime, Pageable pageable);
    
    // 查询所有历史数据（分页）
    Page<MonitoringData> findAll(Pageable pageable);
}
