package com.example.backend.controller;

import com.example.backend.entity.SimulationHistory;
import com.example.backend.entity.User;
import com.example.backend.service.SimulationHistoryService;
import com.example.backend.service.UserService;
import com.example.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/simulation-history")
public class SimulationHistoryController {

    @Autowired
    private SimulationHistoryService simulationHistoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // 从请求头中获取当前用户ID
    private Long getCurrentUserId(HttpServletRequest request) {
        // 从请求头中获取Authorization令牌
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            String username = jwtUtil.validateTokenAndExtractUsername(token);
            if (username != null) {
                User user = userService.getUserByUsername(username);
                if (user != null) {
                    return user.getId();
                }
            }
        }
        throw new RuntimeException("User not authenticated");
    }

    // 保存模拟历史数据
    @PostMapping
    public ResponseEntity<SimulationHistory> saveSimulationHistory(@RequestBody SimulationHistory history, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        SimulationHistory savedHistory = simulationHistoryService.saveSimulationHistory(userId, history);
        return ResponseEntity.ok(savedHistory);
    }

    // 获取当前用户的模拟历史数据
    @GetMapping
    public ResponseEntity<List<SimulationHistory>> getSimulationHistory(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        List<SimulationHistory> historyList = simulationHistoryService.getSimulationHistoryByUserId(userId);
        return ResponseEntity.ok(historyList);
    }

    // 获取当前用户的最新模拟历史数据
    @GetMapping("/latest")
    public ResponseEntity<List<SimulationHistory>> getLatestSimulationHistory(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        List<SimulationHistory> historyList = simulationHistoryService.getLatestSimulationHistory(userId);
        return ResponseEntity.ok(historyList);
    }

    // 根据时间范围获取当前用户的模拟历史数据
    @GetMapping("/by-time-range")
    public ResponseEntity<List<SimulationHistory>> getSimulationHistoryByTimeRange(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            HttpServletRequest request) {
        
        Long userId = getCurrentUserId(request);
        
        try {
            // 转换时间参数
            Date startDate = null;
            Date endDate = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            if (startTime != null && !startTime.isEmpty()) {
                startDate = sdf.parse(startTime);
            }
            
            if (endTime != null && !endTime.isEmpty()) {
                endDate = sdf.parse(endTime);
            }
            
            List<SimulationHistory> historyList = simulationHistoryService.getSimulationHistoryByUserIdAndTimeRange(userId, startDate, endDate);
            return ResponseEntity.ok(historyList);
        } catch (ParseException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 清空当前用户的模拟历史数据
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearSimulationHistory(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        simulationHistoryService.clearSimulationHistoryByUserId(userId);
        return ResponseEntity.ok().build();
    }
}