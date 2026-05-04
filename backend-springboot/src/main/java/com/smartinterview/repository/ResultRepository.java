package com.smartinterview.repository;

import com.smartinterview.model.ResultRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResultRepository extends JpaRepository<ResultRecord, Long> {
    List<ResultRecord> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<ResultRecord> findByUserIdOrderByCreatedAtAsc(Long userId);
}
