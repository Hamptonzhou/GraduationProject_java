package com.southsmart.webplat.workflow.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.southsmart.webplat.workflow.entity.OperationRecord;

@Repository(WFOperationRecordDao.DAO_BEAN_NAME)
public interface WFOperationRecordDao extends JpaRepository<OperationRecord, String> {
    
    static final String DAO_BEAN_NAME = "wfOperationRecordDao";
}
