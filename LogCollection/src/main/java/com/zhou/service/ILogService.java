package com.zhou.service;

import com.zhou.entity.LogInfo;

/**
 * 日志服务接口
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月14日
 * @Version:1.1.0
 */
public interface ILogService {
    
    /**
     * 将日志消息写到控制台和MongoDB
     * 
     * @param logInfo
     * @Description:
     */
    public void writeInvoke(LogInfo logInfo);
}
