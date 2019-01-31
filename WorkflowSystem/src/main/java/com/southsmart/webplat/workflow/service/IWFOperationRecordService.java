package com.southsmart.webplat.workflow.service;

import com.southsmart.webplat.workflow.entity.OperationRecord;

public interface IWFOperationRecordService {
    
    static final String SERVER_BEAN_NAME = "wfOperationRecordService";
    
    OperationRecord saveRecord(OperationRecord record);
}
