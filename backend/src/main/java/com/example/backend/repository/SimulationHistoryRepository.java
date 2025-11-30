package com.example.backend.repository;

import com.example.backend.entity.SimulationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimulationHistoryRepository extends JpaRepository<SimulationHistory, Long> {
    
    // 根据用户ID查询模拟历史数据
    List<SimulationHistory> findByUserId(Long userId);
    
    // 根据用户ID和时间范围查询模拟历史数据
    List<SimulationHistory> findByUserIdAndTimestampBetween(Long userId, java.util.Date startTime, java.util.Date endTime);
    
    // 根据用户ID查询最新的N条模拟历史数据
    List<SimulationHistory> findTop100ByUserIdOrderByTimestampDesc(Long userId);
}