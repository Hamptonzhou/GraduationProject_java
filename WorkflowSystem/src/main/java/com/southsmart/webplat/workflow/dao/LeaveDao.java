package com.southsmart.webplat.workflow.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.southsmart.webplat.workflow.entity.Leave;

@Repository("leaveDao")
public interface LeaveDao extends JpaRepository<Leave, Long> {
    
}
