package com.example.backend.repository;

import com.example.backend.entity.Pipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PipeRepository extends JpaRepository<Pipe, Long> {
}
