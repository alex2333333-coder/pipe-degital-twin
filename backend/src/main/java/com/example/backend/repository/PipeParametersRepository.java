package com.example.backend.repository;

import com.example.backend.entity.PipeParameters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PipeParametersRepository extends JpaRepository<PipeParameters, Long> {
}
