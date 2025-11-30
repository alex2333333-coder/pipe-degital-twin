package com.example.backend.service;

import com.example.backend.entity.SimulationHistory;
import com.example.backend.entity.User;
import com.example.backend.repository.SimulationHistoryRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SimulationHistoryService {

    @Autowired
    private SimulationHistoryRepository simulationHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    // 保存模拟历史数据
    public SimulationHistory saveSimulationHistory(Long userId, SimulationHistory history) {
        // 根据用户ID获取用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // 设置用户和创建时间
        history.setUser(user);
        if (history.getTimestamp() == null) {
            history.setTimestamp(new Date());
        }
        
        // 保存历史数据
        return simulationHistoryRepository.save(history);
    }

    // 根据用户ID查询模拟历史数据
    public List<SimulationHistory> getSimulationHistoryByUserId(Long userId) {
        return simulationHistoryRepository.findByUserId(userId);
    }

    // 根据用户ID和时间范围查询模拟历史数据
    public List<SimulationHistory> getSimulationHistoryByUserIdAndTimeRange(Long userId, Date startTime, Date endTime) {
        return simulationHistoryRepository.findByUserIdAndTimestampBetween(userId, startTime, endTime);
    }

    // 查询最新的N条模拟历史数据
    public List<SimulationHistory> getLatestSimulationHistory(Long userId) {
        return simulationHistoryRepository.findTop100ByUserIdOrderByTimestampDesc(userId);
    }

    // 根据ID查询模拟历史数据
    public Optional<SimulationHistory> getSimulationHistoryById(Long id) {
        return simulationHistoryRepository.findById(id);
    }

    // 删除模拟历史数据
    public void deleteSimulationHistory(Long id) {
        simulationHistoryRepository.deleteById(id);
    }

    // 清空用户的模拟历史数据
    public void clearSimulationHistoryByUserId(Long userId) {
        List<SimulationHistory> histories = simulationHistoryRepository.findByUserId(userId);
        simulationHistoryRepository.deleteAll(histories);
    }
}