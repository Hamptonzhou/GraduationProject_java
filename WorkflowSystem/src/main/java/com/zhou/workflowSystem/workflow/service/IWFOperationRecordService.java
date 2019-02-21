package com.zhou.workflowSystem.workflow.service;

import com.zhou.workflowSystem.workflow.entity.OperationRecord;

public interface IWFOperationRecordService {
    
    static final String SERVER_BEAN_NAME = "wfOperationRecordService";
    
    OperationRecord saveRecord(OperationRecord record);
}
