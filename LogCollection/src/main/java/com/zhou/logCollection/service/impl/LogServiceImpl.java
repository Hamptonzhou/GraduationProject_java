package com.zhou.logCollection.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.zhou.logCollection.entity.LogInfo;
import com.zhou.logCollection.service.ILogService;

import lombok.extern.slf4j.Slf4j;

/**
 * 插入日志信息到mongodb
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月15日
 * @Version:1.1.0
 */
@Service
@Slf4j
public class LogServiceImpl implements ILogService {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Override
    public void writeInvoke(LogInfo logInfo) {
        mongoTemplate.save(logInfo);
        log.info(logInfo.getMethod());
        log.info(logInfo.getDescription());
    }
}
