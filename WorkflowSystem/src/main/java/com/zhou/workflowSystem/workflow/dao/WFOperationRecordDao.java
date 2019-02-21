package com.zhou.workflowSystem.workflow.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zhou.workflowSystem.workflow.entity.OperationRecord;

@Repository(WFOperationRecordDao.DAO_BEAN_NAME)
public interface WFOperationRecordDao extends JpaRepository<OperationRecord, String> {
    
    static final String DAO_BEAN_NAME = "wfOperationRecordDao";
}
