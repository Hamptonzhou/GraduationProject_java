package com.zhou.workflowSystem.workflow.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zhou.workflowSystem.workflow.entity.Leave;

@Repository
public interface LeaveDao extends JpaRepository<Leave, Long> {
    
}
