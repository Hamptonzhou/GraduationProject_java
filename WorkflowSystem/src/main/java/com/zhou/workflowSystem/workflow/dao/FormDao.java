package com.zhou.workflowSystem.workflow.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zhou.workflowSystem.workflow.entity.BusinessFormData;

/**
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年3月5日
 * @Version:1.1.0
 */
@Repository
public interface FormDao extends JpaRepository<BusinessFormData, Integer> {
    
}
