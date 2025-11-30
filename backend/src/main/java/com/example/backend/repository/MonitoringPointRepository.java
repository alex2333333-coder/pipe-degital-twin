package com.example.backend.repository;

import com.example.backend.entity.MonitoringPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonitoringPointRepository extends JpaRepository<MonitoringPoint, Long> {
}
