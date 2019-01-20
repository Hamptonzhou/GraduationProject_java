package com.zhou.service;

import com.zhou.utils.PageQueryData;

/**
 * 查询日志的服务接口
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月15日
 * @Version:1.1.0
 */
public interface IOperationLogAnalyseService<T> {
    /**
     * 分页查询对象
     * 
     * @param pageQueryData
     * @param keyword 查询的关键字
     * @param startDate 日志的开始时间
     * @param endDate 日志的结束时间
     * @Description:
     */
    public void find(PageQueryData<T> pageQueryData, String keyword, String startDate, String endDate);
    
}
