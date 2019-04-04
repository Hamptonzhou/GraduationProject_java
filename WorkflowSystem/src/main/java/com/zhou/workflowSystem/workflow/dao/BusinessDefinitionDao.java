package com.zhou.workflowSystem.workflow.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zhou.workflowSystem.workflow.entity.BusinessDefinition;

/**
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年3月7日
 * @Version:1.1.0
 */
@Repository
public interface BusinessDefinitionDao extends JpaRepository<BusinessDefinition, Integer> {
    
}
