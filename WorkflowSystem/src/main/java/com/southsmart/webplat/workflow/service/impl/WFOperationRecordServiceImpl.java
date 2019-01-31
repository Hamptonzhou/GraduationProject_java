package com.southsmart.webplat.workflow.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.southsmart.webplat.workflow.dao.WFOperationRecordDao;
import com.southsmart.webplat.workflow.entity.OperationRecord;
import com.southsmart.webplat.workflow.service.IWFOperationRecordService;

@Service(IWFOperationRecordService.SERVER_BEAN_NAME)
public class WFOperationRecordServiceImpl implements IWFOperationRecordService {
    
    @Resource(name = WFOperationRecordDao.DAO_BEAN_NAME)
    WFOperationRecordDao wfOperationRecordDao;
    
    @Override
    public OperationRecord saveRecord(OperationRecord record) {
        return wfOperationRecordDao.save(record);
    }
}
