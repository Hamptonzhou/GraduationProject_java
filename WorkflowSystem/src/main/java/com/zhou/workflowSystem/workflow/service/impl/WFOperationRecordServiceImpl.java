package com.zhou.workflowSystem.workflow.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zhou.workflowSystem.workflow.dao.WFOperationRecordDao;
import com.zhou.workflowSystem.workflow.entity.OperationRecord;
import com.zhou.workflowSystem.workflow.service.IWFOperationRecordService;

@Service(IWFOperationRecordService.SERVER_BEAN_NAME)
public class WFOperationRecordServiceImpl implements IWFOperationRecordService {
    
    @Resource(name = WFOperationRecordDao.DAO_BEAN_NAME)
    WFOperationRecordDao wfOperationRecordDao;
    
    @Override
    public OperationRecord saveRecord(OperationRecord record) {
        return wfOperationRecordDao.save(record);
    }
}
